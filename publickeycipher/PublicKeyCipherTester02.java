package publickeycipher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import util.Util;

public class PublicKeyCipherTester02 {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // Generar un par de llaves RSA de 2048 bits
        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Obtener las llaves pública y privada
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Imprimir las llaves en formato PEM
        System.out.println(Util.keyToPem(publicKey, false));
        System.out.println();
        System.out.println(Util.keyToPem(privateKey, true));
    }
}
