import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LZW {

    private static final String VERSAO_PATH = "versao_lzw.txt";

    public byte[] compress(String input) throws IOException {
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
                result.add(dictionary.get(w));
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }

        if (!w.isEmpty()) {
            result.add(dictionary.get(w));
        }

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        for (int code : result) {
            dataOut.writeShort(code); // 2 bytes por código
        }
        dataOut.close();
        return byteOut.toByteArray();
    }

    public String decompress(byte[] compressedInput) throws IOException {
        if (compressedInput == null || compressedInput.length == 0) {
            throw new IllegalArgumentException("Dados comprimidos vazios.");
        }

        int dictSize = 256;
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, "" + (char) i);
        }

        DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(compressedInput));
        int firstCode = dataIn.readUnsignedShort();
        String w = dictionary.get(firstCode);
        StringBuilder result = new StringBuilder(w);

        while (dataIn.available() > 0) {
            int k = dataIn.readUnsignedShort();
            String entry;

            if (dictionary.containsKey(k)) {
                entry = dictionary.get(k);
            } else if (k == dictSize) {
                entry = w + w.charAt(0);
            } else {
                throw new IllegalArgumentException("Código inválido: " + k);
            }

            result.append(entry);
            dictionary.put(dictSize++, w + entry.charAt(0));
            w = entry;
        }

        return result.toString();
    }

    // Salva os bytes comprimidos em um arquivo
    public void salvarArquivoComprimido(byte[] dados, String caminho) throws IOException {
        Files.write(Paths.get(caminho), dados);
    }

    // Lê os bytes comprimidos de um arquivo
    public byte[] lerArquivoComprimido(String caminho) throws IOException {
        return Files.readAllBytes(Paths.get(caminho));
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
