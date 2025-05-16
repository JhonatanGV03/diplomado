package Mensajeria;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;
import publickeycipher.PublicKeyCipher;

public class ClienteMensajeria {
    private static final String SERVER = "localhost";
    private static final int PORT = 12345;

    private static KeyPair keyPair;
    private static String nombreUsuario;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese su nombre de usuario: ");
        nombreUsuario = scanner.nextLine();

        keyPair = cargarLlaves(nombreUsuario);
        if (keyPair == null) {
            keyPair = generateKeyPair();
            guardarLlaves(keyPair, nombreUsuario);
        }

        try (Socket socket = new Socket(SERVER, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            out.println("REGISTRAR " + nombreUsuario + " " + publicKeyBase64);
            System.out.println(in.readLine());

            while (true) {
                System.out.println("\nOpciones:\n1. Obtener llave pública\n2. Enviar mensaje\n3. Leer mensajes\n4. Salir");
                System.out.println("Seleccione una opción: ");
                String opcion = scanner.nextLine();

                if (opcion.equals("1")) {
                    System.out.print("Usuario destino: ");
                    String destino = scanner.nextLine();
                    out.println("OBTENER_LLAVE_PUBLICA " + destino);
                    System.out.println(in.readLine());

                } else if (opcion.equals("2")) {
                    System.out.print("Usuario destino: ");
                    String destino = scanner.nextLine();

                    System.out.print("Mensaje: ");
                    String mensaje = scanner.nextLine();

                    out.println("OBTENER_LLAVE_PUBLICA " + destino);
                    String respuesta = in.readLine();

                    if (respuesta.startsWith("Llave pública")) {
                        String keyBase64 = respuesta.split(": ")[1];
                        PublicKey pubKey = PublicKeyCipher.decodePublicKey(keyBase64);
                        String mensajeConRemitente = "[" + nombreUsuario + "] " + mensaje;
                        String mensajeCifrado = PublicKeyCipher.encryptToBase64(mensajeConRemitente, pubKey);
                        out.println("ENVIAR " + destino + " " + mensajeCifrado);
                        System.out.println(in.readLine());
                    } else {
                        System.out.println(respuesta);
                    }

                } else if (opcion.equals("3")) {
                    out.println("LEER " + nombreUsuario);
                    String cantidad = in.readLine();
                    System.out.println(cantidad);

                    int numMensajes = 0;
                    try {
                        numMensajes = Integer.parseInt(cantidad.replaceAll("[^0-9]", ""));
                    } catch (NumberFormatException e) {
                        // Error inesperado, continuar sin leer más
                    }

                    for (int i = 0; i < numMensajes; i++) {
                        String linea = in.readLine();
                        if (linea != null) {
                            String decifrado = PublicKeyCipher.decryptFromBase64(linea, keyPair.getPrivate());
                            System.out.println(decifrado);
                        }
                    }


                } else if (opcion.equals("4")) {
                    out.println("SALIR");
                    break;
                }
            }
        }
    }


    private static void guardarLlaves(KeyPair keyPair, String nombreUsuario) throws Exception {
        File dir = new File("keys");
        if (!dir.exists()) dir.mkdirs();

        String publicKeyFile = "../keys/" + nombreUsuario + "_public.key";
        String privateKeyFile = "../keys/" + nombreUsuario + "_private.key";

        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        try (FileOutputStream fos = new FileOutputStream(publicKeyFile)) {
            fos.write(publicKeyBytes);
        }

        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        try (FileOutputStream fos = new FileOutputStream(privateKeyFile)) {
            fos.write(privateKeyBytes);
        }
    }

    private static KeyPair cargarLlaves(String nombreUsuario) throws Exception {
        String pubFileName = "../keys/" + nombreUsuario + "_public.key";
        String privFileName = "../keys/" + nombreUsuario + "_private.key";

        File pubFile = new File(pubFileName);
        File privFile = new File(privFileName);

        if (!pubFile.exists() || !privFile.exists()) return null;

        byte[] pubBytes = Files.readAllBytes(pubFile.toPath());
        byte[] privBytes = Files.readAllBytes(privFile.toPath());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubBytes);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privBytes);

        PublicKey publicKey = keyFactory.generatePublic(pubSpec);
        PrivateKey privateKey = keyFactory.generatePrivate(privSpec);

        return new KeyPair(publicKey, privateKey);
    }

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();
    }
}
