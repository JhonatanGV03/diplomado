package util;

/*
 * Tomado de:
 * https://resources.oreilly.com/examples/9781565924024/blob/master/files/oreilly/jonathan/util/Base64.java
 */
public class Base64 {
    public static String encode(byte[] raw) {
        StringBuffer encoded = new StringBuffer();

        for (int i = 0; i < raw.length; i += 3) {
            encoded.append(encodeBlock(raw, i));
        }
        return encoded.toString();
    }

    protected static char[] encodeBlock(byte[] raw, int offset) {
        int block = 0;
        int slack = raw.length - offset - 1;
        int end = (slack >= 2) ? 2 : slack;

        for (int i = 0; i <= end; i++) {
            byte b = raw[offset + i];
            int neuter = (b < 0) ? b + 256 : b;
            block += neuter << (8 * (2 - i));
        }

        char[] base64 = new char[4];

        for (int i = 0; i < 4; i++) {
            int sixbit = (block >>> (6 * (3 - i))) & 0x3f;
            base64[i] = getChar(sixbit);
        }

        if (slack < 1)
            base64[2] = '=';
        if (slack < 2)
            base64[3] = '=';

        return base64;
    }

    protected static char getChar(int sixBit) {
        if (sixBit >= 0 && sixBit <= 25)
            return (char) ('A' + sixBit);

        if (sixBit >= 26 && sixBit <= 51)
            return (char) ('a' + (sixBit - 26));

        if (sixBit >= 52 && sixBit <= 61)
            return (char) ('0' + (sixBit - 52));

        if (sixBit == 62)
            return '+';

        if (sixBit == 63)
            return '/';

        return '?';
    }

    public static byte[] decode(String base64) {
        int pad = 0;

        for (int i = base64.length() - 1; base64.charAt(i) == '='; i--) {
            pad++;
        }

        int length = base64.length() * 6 / 8 - pad;
        byte[] raw = new byte[length];
        int rawIndex = 0;

        for (int i = 0; i < base64.length(); i += 4) {
            int block = (getValue(base64.charAt(i)) << 18) + (getValue(base64.charAt(i + 1)) << 12)
                    + (getValue(base64.charAt(i + 2)) << 6) + (getValue(base64.charAt(i + 3)));

            for (int j = 0; j < 3 && rawIndex + j < raw.length; j++)
                raw[rawIndex + j] = (byte) ((block >> (8 * (2 - j))) & 0xff);

            rawIndex += 3;
        }

        return raw;
    }

    protected static int getValue(char c) {
        if (c >= 'A' && c <= 'Z')
            return c - 'A';
        if (c >= 'a' && c <= 'z')
            return c - 'a' + 26;
        if (c >= '0' && c <= '9')
            return c - '0' + 52;
        if (c == '+')
            return 62;
        if (c == '/')
            return 63;
        if (c == '=')
            return 0;

        return -1;
    }
}