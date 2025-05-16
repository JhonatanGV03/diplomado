package publickeycipher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import util.Util;

public class RSAtester02 {
    public static void main(String[] args) throws Exception {
        testMaxSizeForKeyLength(1024);
        testMaxSizeForKeyLength(2048);
        testMaxSizeForKeyLength(3072);
        testMaxSizeForKeyLength(4096);
    }

    private static void testMaxSizeForKeyLength(int keySize) throws Exception {
        System.out.println("Testing maximum data size for RSA key length: " + keySize + " bits");

        // Generar par de llaves RSA
        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Crear cifrador
        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);

        // Probar diferentes tamaños de datos
        int maxSize = findMaxSize(cipher, keyPair, keySize / 8 - 11);  // Estimación inicial basada en PKCS#1

        System.out.println("Maximum data size for " + keySize + "-bit RSA key: " + maxSize + " bytes");
        System.out.println("----------------------------------------------------");
    }

    private static int findMaxSize(PublicKeyCipher cipher, KeyPair keyPair, int estimatedMax) throws Exception {
        // Búsqueda binaria para encontrar el tamaño máximo
        int low = 1;
        int high = estimatedMax + 20;  // Un poco más que la estimación para estar seguros

        while (low <= high) {
            int mid = (low + high) / 2;
            String testData = generateRandomString(mid);

            try {
                byte[] encrypted = cipher.encryptMessage(testData, keyPair.getPublic());
                String decrypted = cipher.decryptMessage(encrypted, keyPair.getPrivate());

                if (decrypted.equals(testData)) {
                    low = mid + 1;  // Intentar con un tamaño mayor
                } else {
                    high = mid - 1;  // El tamaño actual no funciona correctamente
                }
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                high = mid - 1;  // El tamaño es demasiado grande
            }
        }

        return high;  // El último tamaño que funcionó
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
