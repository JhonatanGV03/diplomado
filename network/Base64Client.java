package network;

import user.Usuario;
import util.Base64;
import util.Util;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Base64Client {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Cliente iniciado. Ingrese datos para transacciones:");

        while (true) {
            System.out.print("\nIngrese nombre (o 'salir' para terminar): ");
            String nombre = sc.nextLine();
            if (nombre.equalsIgnoreCase("salir")) break;

            System.out.print("Ingrese monto: ");
            double monto = Double.parseDouble(sc.nextLine());

            Usuario usuario = new Usuario(nombre, monto);
            procesarTransaccion(usuario);
        }

        sc.close();
        System.out.println("Cliente finalizado.");
    }

    public static void procesarTransaccion(Usuario usuario) {
        try (Socket socket = new Socket(HOST, PUERTO);
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(usuario);
            }

            String base64 = Base64.encode(baos.toByteArray());
            System.out.println("Enviando al servidor (Base64): " + base64);

            salida.println(base64); // enviar

            String respuesta = entrada.readLine();
            System.out.println("Respuesta del servidor: " + respuesta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}