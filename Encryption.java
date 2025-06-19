public class Encryption {
    private String vigenereKey = "SoLongAndThankYouForAllTheFish";
    private int caesarKey = 3;

    public String vigenereCipher(String originalText) {
        StringBuilder cipherText = new StringBuilder();

        for (int i = 0; i < originalText.length(); i++) {
            char plainChar = originalText.charAt(i);

            if (Character.isLetter(plainChar)) {
                boolean isUpper = Character.isUpperCase(plainChar);
                char base = isUpper ? 'A' : 'a';

                char keyChar = vigenereKey.charAt(i % vigenereKey.length());
                int key = Character.toUpperCase(keyChar) - 'A';

                int shifted = (plainChar - base + key) % 26;
                cipherText.append((char)(shifted + base));
            } else {
                cipherText.append(plainChar); // Preserve spaces, punctuation, etc.
            }
        }

        return cipherText.toString();
    }

    public String vigenereDecipher(String cipherText) {
        StringBuilder originalText = new StringBuilder();

        for (int i = 0; i < cipherText.length(); i++) {
            char cipherChar = cipherText.charAt(i);

            if (Character.isLetter(cipherChar)) {
                boolean isUpper = Character.isUpperCase(cipherChar);
                char base = isUpper ? 'A' : 'a';

                char keyChar = vigenereKey.charAt(i % vigenereKey.length());
                int key = Character.toUpperCase(keyChar) - 'A';

                int shifted = (cipherChar - base - key + 26) % 26;
                originalText.append((char)(shifted + base));
            } else {
                originalText.append(cipherChar);
            }
        }

        return originalText.toString();
    }

    public String[] cipherViginereArray(String[] originalTexts) {
        for (int i = 0; i < originalTexts.length; i++) {
            originalTexts[i] = vigenereCipher(originalTexts[i]);
        }

        return originalTexts;
    }

    public String[] decipherViginereArray(String[] originalTexts) {
        for (int i = 0; i < originalTexts.length; i++) {
            originalTexts[i] = vigenereDecipher(originalTexts[i]);
        }

        return originalTexts;
    }

    public String caesarCipher(String originalText, int key) {
        StringBuilder cipherText = new StringBuilder();

        for (char curChar : originalText.toCharArray()) {
            if (Character.isLetter(curChar)) {
                int base = Character.isUpperCase(curChar) ? 65 : 97;
                int shifted = (curChar - base + key + 26) % 26;
                char cipherChar = (char)(shifted + base);

                cipherText.append(cipherChar);
            }
            else {
                // Caracteres que não são letras não são alterados 
                cipherText.append(curChar);
            }
        }

        return cipherText.toString();
    }

    public String caesarCipher(String originalText) {
        return caesarCipher(originalText, caesarKey);
    }

    public String[] cipherCaesarArray(String[] originalTexts, int key) {
        for (int i = 0; i < originalTexts.length; i++) {
            originalTexts[i] = caesarCipher(originalTexts[i], key);
        }

        return originalTexts;
    }

    public String[] cipherCaesarArray(String[] originalTexts) {
        return cipherCaesarArray(originalTexts, caesarKey);
    }

    public String caesarDecipher(String cryptText, int key) {
        return caesarCipher(cryptText, key * -1);
    }

    public String caesarDecipher(String cryptText) {
        return caesarCipher(cryptText, caesarKey * -1);
    }

    public String[] decipherCaesarArray(String[] cryptTexts, int key) {
        return cipherCaesarArray(cryptTexts, key * -1);
    }

    public String[] decipherCaesarArray(String[] cryptTexts) {
        return cipherCaesarArray(cryptTexts, caesarKey * -1);
    }
}
