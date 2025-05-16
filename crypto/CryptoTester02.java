package crypto;

import javax.crypto.SecretKey;
import java.io.File;

public class CryptoTester02 {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("Proyecto.pdf");

        SecretKey key = CryptoUtils.generateDESKey();
        CryptoUtils.saveKey(key, "key.des");

        CryptoUtils.encryptFile(inputFile, key);

        File encryptedFile = new File("Proyecto.pdf.encrypted");
        CryptoUtils.decryptFile(encryptedFile, key);
    }
}
