package util;

import java.io.*;
import java.security.Key;
import java.security.MessageDigest;

public class Util {

    public static String byteArrayToHexString(byte[] bytes, String separator) {
        String result = "";

        for (int i=0; i<bytes.length; i++) {
            result += String.format("%02x", bytes[i]) + separator;
        }

        return result.toString();
    }

    public static String keyToPem(Key key, boolean isPrivate) {
        StringBuilder pemKey = new StringBuilder();

        // Agregar el encabezado correspondiente
        if (isPrivate) {
            pemKey.append("-----BEGIN PRIVATE KEY-----\n");
        } else {
            pemKey.append("-----BEGIN PUBLIC KEY-----\n");
        }

        // Codificar la llave en Base64
        String encodedKey = Base64.encode(key.getEncoded());

        // Formatear la llave en líneas de 64 caracteres
        for (int i = 0; i < encodedKey.length(); i += 64) {
            int endIndex = Math.min(i + 64, encodedKey.length());
            pemKey.append(encodedKey.substring(i, endIndex)).append("\n");
        }

        // Agregar el pie de página correspondiente
        if (isPrivate) {
            pemKey.append("-----END PRIVATE KEY-----");
        } else {
            pemKey.append("-----END PUBLIC KEY-----");
        }

        return pemKey.toString();
    }

    public static void saveObject(Object o, String fileName) throws
            IOException {
        FileOutputStream fileOut;
        ObjectOutputStream out;


        fileOut = new FileOutputStream(fileName);
        out = new ObjectOutputStream(fileOut);


        out.writeObject(o);


        out.flush();
        out.close();
    }

    public static Object loadObject (String fileName) throws
            IOException,
            ClassNotFoundException,
            InterruptedException {
        FileInputStream fileIn;
        ObjectInputStream in;


        fileIn = new FileInputStream(fileName);
        in = new ObjectInputStream(fileIn);

        Thread.sleep(100);

        Object o = in.readObject();


        fileIn.close();
        in.close();


        return o;
    }

    public static byte[] objectToByteArray(Object o) throws
            IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(o);
        out.close();
        byte[] buffer = bos.toByteArray();


        return buffer;
    }

    public static Object byteArrayToObject(byte[] byteArray) throws
            IOException,
            ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArray));
        Object o = in.readObject();
        in.close();
        return o;
    }
    
    
    // ====================== lab03 ==============================
    public static byte[][] split(byte[] input, int blockSize) {
        // Calcular el número de bloques completos
        int numFullBlocks = input.length / blockSize;

        // Calcular si hay un bloque parcial al final
        int remainingBytes = input.length % blockSize;

        // Determinar el número total de bloques
        int totalBlocks = remainingBytes > 0 ? numFullBlocks + 1 : numFullBlocks;

        // Crear la matriz para almacenar los bloques
        byte[][] result = new byte[totalBlocks][];

        // Crear y llenar los bloques completos
        for (int i = 0; i < numFullBlocks; i++) {
            result[i] = new byte[blockSize];
            System.arraycopy(input, i * blockSize, result[i], 0, blockSize);
        }

        // Crear y llenar el último bloque si es necesario
        if (remainingBytes > 0) {
            result[numFullBlocks] = new byte[remainingBytes];
            System.arraycopy(input, numFullBlocks * blockSize, result[numFullBlocks], 0, remainingBytes);
        }

        return result;
    }

    public static byte[] join(byte[][] input) {
        // Calcular el tamaño total del array resultante
        int totalLength = 0;
        for (byte[] block : input) {
            totalLength += block.length;
        }

        // Crear el array resultante
        byte[] result = new byte[totalLength];

        // Copiar cada bloque al array resultante
        int currentPosition = 0;
        for (byte[] block : input) {
            System.arraycopy(block, 0, result, currentPosition, block.length);
            currentPosition += block.length;
        }

        return result;
    }


    public static byte[] readFileBytes(String filePath) throws IOException {
        return java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath));
    }
    public static void writeFileBytes(String filePath, byte[] data) throws IOException {
        java.nio.file.Files.write(java.nio.file.Paths.get(filePath), data);
    }


}
