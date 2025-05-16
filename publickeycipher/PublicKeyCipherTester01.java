package publickeycipher;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import util.Util;

public class PublicKeyCipherTester01 {
    public static void main(String[] args) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);
        String clearText = "In public key cryptography, one key is used to encrypt the text. " +
                "The other key is used to decrypt the text.";
        System.out.println(clearText);

        // Cifrar con llave pública, descifrar con llave privada
        byte[] encryptedText = cipher.encryptMessage(clearText, publicKey);
        System.out.println(Util.byteArrayToHexString(encryptedText, " "));
        clearText = cipher.decryptMessage(encryptedText, privateKey);
        System.out.println(clearText);

        // Cifrar con llave privada, descifrar con llave pública
        encryptedText = cipher.encryptMessage(clearText, privateKey);
        System.out.println(Util.byteArrayToHexString(encryptedText, " "));
        clearText = cipher.decryptMessage(encryptedText, publicKey);
        System.out.println(clearText);
    }
}
