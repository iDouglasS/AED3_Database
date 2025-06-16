public class HashFunc {

    public  int hashPorTitulo(String titulo) {
        int soma = 0;
        for (int i = 0; i < titulo.length(); i++) {
            soma += (int) titulo.charAt(i);
        }

        int indice = (soma * 31) % 1000; 
        return indice;
    }

    public static int hashPorID(int id) {
        return (id * 17 + 3) % 1000;
    }

    public int hashComColisao(String titulo, int tentativa) {
        int base = hashPorTitulo(titulo);
        return (base + tentativa * tentativa) % 1000; 
    }
}
