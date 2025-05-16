package publickeycipher;

import util.Base64;
import util.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;
import javax.crypto.*;

public class PublicKeyCipherTester03 {
    public static void main(String[] args) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            IOException {

        // 1. Generar par de llaves
        String algorithm = "RSA";
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 2. Iniciar el cifrador
        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);
        String clearText = "Diplomado 10/05/25";

        // 3. Cifrar con clave pública
        byte[] encryptedText = cipher.encryptMessage(clearText, publicKey);

        // 4. Guardar claves en formato PEM (.asc)
        try (FileWriter pubWriter = new FileWriter("PublicCipherDocs/public.asc");
             FileWriter privWriter = new FileWriter("PublicCipherDocs/private.asc");
             FileWriter encWriter = new FileWriter("PublicCipherDocs/encrypted.asc")) {

            String publicPem = Util.keyToPem(publicKey, false);
            String privatePem = Util.keyToPem(privateKey, true);
            String encryptedBase64 = Base64.encode(encryptedText);

            pubWriter.write(publicPem);
            privWriter.write(privatePem);
            encWriter.write(encryptedBase64);
        }

        System.out.println("Llaves y mensaje cifrado guardados en formato ASCII (.asc)");
    }
}


