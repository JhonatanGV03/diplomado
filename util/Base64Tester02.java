package util;

import user.Person;

public class Base64Tester02 {
    public static void main(String[] args) throws Exception {
        //Crear objeto Person
        Person persona = new Person("Cesar", 23, 1.87);
        System.out.println("Original: " + persona);

        //Serializar
        byte[] serialized = Util.objectToByteArray(persona);
        //Codificar a Base64
        String encoded = Base64.encode(serialized);
        System.out.println("Codificado: " + encoded);

        //Decodificar Base64
        byte[] decoded = Base64.decode(encoded);
        //Deserializar
        Person p2 = (Person) Util.byteArrayToObject(decoded);
        System.out.println("Decodificado: " + p2);
    }
}
