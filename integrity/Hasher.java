package integrity;

import util.Util;

import java.io.*;
import java.security.MessageDigest;
import java.util.Scanner;

public class Hasher {
    public static String getHash(String input, String algorithm) throws Exception {
        byte[] inputBA = input.getBytes();

        MessageDigest hasher = MessageDigest.getInstance(algorithm);
        hasher.update(inputBA);


        return Util.byteArrayToHexString(hasher.digest(), "");
    }



    public static String getHashFile(String filename, String algorithm) throws Exception {
        MessageDigest hasher = MessageDigest.getInstance(algorithm);

        FileInputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[1024];

        int in;
        while ((in = fis.read(buffer)) != -1) {
            hasher.update(buffer, 0, in);
        }

        fis.close();

        return Util.byteArrayToHexString(hasher.digest(), "");
    }



    public static void generateIntegrityCheckerFile(String rutaCarpeta, String rutaArchivo) throws Exception{
        File carpeta = new File(rutaCarpeta);

        // Validaciones
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            throw new IllegalArgumentException("La carpeta no existe.");
        }

        File[] files = carpeta.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("La carpeta está vacía.");
        }

        // Escribir los hashes sobre el archivo
        FileWriter writer = new FileWriter(rutaArchivo, false); // false = sobrescribir

        for (File file : files) {
            if (file.isFile()) {
                String hash = getHashFile(file.getAbsolutePath(), "SHA-256");
                String line = hash + " *" + file.getName() + System.lineSeparator();
                writer.write(line);
            }
        }
        writer.close();
    }

    public static void checkIntegrityFile(String rutaArchivo, String rutaCarpeta) throws Exception {
        File integrityFile = new File(rutaArchivo);
        if (!integrityFile.exists()) {
            throw new Exception("El archivo de integridad no existe.");
        }

        BufferedReader reader = new BufferedReader(new FileReader(integrityFile));
        String line;

        int notFoundCount = 0;
        int checksumMismatchCount = 0;
        int formattingErrorsCount = 0;
        int okCount = 0;

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(" ", 2);
            if (parts.length < 2) {
                System.out.println("Formato inválido en línea: " + line);
                formattingErrorsCount++;
                continue;
            }

            String expectedHash = parts[0];
            String filename = parts[1].substring(1); // Quita el '*' al inicio
            File file = new File(rutaCarpeta + "/" + filename);

            if (!file.exists()) {
                System.out.println(filename + ": FAILED open or read");
                notFoundCount++;
            } else {
                String actualHash = getHashFile(file.getPath(), "SHA-256");
                if (expectedHash.equalsIgnoreCase(actualHash)) {
                    System.out.println(filename + ": OK");
                    okCount++;
                } else {
                    System.out.println(filename + ": FAILED");
                    checksumMismatchCount++;
                }
            }
        }
        reader.close();

        //resumen
        System.out.println();

        if (notFoundCount == 1) {
            System.out.println("shasum: WARNING: 1 listed file could not be read");
        } else if (notFoundCount > 1) {
            System.out.println("shasum: WARNING: " + notFoundCount + " listed files could not be read");
        }

        if (checksumMismatchCount == 1) {
            System.out.println("shasum: WARNING: 1 computed checksum did NOT match");
        } else if (checksumMismatchCount > 1) {
            System.out.println("shasum: WARNING: " + checksumMismatchCount + " computed checksums did NOT match");
        }

        if (formattingErrorsCount == 1) {
            System.out.println("shasum: WARNING: 1 line is improperly formatted");
        } else if (formattingErrorsCount > 1) {
            System.out.println("shasum: WARNING: " + formattingErrorsCount + " lines are improperly formatted");
        }

        if (notFoundCount == 0 && checksumMismatchCount == 0 && formattingErrorsCount == 0) {
            System.out.println("Todos los archivos fueron verificados exitosamente (" + okCount + " archivos OK).");
        }
    }
}

