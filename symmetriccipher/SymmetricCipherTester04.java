package symmetriccipher;

import util.Util;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class SymmetricCipherTester04 {
    public static void main(String[] args) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            IOException,
            ClassNotFoundException {
        SecretKey secretKey = null;


        secretKey = KeyGenerator.getInstance("DES").generateKey();


        SymmetricCipher cipher = new SymmetricCipher(secretKey, "DES/ECB/PKCS5Padding");


        ArrayList<String> clearObject = new ArrayList<String>();
        byte[] encrypedObject = null;


        clearObject.add("Ana");
        clearObject.add("Bety");
        clearObject.add("Carolina");
        clearObject.add("Daniela");
        clearObject.add("Elena");


        System.out.println(clearObject);


        encrypedObject = cipher.encryptObject(clearObject);


        System.out.println(Util.byteArrayToHexString(encrypedObject, " "));


        clearObject = (ArrayList<String>) cipher.decryptObject(encrypedObject);
        System.out.println(clearObject);
    }

}
