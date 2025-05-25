import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;

public class Comparador {

    public void comparar(String texto) {
        Huffman huffman = new Huffman();
        LZW lzw = new LZW();

        try {
            int originalSize = texto.getBytes(StandardCharsets.UTF_8).length;

            // Huffman
            Map<Character, String> codeMap = new HashMap<>();
            long inicioHuffman = System.currentTimeMillis();
            String comprimidoHuffman = huffman.compress(texto, codeMap);
            long fimHuffman = System.currentTimeMillis();
            long tempoHuffman = fimHuffman - inicioHuffman;
            int tamanhoHuffman = comprimidoHuffman.getBytes(StandardCharsets.UTF_8).length;
            double taxaHuffman = (double) tamanhoHuffman / originalSize * 100;

            // LZW
            long inicioLZW = System.currentTimeMillis();
            List<Integer> comprimidoLZW = lzw.compress(texto);
            long fimLZW = System.currentTimeMillis();
            long tempoLZW = fimLZW - inicioLZW;
            int tamanhoLZW = comprimidoLZW.size() * Integer.BYTES; // cada Integer = 4 bytes
            double taxaLZW = (double) tamanhoLZW / originalSize * 100;

            // Resultados
            System.out.println("===== Resultados =====");
            System.out.println("Tamanho original: " + originalSize + " bytes\n");

            System.out.println("Huffman:");
            System.out.println("  Tempo: " + tempoHuffman + " ms");
            System.out.println("  Tamanho comprimido: " + tamanhoHuffman + " bytes");
            System.out.printf("  Taxa de compressão: %.2f%%\n", taxaHuffman);

            System.out.println("\nLZW:");
            System.out.println("  Tempo: " + tempoLZW + " ms");
            System.out.println("  Tamanho comprimido: " + tamanhoLZW + " bytes");
            System.out.printf("  Taxa de compressão: %.2f%%\n", taxaLZW);

            // Melhor desempenho
            System.out.println("\n===== Comparação =====");
            if (tempoHuffman < tempoLZW) {
                System.out.println("✔ Huffman foi mais rápido.");
            } else if (tempoLZW < tempoHuffman) {
                System.out.println("✔ LZW foi mais rápido.");
            } else {
                System.out.println("✔ Ambos tiveram o mesmo tempo.");
            }

            if (taxaHuffman < taxaLZW) {
                System.out.println("✔ Huffman teve melhor compressão.");
            } else if (taxaLZW < taxaHuffman) {
                System.out.println("✔ LZW teve melhor compressão.");
            } else {
                System.out.println("✔ Ambos tiveram compressão semelhante.");
            }

        } catch (Exception e) {
            System.err.println("Erro durante a comparação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
