package integrity;

public class HasherTester4 {
    public static void main(String[] args) throws Exception {
        String ruta = "src/resources/documents";
        String rutaArchivo = "src/resources/checksum.txt";
        Hasher.checkIntegrityFile(rutaArchivo, ruta);
    }
}
