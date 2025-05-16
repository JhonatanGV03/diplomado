package integrity;

public class HasherTester1 {

    public static void main(String args[]) throws Exception {
        String message = "Fundamentos de seguridad digital";
        System.out.println(Hasher.getHash(message, "SHA-256"));
    }

}
