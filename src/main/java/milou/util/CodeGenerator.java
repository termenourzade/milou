package milou.util;

import java.security.SecureRandom;
import java.util.Random;

public class CodeGenerator {
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random rand = new Random();

    public static String next6() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) sb.append(alphabet.charAt(rand.nextInt(alphabet.length())));
        return sb.toString();
    }
}
