package publickeycipher;

import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PublicKeyCipher {

    private Cipher cipher;

    public PublicKeyCipher(String algorithm) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException {
        cipher = Cipher.getInstance(algorithm);
    }

    public byte[] encryptMessage(String input, Key key) throws
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        byte[] cipherText = null;
        byte[] clearText = input.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        cipherText = cipher.doFinal(clearText);
        return cipherText;
    }

    public String decryptMessage(byte[] input, Key key) throws
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        String output = "";
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] clearText = cipher.doFinal(input);
        output = new String(clearText);
        return output;
    }

    public byte[] encryptObject(Object input, Key key) throws
            InvalidKeyException,
            IOException,
            IllegalBlockSizeException,
            BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] clearObject = Util.objectToByteArray(input);
        byte[] cipherObject = cipher.doFinal(clearObject);
        return cipherObject;
    }

    public Object decryptObject(byte[] input, Key key) throws
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            ClassNotFoundException,
            IOException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] clearText = cipher.doFinal(input);
        Object output = Util.byteArrayToObject(clearText);
        return output;
    }

    public byte[] encryptFragment(byte[] input, Key key) throws
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    public byte[] decryptFragment(byte[] input, Key key) throws
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }
    public static String encryptToBase64(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Desencriptar texto en Base64 usando una llave privada
    public static String decryptFromBase64(String base64CipherText, PrivateKey privateKey) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(base64CipherText);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, "UTF-8");
    }

    public static PublicKey decodePublicKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
