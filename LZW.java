import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LZW {

    private static final String VERSAO_PATH = "versao_lzw.txt";

    public List<Integer> compress(String input) {
        // Filtrar apenas caracteres ASCII básicos
        input = input.replaceAll("[^\\x00-\\x7F]", "?");

        int dictSize = 256;
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char) i, i);

        String w = "";
        List<Integer> result = new ArrayList<>();

        for (char c : input.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc)) {
                w = wc;
            } else {
                Integer code = dictionary.get(w);
                if (code == null) {
                    System.out.println("ERRO: dicionário não contém '" + w + "' (comprimento: " + w.length() + "), ignorando.");
                } else {
                    result.add(code);
                }
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }

        if (!w.isEmpty()) {
            Integer code = dictionary.get(w);
            if (code != null) {
                result.add(code);
            }
        }

        return result;
    }

    public String decompress(List<Integer> compressedInput) {
        if (compressedInput == null || compressedInput.isEmpty()) {
            throw new IllegalArgumentException("Lista de códigos está vazia.");
        }

        int dictSize = 256;
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, "" + (char) i);
        }

        Integer firstCode = compressedInput.get(0);
        if (!dictionary.containsKey(firstCode)) {
            throw new IllegalArgumentException("Código inicial inválido: " + firstCode);
        }

        String w = dictionary.get(firstCode);
        StringBuilder result = new StringBuilder(w);

        for (int i = 1; i < compressedInput.size(); i++) {
            int k = compressedInput.get(i);
            String entry;

            if (dictionary.containsKey(k)) {
                entry = dictionary.get(k);
            } else if (k == dictSize) {
                entry = w + w.charAt(0);
            } else {
                throw new IllegalArgumentException("Código inválido: " + k + ", dicionário atual até " + (dictSize - 1));
            }

            result.append(entry);
            dictionary.put(dictSize++, w + entry.charAt(0));
            w = entry;
        }

        return result.toString();
    }

    public double getVersao() {
        try {
            if (!Files.exists(Paths.get(VERSAO_PATH))) return 1.0;
            String txt = Files.readString(Paths.get(VERSAO_PATH));
            return Double.parseDouble(txt);
        } catch (Exception e) {
            return 1.0;
        }
    }

    public void incrementarVersao() {
        double versao = getVersao() + 0.1;
        try {
            Files.writeString(Paths.get(VERSAO_PATH), String.format("%.1f", versao), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
