package filetransfer;

import java.io.File;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import integrity.Hasher;
import util.Files;

public class FileTransferServer {
	public static final int PORT = 4000;

	private ServerSocket listener;
	private Socket serverSideSocket;

	private int port;

	public FileTransferServer() {
		this.port = PORT;
		System.out.println("Michael Aristizabal - Yhonatan Gomez - May 2/2025");
		System.out.println("File transfer server is running on port: " + this.port);
	}

	public FileTransferServer(int port) {
		this.port = port;
		System.out.println("Michael Aristizabal - Yhonatan Gomez - May 2/2025");
		System.out.println("File transfer server is running on port: " + this.port);
	}

	private void init() throws Exception {
		listener = new ServerSocket(port);
		System.out.println("Servidor esperando conexiones en el puerto " + port + "...");

		while (true) {
			serverSideSocket = listener.accept();
			System.out.println("Cliente conectado desde: " + serverSideSocket.getInetAddress());
			protocol(serverSideSocket);
			serverSideSocket.close();
		}
	}

	public void protocol(Socket socket) throws Exception {
		System.out.println("Iniciando protocolo de transferencia segura de archivos...");

		// 1. Recibir un archivo encriptado del cliente
		System.out.println("Esperando archivo del cliente...");
		File receivedFile = SecureFileTransfer.receiveEncryptedFile(socket, "Receive");

		if (receivedFile != null) {
			System.out.println("Archivo recibido y desencriptado correctamente: " + receivedFile.getName());

			// Verificar si es un archivo de texto o binario
			if (receivedFile.getName().endsWith(".txt")) {
				System.out.println("El archivo recibido es un archivo de texto.");
			} else {
				System.out.println("El archivo recibido es un archivo binario.");
			}

			// 2. Enviar un archivo encriptado al cliente como respuesta
			System.out.println("\nEnviando archivo de respuesta al cliente...");
			Thread.sleep(1000); // Pequeña pausa para asegurar que los streams estén listos

			// Decidir qué tipo de archivo enviar (texto o binario)
			File fileToSend;
			if (receivedFile.getName().endsWith(".txt")) {
				// Si recibimos un archivo de texto, enviamos un archivo binario
				fileToSend = new File("Proyecto.pdf");
				if (!fileToSend.exists()) {
					// Si no existe, enviamos un archivo de texto
					fileToSend = new File("Prueba.txt");
				}
			} else {
				// Si recibimos un archivo binario, enviamos un archivo de texto
				fileToSend = new File("Prueba.txt");
				if (!fileToSend.exists()) {
					// Si no existe, enviamos un archivo binario
					fileToSend = new File("Proyecto.pdf");
				}
			}

			// Si no existe ninguno de los archivos de respuesta, enviamos el mismo archivo recibido
			if (!fileToSend.exists()) {
				fileToSend = receivedFile;
				System.out.println("No se encontraron archivos de respuesta. Enviando el mismo archivo recibido.");
			}

			SecureFileTransfer.sendEncryptedFile(socket, fileToSend, "Docs");
		} else {
			System.out.println("Error al recibir el archivo del cliente.");
		}

		System.out.println("\nProtocolo de transferencia segura completado.");
	}

	public static void main(String[] args) throws Exception {
		FileTransferServer fts = null;
		if (args.length == 0) {
			fts = new FileTransferServer();
		} else {
			int port = Integer.parseInt(args[0]);
			fts = new FileTransferServer(port);
		}
		fts.init();
	}
}
