package publickeycipher;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Random;

public class RSAtester01 {
    public static String generarCadenaAleatoria(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < longitud; i++) {
            sb.append(caracteres.charAt(rand.nextInt(caracteres.length())));
        }

        return sb.toString();
    }

    // Intenta cifrar una cadena con RSA, devuelve true si tiene éxito
    public static boolean intentarCifrado(String texto, int keySize) {
        try {
            KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
            generador.initialize(keySize);
            KeyPair par = generador.generateKeyPair();

            Cipher cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cifrador.init(Cipher.ENCRYPT_MODE, par.getPublic());

            byte[] datos = texto.getBytes("UTF-8");
            cifrador.doFinal(datos); // Esto lanza excepción si el mensaje es demasiado grande
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        int[] tamanosClave = {1024, 2048, 3072, 4096};

        for (int keySize : tamanosClave) {
            int longitud = 1;
            while (true) {
                String cadena = generarCadenaAleatoria(longitud);
                if (!intentarCifrado(cadena, keySize)) {
                    // No se pudo cifrar, el límite fue el anterior
                    System.out.println("Clave RSA de " + keySize + " bits -> Máx. texto plano: " + (longitud - 1) + " bytes");
                    break;
                }
                longitud++;
            }
        }
    }
}
