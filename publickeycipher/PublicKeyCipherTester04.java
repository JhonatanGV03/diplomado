package publickeycipher;

import util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;

public class PublicKeyCipherTester04 {
    public static void main(String[] args) throws Exception {
        String algorithm = "RSA";
        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);

        // 1. Leer archivos .asc
        String privatePem = new String(Files.readAllBytes(Paths.get("PublicCipherDocs/private.asc")));
        String encryptedBase64 = new String(Files.readAllBytes(Paths.get("PublicCipherDocs/encrypted.asc")));

        // 2. Convertir clave privada PEM a objeto PrivateKey
        PrivateKey privateKey = pemToPrivateKey(privatePem);

        // 3. Decodificar texto cifrado desde Base64
        byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedBase64);

        // 4. Descifrar
        String decryptedText = cipher.decryptMessage(encryptedBytes, privateKey);

        System.out.println("Texto descifrado:");
        System.out.println(decryptedText);
    }

    private static PrivateKey pemToPrivateKey(String pem) throws Exception {
        // Eliminar encabezado, pie de página y saltos de línea
        String base64Key = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = java.util.Base64.getDecoder().decode(base64Key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}

