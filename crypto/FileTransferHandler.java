package crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Arrays;

public class FileTransferHandler {

    public static void sendEncryptedFile(File file, OutputStream os) throws Exception {
        // 1. Generar clave secreta DES
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        SecretKey secretKey = keyGen.generateKey();

        // 2. Inicializar cifrador DES en modo cifrado
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // 3. Cifrar archivo
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] encryptedBytes = cipher.doFinal(fileBytes);

        // 4. Calcular hash SHA-256 del archivo original
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(fileBytes);

        // 5. Enviar: nombre del archivo, longitud, archivo cifrado, clave secreta y hash
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF(file.getName());                  // Nombre del archivo
        dos.writeInt(encryptedBytes.length);           // Longitud del archivo cifrado
        dos.write(encryptedBytes);                     // Archivo cifrado
        dos.writeInt(secretKey.getEncoded().length);   // Longitud de la clave secreta
        dos.write(secretKey.getEncoded());             // Clave secreta DES
        dos.writeInt(hash.length);                     // Longitud del hash
        dos.write(hash);                               // Hash SHA-256
        dos.flush();
    }


    public static void receiveEncryptedFile(InputStream is, String destinationDir) throws Exception {
        DataInputStream dis = new DataInputStream(is);

        // 1. Leer datos recibidos
        String fileName = dis.readUTF();
        int encryptedLength = dis.readInt();
        byte[] encryptedBytes = new byte[encryptedLength];
        dis.readFully(encryptedBytes);

        int keyLength = dis.readInt();
        byte[] keyBytes = new byte[keyLength];
        dis.readFully(keyBytes);

        int hashLength = dis.readInt();
        byte[] receivedHash = new byte[hashLength];
        dis.readFully(receivedHash);

        // 2. Reconstruir clave secreta DES
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "DES");

        // 3. Inicializar cifrador DES en modo descifrado
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // 4. Descifrar archivo
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // 5. Verificar integridad
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] calculatedHash = digest.digest(decryptedBytes);

        if (!Arrays.equals(receivedHash, calculatedHash)) {
            throw new SecurityException("¡Integridad comprometida! El hash no coincide.");
        }

        // 6. Guardar archivo descifrado
        File outputFile = new File(destinationDir, fileName);
        Files.write(outputFile.toPath(), decryptedBytes);
    }

}
