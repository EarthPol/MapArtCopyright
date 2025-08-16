package net.glassmc.mapartcopyright.util;

/**
 * Utility for sanitizing user input like map names and creator credits.
 */
public class StringSanitizer {

    /**
     * Cleans a string by:
     * - Trimming whitespace
     * - Removing all control characters
     * - Limiting maximum length
     *
     * @param input     Raw input from player
     * @param maxLength Maximum length allowed
     * @return Sanitized string
     */
    public static String clean(String input, int maxLength) {
        if (input == null) return "";

        // Remove non-printable and illegal characters except Minecraft color codes (§)
        String stripped = input.replaceAll("[^\\p{Print}&&[^§]]", "").trim();

        if (stripped.length() > maxLength) {
            stripped = stripped.substring(0, maxLength);
        }

        return stripped;
    }
}
