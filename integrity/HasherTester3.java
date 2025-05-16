package integrity;

public class HasherTester3 {
    public static void main(String[] args) throws Exception {
        String ruta = "src/resources/documents";
        String rutaArchivo = "src/resources/checksum.txt";

        Hasher.generateIntegrityCheckerFile(ruta, rutaArchivo);
        System.out.println("Archivo Generado");
    }
}