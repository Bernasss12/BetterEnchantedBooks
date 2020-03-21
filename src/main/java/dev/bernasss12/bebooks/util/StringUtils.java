package dev.bernasss12.bebooks.util;

public class StringUtils {
    public static boolean isValidHexColorString(String hex) {
        String value;
        if (hex.startsWith("#")) {
            value = hex.substring(1);
        } else if (hex.startsWith("0x")) {
            value = hex.substring(2);
        } else return false;
        try {
            int i = Integer.parseInt(value, 16);
            return i >= 0 && i <= 0xffffff && value.length() == 6;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static int getValidIntColor(String hex) {
        if (isValidHexColorString(hex)) {
            String value;
            if (hex.startsWith("#")) {
                value = hex.substring(1);
            } else if (hex.startsWith("0x")) {
                value = hex.substring(2);
            } else return 0x000000;
            try {
                return Integer.parseInt(value, 16);
            } catch (NumberFormatException ex) {
                return 0x000000;
            }
        } else return 0x000000;
    }

    public static String getHexColorString(int color) {
        String overpadded = "000000" + Integer.toHexString(color);
        return "#" + overpadded.substring(overpadded.length() - 6);
    }
}
