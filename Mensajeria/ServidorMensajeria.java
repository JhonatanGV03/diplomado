package Mensajeria;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class ServidorMensajeria {
    private static final int PORT = 12345;
    private static final Map<String, String> usuarios = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> buzon = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado en puerto " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] tokens = line.split(" ", 3);
                    String comando = tokens[0];

                    switch (comando) {
                        case "REGISTRAR":
                            String usuario = tokens[1];
                            String publicKeyBase64 = tokens[2];
                            if (usuarios.containsKey(usuario)) {
                                out.println("El usuario " + usuario + " ya está registrado");
                            } else {
                                usuarios.put(usuario, publicKeyBase64);
                                buzon.put(usuario, new ArrayList<>());

                                try {
                                    File dir = new File("serverkeys");
                                    if (!dir.exists()) dir.mkdirs();

                                    byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
                                    FileOutputStream fos = new FileOutputStream("serverkeys/" + usuario + "_public.key");
                                    fos.write(publicKeyBytes);
                                    fos.close();
                                } catch (IOException e) {
                                    System.err.println("Error guardando llave de " + usuario + ": " + e.getMessage());
                                }

                                out.println("Bienvenido " + usuario);
                            }
                            break;

                        case "OBTENER_LLAVE_PUBLICA":
                            String target = tokens[1];
                            File keyFile = new File("serverkeys/" + target + "_public.key");

                            if (keyFile.exists()) {
                                try {
                                    byte[] keyBytes = Files.readAllBytes(keyFile.toPath());
                                    String base64Key = Base64.getEncoder().encodeToString(keyBytes);
                                    out.println("Llave pública de " + target + ": " + base64Key);
                                    System.out.println("Llave pública de " + target + " enviada");
                                } catch (IOException e) {
                                    out.println("ERROR. No se pudo leer la llave pública de " + target);
                                    System.err.println("Error leyendo archivo de llave de " + target + ": " + e.getMessage());
                                }
                            } else {
                                out.println("ERROR. El usuario " + target + " no está registrado");
                                System.err.println("Error: el usuario " + target + " no tiene llave pública registrada");
                            }
                            break;


                        case "ENVIAR":
                            String destinatario = tokens[1];
                            String mensaje = tokens[2];
                            if (usuarios.containsKey(destinatario)) {
                                buzon.get(destinatario).add(mensaje);
                                out.println("Mensaje enviado a " + destinatario);
                                System.out.println("Mensaje enviado a " + destinatario + ": " + mensaje);
                            } else {
                                out.println("ERROR. El usuario " + destinatario + " no está registrado.");
                                System.err.println("Error: el usuario " + destinatario + " no está registrado.");
                            }
                            break;

                        case "LEER":
                            String solicitante = tokens[1];
                            if (!usuarios.containsKey(solicitante)) {
                                out.println("ERROR. El usuario " + solicitante + " no está registrado.");
                                System.err.println("Error: el usuario " + solicitante + " no está registrado.");
                            } else {
                                List<String> mensajes = buzon.get(solicitante);
                                out.println("El usuario " + solicitante + " tiene " + mensajes.size() + " mensaje(s)");
                                System.out.println("El usuario " + solicitante + " tiene " + mensajes.size() + " mensaje(s)");
                                for (String m : mensajes) out.println(m);
                                mensajes.clear();
                            }
                            break;

                        case "SALIR":
                            out.println("Conexión cerrada.");
                            System.out.println("Cliente desconectado: " + socket.getInetAddress());
                            return;

                        default:
                            out.println("Comando no reconocido.");
                    }
                }
            } catch (IOException e) {
                System.err.println("Error manejando cliente: " + e.getMessage());
            }
        }
    }
}

