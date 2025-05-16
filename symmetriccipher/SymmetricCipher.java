package symmetriccipher;
import util.Util;
import javax.crypto.*;
import javax.management.openmbean.InvalidKeyException;
import java.io.*;
import java.security.NoSuchAlgorithmException;

public class SymmetricCipher {

    private SecretKey secretKey;
    private Cipher cipher;

    public SymmetricCipher(SecretKey secretKey, String transformation)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.secretKey = secretKey;
        cipher = Cipher.getInstance(transformation);
    }

    public byte[] encryptMessage(String input) throws
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException, java.security.InvalidKeyException {
        byte[] clearText = input.getBytes();
        byte[] cipherText = null;


        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        cipherText = cipher.doFinal(clearText);


        return cipherText;
    }

    public String decryptMessage(byte[] input) throws
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException, java.security.InvalidKeyException {
        String output = "";


        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] clearText = cipher.doFinal(input);
        output = new String(clearText);


        return output;
    }

    public byte[] encryptObject(Object input)
            throws IOException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, java.security.InvalidKeyException {
        byte[] cipherObject = null;
        byte[] clearObject = null;


        clearObject = Util.objectToByteArray(input);


        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        cipherObject = cipher.doFinal(clearObject);


        return cipherObject;
    }

    public Object decryptObject(byte[] input) throws
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            ClassNotFoundException,
            IOException, java.security.InvalidKeyException {
        Object output = null;


        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] clearObject = cipher.doFinal(input);


        output = Util.byteArrayToObject(clearObject);


        return output;
    }









}
