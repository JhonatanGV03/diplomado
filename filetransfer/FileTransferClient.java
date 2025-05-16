package filetransfer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;

import crypto.CryptoUtils;
import integrity.Hasher;
import util.Files;

import javax.crypto.SecretKey;

import static integrity.Hasher.checkIntegrityFile;

public class FileTransferClient {
	public static final int PORT = 4000;
	public static final String SERVER = "localhost";

	private Socket clientSideSocket;

	private String server;
	private int port;

	public FileTransferClient() {
		this.server = SERVER;
		this.port = PORT;
		System.out.println("Michael Aristizabal - Yhonatan Gomez - May 2/2025");
		System.out.println("File transfer client is running ... connecting the server in "+ this.server + ":" + this.port);
		System.out.println("Other usage: FileTransferClient host port. Ex: FileTransferClient localhost 3800");
	}

	public FileTransferClient(String server, int port) {
		this.server = server;
		this.port = port;
		System.out.println("Michael Aristizabal - Yhonatan Gomez - May 2/2025");
		System.out.println("Echo client is running ... connecting the server in "+ this.server + ":" + this.port);
	}

	public void init() throws Exception {
		clientSideSocket = new Socket(server, port);
		protocol(clientSideSocket);
		clientSideSocket.close();
	}

	public void protocol(Socket socket) throws Exception {
		System.out.println("Iniciando protocolo de transferencia segura de archivos...");

		// 1. Enviar un archivo encriptado al servidor
		File fileToSend = new File("Prueba.txt");
		if (fileToSend.exists()) {
			System.out.println("Enviando archivo: " + fileToSend.getName());
			SecureFileTransfer.sendEncryptedFile(socket, fileToSend, "Receive");
		} else {
			System.out.println("El archivo a enviar no existe: " + fileToSend.getAbsolutePath());
			// Intentar con un archivo binario
			fileToSend = new File("Proyecto.pdf");
			if (fileToSend.exists()) {
				System.out.println("Enviando archivo binario: " + fileToSend.getName());
				SecureFileTransfer.sendEncryptedFile(socket, fileToSend, "Receive");
			} else {
				System.out.println("No se encontraron archivos para enviar");
			}
		}

		// 2. Recibir un archivo encriptado del servidor
		System.out.println("\nEsperando archivo del servidor...");
		Thread.sleep(1000); // Pequeña pausa para asegurar que los streams estén listos
		File receivedFile = SecureFileTransfer.receiveEncryptedFile(socket, "Docs");

		if (receivedFile != null) {
			System.out.println("Archivo recibido y desencriptado correctamente: " + receivedFile.getName());

			// Verificar si es un archivo de texto o binario
			if (receivedFile.getName().endsWith(".txt")) {
				System.out.println("El archivo recibido es un archivo de texto.");
			} else {
				System.out.println("El archivo recibido es un archivo binario.");
			}
		} else {
			System.out.println("Error al recibir el archivo del servidor.");
		}

		System.out.println("\nProtocolo de transferencia segura completado.");
	}

	public static void main(String[] args) throws Exception {
		FileTransferClient ftc = null;
		if (args.length == 0) {
			ftc = new FileTransferClient();

		} else {
			String server = args[0];
			int port = Integer.parseInt(args[1]);
			ftc = new FileTransferClient(server, port);
		}
		ftc.init();
	}
}
