public class Encryption {
    private int caesarKey = 3;

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
