package util;

import java.util.ArrayList;

public class Base64Tester01 {
    public static void main(String[] args) throws
            Exception {
        ArrayList<String> names = new ArrayList<String>();
        names.add("Ana");
        names.add("Bibiana");
        names.add("Carolina");
        names.add("Diana");
        names.add("Elena");

        System.out.println(names);

        byte[] namesBA = Util.objectToByteArray(names);
        String namesB64 = Base64.encode(namesBA);
        System.out.println(namesB64);

        byte[] namesBA2 = Base64.decode(namesB64);
        ArrayList<String> names2 = (ArrayList<String>) Util.byteArrayToObject(namesBA2);
        System.out.println(names2);
    }

}
