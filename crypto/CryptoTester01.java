package crypto;

import javax.crypto.SecretKey;
import java.io.File;

public class CryptoTester01 {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("integrity.txt");

        // Generar y guardar llave
        SecretKey key = CryptoUtils.generateDESKey();
        CryptoUtils.saveKey(key, "clave.des");

        // Encriptar
        CryptoUtils.encryptTextFile(inputFile, key);

        // Leer la clave desde archivo
        SecretKey loadedKey = CryptoUtils.loadKey("clave.des");

        // Desencriptar
        File encryptedFile = new File("integrity.txt.encrypted");
        CryptoUtils.decryptTextFile(encryptedFile, loadedKey);
    }
}
