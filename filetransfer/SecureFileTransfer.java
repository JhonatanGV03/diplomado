package filetransfer;

import crypto.CryptoUtils;
import integrity.Hasher;
import util.Base64;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SecureFileTransfer {

    // Método para enviar un archivo encriptado
    public static void sendEncryptedFile(Socket socket, File file, String destinationDirectory) throws Exception {
        System.out.println("Enviando archivo encriptado: " + file.getName());

        // 1. Generar clave DES
        SecretKey secretKey = CryptoUtils.generateDESKey();

        // 2. Crear directorio para archivos encriptados si no existe
        File encryptedDir = new File("encrypted_files");
        if (!encryptedDir.exists()) {
            encryptedDir.mkdirs();
        }

        // 3. Encriptar el archivo
        File encryptedFile = new File(encryptedDir, file.getName() + ".encrypted");

        byte[] encryptedBytes;
        try (FileInputStream fis = new FileInputStream(file)) {
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey);
            byte[] fileBytes = fis.readAllBytes();
            encryptedBytes = cipher.doFinal(fileBytes);
        }

        String base64Encoded = Base64.encode(encryptedBytes);
        try (FileWriter writer = new FileWriter(encryptedFile)) {
            writer.write(base64Encoded);
        }

        File keyFile = new File(encryptedDir, file.getName() + ".key");
        CryptoUtils.saveKey(secretKey, keyFile.getPath());

        File hashFile = new File(encryptedDir, file.getName() + ".hash");
        String fileHash = Hasher.getHashFile(encryptedFile.getAbsolutePath(), "SHA-256");
        try (FileWriter writer = new FileWriter(hashFile)) {
            writer.write(fileHash + " *" + encryptedFile.getName());
        }

        System.out.println("Archivos generados:");
        System.out.println("- Archivo encriptado en Base64: " + encryptedFile.getAbsolutePath());
        System.out.println("- Archivo de clave: " + keyFile.getAbsolutePath());
        System.out.println("- Archivo de hash: " + hashFile.getAbsolutePath());

        // 6. Enviar los datos
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        // Enviar nombre del archivo original (puede quedarse como writeUTF)
        dos.writeUTF(file.getName());

        // Enviar archivo en Base64 (longitud + bytes)
        byte[] base64Bytes = base64Encoded.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(base64Bytes.length);
        dos.write(base64Bytes);

        // Enviar la clave
        System.out.println("Enviando clave secreta...");
        byte[] serializedKey = serializeKey(secretKey);
        String keyBase64 = Base64.encode(serializedKey);
        byte[] keyBytes = keyBase64.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(keyBytes.length);
        dos.write(keyBytes);

        // Enviar hash
        System.out.println("Enviando hash...");
        byte[] hashBytes = fileHash.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(hashBytes.length);
        dos.write(hashBytes);

        System.out.println("Archivo enviado correctamente: " + file.getName());

        // Confirmación
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        boolean integrityVerified = dis.readBoolean();
        if (integrityVerified) {
            System.out.println("El receptor verificó la integridad del archivo correctamente.");
        } else {
            System.out.println("¡ADVERTENCIA! El receptor reportó problemas de integridad con el archivo.");
        }
    }

    // Método para recibir un archivo encriptado
    public static File receiveEncryptedFile(Socket socket, String destinationDirectory) throws Exception {
        System.out.println("Recibiendo archivo encriptado...");

        File receivedDir = new File("received_files");
        if (!receivedDir.exists()) receivedDir.mkdirs();

        File destDir = new File(destinationDirectory);
        if (!destDir.exists()) destDir.mkdirs();

        DataInputStream dis = new DataInputStream(socket.getInputStream());

        // Nombre del archivo original
        String fileName = dis.readUTF();
        System.out.println("Recibiendo archivo: " + fileName);

        // Recibir archivo en Base64
        int fileLen = dis.readInt();
        byte[] base64Bytes = new byte[fileLen];
        dis.readFully(base64Bytes);
        String base64Encoded = new String(base64Bytes, StandardCharsets.UTF_8);

        File encryptedFile = new File(receivedDir, fileName + ".encrypted");
        try (FileWriter writer = new FileWriter(encryptedFile)) {
            writer.write(base64Encoded);
        }

        // Recibir clave
        int keyLen = dis.readInt();
        byte[] keyBytes = new byte[keyLen];
        dis.readFully(keyBytes);
        String keyBase64 = new String(keyBytes, StandardCharsets.UTF_8);
        byte[] serializedKey = Base64.decode(keyBase64);
        SecretKey secretKey = deserializeKey(serializedKey);

        File keyFile = new File(receivedDir, fileName + ".key");
        CryptoUtils.saveKey(secretKey, keyFile.getPath());

        // Recibir hash
        int hashLen = dis.readInt();
        byte[] hashBytes = new byte[hashLen];
        dis.readFully(hashBytes);
        String receivedHash = new String(hashBytes, StandardCharsets.UTF_8);

        File hashFile = new File(receivedDir, fileName + ".hash");
        try (FileWriter writer = new FileWriter(hashFile)) {
            writer.write(receivedHash + " *" + encryptedFile.getName());
        }

        System.out.println("Archivos recibidos:");
        System.out.println("- Archivo encriptado en Base64: " + encryptedFile.getAbsolutePath());
        System.out.println("- Archivo de clave: " + keyFile.getAbsolutePath());
        System.out.println("- Archivo de hash: " + hashFile.getAbsolutePath());

        // Verificación de integridad
        String actualHash = Hasher.getHashFile(encryptedFile.getAbsolutePath(), "SHA-256");
        boolean integrityVerified = receivedHash.equals(actualHash);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeBoolean(integrityVerified);
        dos.flush();

        // Desencriptar si se verificó
        File outputFile = null;
        if (integrityVerified) {
            System.out.println("Integridad verificada. Desencriptando archivo...");
            outputFile = new File(destinationDirectory, fileName);
            byte[] encryptedBytes = Base64.decode(base64Encoded);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DES/ECB/PKCS5Padding");
                cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey);
                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                fos.write(decryptedBytes);
            }

            System.out.println("Archivo recibido y desencriptado correctamente: " + outputFile.getName());
        } else {
            System.out.println("¡ERROR! La integridad del archivo no pudo ser verificada.");
        }

        return outputFile;
    }

    private static byte[] serializeKey(SecretKey key) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(key);
        oos.close();
        return baos.toByteArray();
    }

    private static SecretKey deserializeKey(byte[] keyBytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(keyBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        SecretKey key = (SecretKey) ois.readObject();
        ois.close();
        return key;
    }
}
