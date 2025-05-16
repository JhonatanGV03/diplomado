package network;

import user.Usuario;
import util.Base64;
import util.Util;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Base64Server {
    private static final int PUERTO = 5000;
    private static HashMap<String, Double> cuentas = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO);

            while (true) {
                try (Socket cliente = servidor.accept();
                     BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                     PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true)) {

                    String base64 = entrada.readLine();
                    System.out.println("\nMensaje recibido (Base64): " + base64);

                    byte[] datos = Base64.decode(base64);

                    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(datos))) {
                        Usuario usuario = (Usuario) ois.readObject();
                        String nombre = usuario.getNombre();
                        double monto = usuario.getMonto();

                        System.out.println("Usuario: " + nombre + ", Monto: " + monto);

                        String respuesta;
                        if (!cuentas.containsKey(nombre)) {
                            cuentas.put(nombre, monto);
                            respuesta = "Cuenta creada exitosamente. Saldo: " + monto;
                        } else {
                            double nuevoSaldo = cuentas.get(nombre) + monto;
                            cuentas.put(nombre, nuevoSaldo);
                            respuesta = "Transacción realizada. Saldo: " + nuevoSaldo;
                        }

                        salida.println(respuesta);
                        System.out.println("Respuesta enviada: " + respuesta);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}