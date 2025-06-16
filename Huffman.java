import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Huffman {

    private static final String VERSAO_PATH = "versao_huffman.txt";

    private static class Node implements Comparable<Node> {
        char ch;
        int freq;
        Node left, right;

        Node(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }

        Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compareTo(Node o) {
            return this.freq - o.freq;
        }
    }

    private void buildCode(Node node, String code, Map<Character, String> map) {
        if (node == null) return;
        if (node.isLeaf()) {
            map.put(node.ch, code);
            return;
        }
        buildCode(node.left, code + "0", map);
        buildCode(node.right, code + "1", map);
    }

    public String compress(String input, Map<Character, String> codeMap) throws IOException {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : input.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            Node n1 = pq.poll();
            Node n2 = pq.poll();
            pq.add(new Node('\0', n1.freq + n2.freq, n1, n2));
        }

        Node root = pq.poll();
        buildCode(root, "", codeMap);

        // Serializa a árvore
        ByteArrayOutputStream treeOut = new ByteArrayOutputStream();
        writeTree(root, new DataOutputStream(treeOut));
        byte[] treeBytes = treeOut.toByteArray();

        // Codifica em bits reais usando BitSet
        BitSet bitSet = new BitSet();
        int bitIndex = 0;
        for (char c : input.toCharArray()) {
            String code = codeMap.get(c);
            for (char bit : code.toCharArray()) {
                if (bit == '1') bitSet.set(bitIndex);
                bitIndex++;
            }
        }

        byte[] bitData = bitSet.toByteArray();

        // Concatena: [treeLength][tree][bitLength][bitData]
        ByteArrayOutputStream finalOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(finalOut);
        dataOut.writeInt(treeBytes.length);
        dataOut.write(treeBytes);
        dataOut.writeInt(bitIndex); // quantidade real de bits usados
        dataOut.write(bitData);

        byte[] finalBytes = finalOut.toByteArray();
        return Base64.getEncoder().encodeToString(finalBytes);
    }

    public String decompress(String compressed) throws IOException {
        byte[] allBytes = Base64.getDecoder().decode(compressed);
        DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(allBytes));

        int treeLength = dataIn.readInt();
        byte[] treeBytes = new byte[treeLength];
        dataIn.readFully(treeBytes);
        Node root = readTree(new DataInputStream(new ByteArrayInputStream(treeBytes)));

        int bitLength = dataIn.readInt();
        byte[] bitData = dataIn.readNBytes((bitLength + 7) / 8);
        BitSet bitSet = BitSet.valueOf(bitData);

        StringBuilder decoded = new StringBuilder();
        Node current = root;
        for (int i = 0; i < bitLength; i++) {
            current = bitSet.get(i) ? current.right : current.left;
            if (current.isLeaf()) {
                decoded.append(current.ch);
                current = root;
            }
        }

        return decoded.toString();
    }

    private void writeTree(Node node, DataOutputStream out) throws IOException {
        if (node.isLeaf()) {
            out.writeBoolean(true);
            out.writeChar(node.ch);
        } else {
            out.writeBoolean(false);
            writeTree(node.left, out);
            writeTree(node.right, out);
        }
    }

    private Node readTree(DataInputStream in) throws IOException {
        if (in.readBoolean()) {
            return new Node(in.readChar(), 0);
        }
        Node left = readTree(in);
        Node right = readTree(in);
        return new Node('\0', 0, left, right);
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
