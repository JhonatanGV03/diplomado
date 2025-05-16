package util;

import java.io.*;
import java.net.Socket;
import static integrity.Hasher.generateIntegrityCheckerFile;

public class Files {

	public static void sendFile(String filename, Socket socket) throws Exception {
		System.out.println("File to send: " + filename);
		File localFile = new File(filename);
		BufferedInputStream fromFile = new BufferedInputStream(new FileInputStream(localFile));

		// Enviar nombre del archivo
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
		printWriter.println(localFile.getName());

		// Enviar tamaño del archivo
		long size = localFile.length();
		System.out.println("Size: " + size);
		printWriter.println("Size:" + size);

		BufferedOutputStream toNetwork = new BufferedOutputStream(socket.getOutputStream());

		pause(50);

		// Enviar archivo en bloques
		byte[] blockToSend = new byte[1024];
		int in;
		while ((in = fromFile.read(blockToSend)) != -1) {
			toNetwork.write(blockToSend, 0, in);
		}

		toNetwork.flush();
		fromFile.close();

		pause(50);
	}

	public static String receiveFile(String folder, Socket socket) throws Exception {
		File fd = new File(folder);
		if (!fd.exists()) {
			fd.mkdir();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedInputStream fromNetwork = new BufferedInputStream(socket.getInputStream());

		// Leer nombre del archivo
		String filename = reader.readLine();
		String fullPath = folder + File.separator + filename;

		BufferedOutputStream toFile = new BufferedOutputStream(new FileOutputStream(fullPath));

		System.out.println("File to receive: " + fullPath);

		// Leer tamaño del archivo
		String sizeString = reader.readLine();
		long size = Long.parseLong(sizeString.split(":")[1]);
		System.out.println("Size: " + size);

		// Recibir archivo en bloques
		byte[] blockToReceive = new byte[512];
		int in;
		long remainder = size;
		while ((in = fromNetwork.read(blockToReceive)) != -1) {
			toFile.write(blockToReceive, 0, in);
			remainder -= in;
			if (remainder == 0) break;
		}

		pause(50);

		toFile.flush();
		toFile.close();
		System.out.println("File received: " + fullPath);

		return fullPath;
	}

	public static void pause(int milliseconds) throws Exception {
		Thread.sleep(milliseconds);
	}

	public static void sendFolder(String folderName, Socket socket) throws Exception {
		File folder = new File(folderName);
		if (!folder.exists()) {
			throw new FileNotFoundException("La carpeta no existe.");
		}

		File[] files = folder.listFiles((dir, name) -> !name.equals("integrity.txt")); // ignorar archivo anterior si existe

		if (files == null || files.length == 0) {
			throw new IOException("La carpeta está vacía.");
		}

		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		writer.println(files.length);

		// Enviar archivos
		for (File file : files) {
			sendFile(file.getAbsolutePath(), socket);
		}

		// Crear y enviar archivo de integridad
		generateIntegrityCheckerFile(folderName, "integrity.txt");
		sendFile("integrity.txt", socket);

		System.out.println("Carpeta y archivo de integridad enviados correctamente.");
	}

	public static void receiveFolder(String folderName, Socket socket) throws Exception {
		File folder = new File(folderName);

		if (!folder.exists()) {
			if (!folder.mkdirs()) {
				throw new IOException("No se pudo crear la carpeta destino.");
			}
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		int fileCount = Integer.parseInt(reader.readLine());

		if (fileCount == 0) {
			throw new IOException("No se enviaron archivos.");
		}

		// Recibir archivos
		for (int i = 0; i < fileCount; i++) {
			receiveFile(folderName, socket);
		}
	}
}
