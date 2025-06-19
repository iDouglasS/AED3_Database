import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Kmp {

    // Método que busca o padrão no texto e retorna trechos com contexto
    public List<String> searchWithContext(String pattern, String text, int contexto) {
        List<String> contextos = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        int[] lps = buildLPSArray(pattern); // constrói o array de prefixos

        int i = 0; 
        int j = 0; 

        // percorre o texto
        while (i < n) {
            
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                // se encontrou o padrão completo
                if (j == m) {
                    int matchStart = i - j; 

                    // calcula início e fim do trecho com contexto
                    int inicio = Math.max(0, matchStart - contexto);
                    int fim = Math.min(n, matchStart + m + contexto);

                    
                    String contextoExtraido = text.substring(inicio, fim)
                        .replaceAll("(?i)(" + Pattern.quote(pattern) + ")", "**$1**");

                   
                    contextos.add("[...] " + contextoExtraido.trim() + " [...]");

                    
                    j = lps[j - 1];
                }
            } else {
                // se houve erro, volta j com base no LPS ou avança i
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return contextos; 
    }

    // Método auxiliar que monta o array de prefixo
    private static int[] buildLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m]; 
        int len = 0;
        lps[0] = 0; 
        int i = 1;

        // percorre o padrão
        while (i < m) {
            
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps; 
    }
}
