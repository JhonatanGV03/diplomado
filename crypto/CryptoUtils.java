package crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;

public class CryptoUtils {

    public static SecretKey generateDESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        return keyGen.generateKey();
    }

    public static void saveKey(SecretKey key, String filename) throws Exception {
        byte[] encoded = key.getEncoded();
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(encoded);
        }
    }

    public static SecretKey loadKey(String filename) throws Exception {
        byte[] encoded = Files.readAllBytes(new File(filename).toPath());
        return new SecretKeySpec(encoded, "DES");
    }


    public static void encryptTextFile(File inputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        File outputFile = new File(inputFile.getAbsolutePath() + ".encrypted");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                byte[] encrypted = cipher.doFinal(line.getBytes("UTF-8"));
                String encoded = util.Base64.encode(encrypted);
                writer.println(encoded);
            }
        }

        System.out.println("Archivo encriptado: " + outputFile.getName());
    }


    public static void decryptTextFile(File encryptedFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        String originalName = encryptedFile.getName().replace(".encrypted", "");
        int dot = originalName.lastIndexOf(".");
        String outputName = originalName.substring(0, dot) + ".plain" + originalName.substring(dot);
        File outputFile = new File(encryptedFile.getParent(), outputName);

        try (BufferedReader reader = new BufferedReader(new FileReader(encryptedFile));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                byte[] decoded = util.Base64.decode(line);
                String decrypted = new String(cipher.doFinal(decoded), "UTF-8");
                writer.println(decrypted);
            }
        }

        System.out.println("Archivo desencriptado: " + outputFile.getName());
    }

    public static void encryptFile(File inputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        File outputFile = new File(inputFile.getAbsolutePath() + ".encrypted");

        try (FileInputStream fis = new FileInputStream(inputFile);
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            byte[] buffer = new byte[64]; // leer bloques pequeños
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] toEncrypt = (bytesRead == buffer.length) ? buffer : java.util.Arrays.copyOf(buffer, bytesRead);
                byte[] encrypted = cipher.doFinal(toEncrypt);
                String encoded = util.Base64.encode(encrypted);
                writer.write(encoded);
                writer.newLine(); // cada bloque en una línea
            }
        }

        System.out.println("Archivo binario encriptado: " + outputFile.getName());
    }

    public static void decryptFile(File encryptedFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        String originalName = encryptedFile.getName().replace(".encrypted", "");
        int dot = originalName.lastIndexOf(".");
        String outputName = originalName.substring(0, dot) + ".plain" + originalName.substring(dot);
        File outputFile = new File(encryptedFile.getParent(), outputName);

        try (BufferedReader reader = new BufferedReader(new FileReader(encryptedFile));
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            String line;
            while ((line = reader.readLine()) != null) {
                byte[] encryptedBlock = util.Base64.decode(line);
                byte[] decrypted = cipher.doFinal(encryptedBlock);
                fos.write(decrypted);
            }
        }

        System.out.println("Archivo binario desencriptado: " + outputFile.getName());
    }

    // ==================================================

    // Nuevo método para encriptar un archivo para envío por red
    public static File encryptFileForTransfer(File inputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        File outputFile = new File(inputFile.getAbsolutePath() + ".encrypted");

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    fos.write(output);
                }
            }

            byte[] finalOutput = cipher.doFinal();
            if (finalOutput != null) {
                fos.write(finalOutput);
            }
        }

        return outputFile;
    }

    // Nuevo método para desencriptar un archivo recibido por red
    public static File decryptFileFromTransfer(File encryptedFile, SecretKey key, String outputDirectory) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        String originalName = encryptedFile.getName().replace(".encrypted", "");
        File outputFile = new File(outputDirectory, originalName);

        try (FileInputStream fis = new FileInputStream(encryptedFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    fos.write(output);
                }
            }

            byte[] finalOutput = cipher.doFinal();
            if (finalOutput != null) {
                fos.write(finalOutput);
            }
        }

        return outputFile;
    }

    // Método para serializar una clave para enviarla por la red
    public static byte[] serializeKey(SecretKey key) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(key);
        oos.close();
        return baos.toByteArray();
    }

    // Método para deserializar una clave recibida por la red
    public static SecretKey deserializeKey(byte[] keyBytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(keyBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        SecretKey key = (SecretKey) ois.readObject();
        ois.close();
        return key;
    }
}
