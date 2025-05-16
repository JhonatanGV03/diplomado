package util;

import user.Person;

import java.util.ArrayList;

public class Base64Tester03 {
    public static void main(String[] args) throws Exception {
        // Crear lista de Person
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("Ana", 25, 1.65));
        people.add(new Person("Luis", 30, 1.80));
        people.add(new Person("María", 22, 1.60));

        System.out.println("Original list: " + people);

        // Serializar la lista
        byte[] serializedList = Util.objectToByteArray(people);
        // Codificar a Base64
        String encodedList = Base64.encode(serializedList);
        System.out.println("List codificada: " + encodedList);

        // Decodificar Base64
        byte[] decodedList = Base64.decode(encodedList);
        // Deserializar en lista
        @SuppressWarnings("unchecked")
        ArrayList<Person> list2 = (ArrayList<Person>) Util.byteArrayToObject(decodedList);

        System.out.println("Lista decodificada: " + list2);
    }
}
