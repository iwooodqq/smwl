package org.example.admin.util;

import java.util.Random;

public final class RandomStringGenerator {

    private static final String CHAR_LIST = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int STRING_LENGTH = 6;
    private static final Random RANDOM = new Random();

    /**
     * 生成一个包含英文大写字母和数字的随机字符串
     *
     * @return 随机字符串
     */
    public static String generateRandom() {
        StringBuilder sb = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            int index = RANDOM.nextInt(CHAR_LIST.length());
            char randomChar = CHAR_LIST.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}