package publickeycipher;

import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

public class RSA {
    public static void main(String[] args) throws Exception {
        String inputFilePath = "Proyecto.pdf";
        String outputFilePath = "ProyectoReconstruido.pdf";

        // 1. Generar par de llaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 2. Crear instancia de cifrado
        PublicKeyCipher cipher = new PublicKeyCipher("RSA/ECB/PKCS1Padding");

        // 3. Leer archivo original como bytes
        byte[] originalData = Util.readFileBytes(inputFilePath);
        System.out.println("Original data size: " + originalData.length + " bytes");

        // 4. Dividir en bloques de 245 bytes (2048 bits - 11 para PKCS1)
        int keySizeBytes = 2048 / 8;
        int maxBlockSize = keySizeBytes - 11;
        byte[][] blocks = Util.split(originalData, maxBlockSize);
        System.out.println("Data split into " + blocks.length + " blocks");

        // 5. Encriptar cada bloque
        ArrayList<byte[]> encryptedBlocks = new ArrayList<>();
        for (int i = 0; i < blocks.length; i++) {
            byte[] encrypted = cipher.encryptFragment(blocks[i], publicKey);
            encryptedBlocks.add(encrypted);
            System.out.println("Block " + i + " encrypted: " + maxBlockSize + " bytes");
        }

        // 6. Desencriptar cada bloque
        ArrayList<byte[]> decryptedBlocks = new ArrayList<>();
        for (int i = 0; i < encryptedBlocks.size(); i++) {
            byte[] decrypted = cipher.decryptFragment(encryptedBlocks.get(i), privateKey);
            decryptedBlocks.add(decrypted);
            System.out.println("Block " + i + " decrypted: " + decrypted.length + " bytes");
        }

        // 7. Reconstruir datos
        byte[] recoveredData = Util.join(decryptedBlocks.toArray(new byte[0][]));
        System.out.println("Decrypted data size: " + recoveredData.length + " bytes");

        // 8. Verificar integridad
        boolean sameSize = originalData.length == recoveredData.length;
        boolean sameContent = Arrays.equals(originalData, recoveredData);
        System.out.println("Data integrity check: " + sameSize);
        System.out.println("Original data equals decrypted data: " + sameContent);

        // 9. Guardar el archivo recuperado
        Util.writeFileBytes(outputFilePath, recoveredData);
    }
}