package publickeycipher;

import java.util.Arrays;
import util.Util;

public class RSATester03 {
    public static void main(String[] args) {
        // Crear un array de bytes de prueba
        String testString = "Este es un mensaje de prueba para verificar los métodos split y join. " +
                "Michael Aristizabal Molina - Yhonatan Steven Gomez - 10 Mayo 2025";
        byte[] originalData = testString.getBytes();

        System.out.println("Original data length: " + originalData.length + " bytes");
        System.out.println("Original data: " + new String(originalData));

        // Probar con diferentes tamaños de bloque
        testSplitAndJoin(originalData, 16);
        testSplitAndJoin(originalData, 32);
        testSplitAndJoin(originalData, 64);
        testSplitAndJoin(originalData, 128);
    }

    private static void testSplitAndJoin(byte[] data, int blockSize) {
        System.out.println("\nTesting with block size: " + blockSize + " bytes");

        // Dividir los datos en bloques
        byte[][] blocks = Util.split(data, blockSize);

        System.out.println("Number of blocks: " + blocks.length);
        for (int i = 0; i < blocks.length; i++) {
            String blockContent = new String(blocks[i]);
            System.out.println("Block " + i + ": \"" + blockContent + "\"");
        }

        // Unir los bloques de nuevo
        byte[] joinedData = Util.join(blocks);

        System.out.println("Joined data length: " + joinedData.length + " bytes");
        System.out.println("Data integrity check: " + Arrays.equals(data, joinedData));
        System.out.println("Joined data: " + new String(joinedData));
    }
}
