package publickeycipher;

import java.io.IOException;
import java.io.Serializable;
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

public class PublicKeyCipherTester05 {

    // Clase interna para probar la serialización y cifrado de objetos
    static class Person implements Serializable {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }

    public static void main(String[] args) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            IOException,
            ClassNotFoundException {

        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Crear un objeto para cifrar
        Person person = new Person("Juan Pérez", 30);
        System.out.println("Objeto original: " + person);

        // Cifrar el objeto
        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);
        byte[] encryptedObject = cipher.encryptObject(person, publicKey);
        System.out.println("Objeto cifrado (hex): " + Util.byteArrayToHexString(encryptedObject, " "));

        // Descifrar el objeto
        Object decryptedObject = cipher.decryptObject(encryptedObject, privateKey);
        System.out.println("Objeto descifrado: " + decryptedObject);
    }
}
