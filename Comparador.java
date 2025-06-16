import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

public class Comparador {

    public void comparar(String texto) {
        Huffman huffman = new Huffman();
        LZW lzw = new LZW();

        try {
            // Tamanho original em bytes
            int originalSize = texto.getBytes(StandardCharsets.UTF_8).length;

            // ===== HUFFMAN =====
            Map<Character, String> codeMap = new HashMap<>();
            long inicioHuffman = System.currentTimeMillis();
            String comprimidoHuffman = huffman.compress(texto, codeMap);
            long fimHuffman = System.currentTimeMillis();
            long tempoHuffman = fimHuffman - inicioHuffman;
            int tamanhoHuffman = comprimidoHuffman.getBytes(StandardCharsets.UTF_8).length;
            double taxaHuffman = ((double) tamanhoHuffman / originalSize) * 100;

            // ===== LZW =====
            long inicioLZW = System.currentTimeMillis();
            byte[] comprimidoLZW = lzw.compress(texto);
            long fimLZW = System.currentTimeMillis();
            long tempoLZW = fimLZW - inicioLZW;
            int tamanhoLZW = comprimidoLZW.length;
            double taxaLZW = ((double) tamanhoLZW / originalSize) * 100;

            // ===== RESULTADOS =====
            System.out.println("===== Resultados da Compressão =====");
            System.out.println("Tamanho original: " + originalSize + " bytes");

            System.out.println("\n>> Huffman:");
            System.out.println("  Tempo de compressão: " + tempoHuffman + " ms");
            System.out.println("  Tamanho comprimido: " + tamanhoHuffman + " bytes");
            System.out.printf("  Taxa de compressão: %.2f%%\n", taxaHuffman);

            System.out.println("\n>> LZW:");
            System.out.println("  Tempo de compressão: " + tempoLZW + " ms");
            System.out.println("  Tamanho comprimido: " + tamanhoLZW + " bytes");
            System.out.printf("  Taxa de compressão: %.2f%%\n", taxaLZW);

            // ===== COMPARAÇÃO FINAL =====
            System.out.println("\n===== Comparação de Desempenho =====");

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
