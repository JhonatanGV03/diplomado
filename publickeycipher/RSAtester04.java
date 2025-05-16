package publickeycipher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;
import java.util.Random;

import util.Util;

public class RSAtester04 {

    public static void main(String[] args) throws Exception {
        // Generar par de llaves RSA
        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(2048);  // Usamos una llave de 2048 bits
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);

        // Determinar el tamaño máximo de bloque para RSA 2048 bits (aproximadamente 245 bytes)
        int maxBlockSize = 245;

        // Generar datos de prueba más grandes que el límite (por ejemplo, 1000 bytes)
        String largeData = generateRandomString(1000);
        byte[] originalData = largeData.getBytes();

        System.out.println("Original data size: " + originalData.length + " bytes");

        // Dividir los datos en bloques del tamaño máximo permitido
        byte[][] blocks = Util.split(originalData, maxBlockSize);

        System.out.println("Data split into " + blocks.length + " blocks");

        // Cifrar cada bloque
        byte[][] encryptedBlocks = new byte[blocks.length][];
        for (int i = 0; i < blocks.length; i++) {
            encryptedBlocks[i] = cipher.encryptMessage(new String(blocks[i]), keyPair.getPublic());
            System.out.println("Block " + i + " encrypted: " + maxBlockSize + " bytes");
        }

        // Descifrar cada bloque
        byte[][] decryptedBlocks = new byte[encryptedBlocks.length][];
        for (int i = 0; i < encryptedBlocks.length; i++) {
            String decryptedString = cipher.decryptMessage(encryptedBlocks[i], keyPair.getPrivate());
            decryptedBlocks[i] = decryptedString.getBytes();
            System.out.println("Block " + i + " decrypted: " + decryptedBlocks[i].length + " bytes");
        }

        // Unir los bloques descifrados
        byte[] decryptedData = Util.join(decryptedBlocks);

        System.out.println("Decrypted data size: " + decryptedData.length + " bytes");
        System.out.println("Data integrity check: " + Arrays.equals(originalData, decryptedData));

        // Verificar que los datos originales y descifrados son iguales
        String decryptedString = new String(decryptedData);
        System.out.println("Original data equals decrypted data: " + largeData.equals(decryptedString));
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}
