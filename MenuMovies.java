import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class MenuMovies {
    private MovieDAO movieDAO;
    private Scanner console = new Scanner(System.in);

    // Construtor inicializa o DAO de filmes
    public MenuMovies() throws Exception {
        movieDAO = new MovieDAO();
    }

    // Exibe o menu e gerencia as opções
    public void menu() throws Exception {
        int option;
        do {
            System.out.println("\n\nSistema de Gerenciamento Locadora Yellow");
            System.out.println("------------------------");
            System.out.println("1 - Procurar filme");
            System.out.println("2 - Adicionar filme");
            System.out.println("3 - Atualizar filme");
            System.out.println("4 - Deletar filme");
            System.out.println("5 - Importar planilha de filmes");
            System.out.println("6 - Compressão LZW");
            System.out.println("7 - Descomprimir arquivo LZW");
            System.out.println("8 - Compressão Huffman");
            System.out.println("9 - Descomprimir arquivo Huffman");
            System.out.println("0 - Sair");

            System.out.print("\nOpção: ");
            try {
                option = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                option = -1; // Captura erro de entrada inválida
            }

            // Chama a função correspondente à opção escolhida
            switch (option) {
                case 1:
                    searchMovieByTitle();
                    break;
                case 2:
                    addMovie();
                    break;
                case 3:
                    updateMovieByTitle();
                    break;
                case 4:
                    deleteMovieByTitle();
                    break;
                case 5:
                    importMoviesFromCSV();
                    break;
                case 6:
                    comprimirFilmes(movieDAO.retornFilm());
                break;
                case 7:
                    desComprimirFilmes();
                break;
                case 8:
                    comprimirFilmesHuffman(movieDAO.retornFilm());
                break;
                case 9:
                    descomprimirFilmesHuffman();
                break;
                case 0:
                    System.out.println("Saindo do menu.");
                    break;
                default:
                    System.out.println("Opção inválida!"); // Trata opções inválidas
                    break;
            }
        } while (option != 0); // Continua até o usuário escolher sair
    }

   // Comparador comparador = new Comparador();
    // Compressão usando Huffman
    public void comprimirFilmesHuffman(List<String> filmes) throws Exception {
        if (filmes == null || filmes.isEmpty()) {
            System.out.println("Lista de filmes está vazia.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String linha : filmes) {
            sb.append(linha).append("\n");
        }

        String texto = sb.toString();
        Huffman huffman = new Huffman();
        Map<Character, String> codeMap = new HashMap<>();

        String resultado = huffman.compress(texto, codeMap);

        double versao = huffman.getVersao();
        String nomeArquivo = String.format("Huffman_v%.1f.txt", versao);
        Files.writeString(Paths.get(nomeArquivo), resultado, StandardCharsets.UTF_8);
        System.out.println("Arquivo comprimido com sucesso: " + nomeArquivo);
        System.out.println("Aguarde a avaliação dos algorítimos...");
       // comparador.comparar(texto);
        huffman.incrementarVersao();
}
    // Descompressão usando Huffman
    public void descomprimirFilmesHuffman() throws Exception {
        System.out.print("\nDigite o nome do arquivo comprimido: ");
        String nomeArquivo = console.nextLine();

        String conteudo = Files.readString(Paths.get(nomeArquivo), StandardCharsets.UTF_8).trim();
        if (conteudo.isEmpty()) {
            System.out.println("Arquivo está vazio.");
            return;
        }

        Huffman huffman = new Huffman();
        String resultado;
        try {
            resultado = huffman.decompress(conteudo);
        } catch (Exception e) {
            System.out.println("Erro ao descomprimir: " + e.getMessage());
            return;
        }

        String nomeSaida = nomeArquivo.replace(".txt", "_descomprimido.txt");
        Files.write(Paths.get(nomeSaida), resultado.getBytes(StandardCharsets.UTF_8));
        System.out.println("Descompressão concluída com sucesso: " + nomeSaida);
}

    // Descompressão usando LZW
    public void desComprimirFilmes() throws Exception {
        System.out.print("\nDigite o nome do arquivo comprimido: ");
        String nomeArquivo = console.nextLine();

        String conteudo = Files.readString(Paths.get(nomeArquivo), StandardCharsets.UTF_8).trim();
        if (conteudo.isEmpty()) {
            System.out.println("Arquivo está vazio.");
            return;
        }

        List<Integer> codigos = new ArrayList<>();
        for (String s : conteudo.split("\\s+")) {
            try {
                int code = Integer.parseInt(s);
                if (code < 0) {
                    System.out.println("Código negativo ignorado: " + code);
                    continue;
                }
                codigos.add(code);
            } catch (NumberFormatException e) {
                System.out.println("Código inválido ignorado: " + s);
            }
        }

        if (codigos.isEmpty()) {
            System.out.println("Nenhum código válido lido do arquivo.");
            return;
        }

        LZW lzw = new LZW();
        String resultado;
        try {
            resultado = lzw.decompress(codigos);
        } catch (Exception e) {
            System.out.println("Erro ao descomprimir: " + e.getMessage());
            return;
        }

        String nomeSaida = nomeArquivo.replace(".txt", "_descomprimido.txt");
        Files.write(Paths.get(nomeSaida), resultado.getBytes(StandardCharsets.UTF_8));

        System.out.println("Descompressão concluída com sucesso: " + nomeSaida);
}



// Compressão usando LZW
public void comprimirFilmes(List<String> filmes) throws Exception {
    if (filmes == null || filmes.isEmpty()) {
        System.out.println("Lista de filmes está vazia. Não é possível realizar compressão.");
        return;
    }

    LZW lzw = new LZW();
    StringBuilder sb = new StringBuilder();

    for (String linha : filmes) {
        if (linha != null) {
            sb.append(linha).append("\n");
        }
    }

    String textoFinal = sb.toString();
    if (textoFinal.isEmpty()) {
        System.out.println("Nenhum conteúdo válido para comprimir.");
        return;
    }

    List<Integer> comprimido = lzw.compress(textoFinal);

    // Remover nulos
    comprimido.removeIf(Objects::isNull);

    if (comprimido.isEmpty()) {
        System.out.println("Compressão resultou em lista vazia.");
        return;
    }


    StringBuilder resultado = new StringBuilder();
    for (int code : comprimido) {
        resultado.append(code).append(" ");
    }

    double versao = lzw.getVersao();
    String nomeTxt = String.format("LZW_v%.1f.txt", versao);

    Files.writeString(Paths.get(nomeTxt), resultado.toString().trim(), StandardCharsets.UTF_8);
    System.out.println("Arquivo comprimido com sucesso: " + nomeTxt);
    System.out.println("Aguarde a avaliação dos algorítimos...");
    // comparador.comparar(textoFinal);
    lzw.incrementarVersao();
}

    // Procura um filme pelo título
    private void searchMovieByTitle() {
        System.out.print("\nTítulo do filme: ");
        String title = console.nextLine();
        try {
            Movie movie = movieDAO.findMovieByTitle(title); // Busca o filme
            if (movie != null) {
                System.out.println(movie); // Exibe o filme encontrado
                System.out.println(movie.getCast()[0]);
            } else {
                System.out.println("Filme não encontrado");
            }
        } catch (Exception e) {
            System.out.println("Error buscando filme."); // Trata erro na busca
        }
    }

    // Adiciona um novo filme
    private void addMovie() {
        System.out.println("\nAdicionando novo filme");

        // Coleta informações do filme
        System.out.print("Título: ");
        String title = console.nextLine();

        System.out.print("Data de lançamento (formato: yyyy-MM-dd): ");
        String dateStr = console.nextLine();
        LocalDate releaseDate = LocalDate.parse(dateStr); // Formata a data

        System.out.print("Generos (separados por vírgula): ");
        String[] genres = console.nextLine().split(", *");

        System.out.print("Nota: ");
        float score = Float.parseFloat(console.nextLine());

        System.out.print("Elenco (Separado por vírgula): ");
        String[] cast = console.nextLine().split(", *");

        try {
            Movie movie = new Movie(title, releaseDate, genres, score, cast); // Cria o objeto filme
            movieDAO.addMovie(movie);
            System.out.println("Filme adicionado com sucesso.");

        } catch (Exception e) {
            System.out.println("Erro adicionando filme."); // Trata erro ao adicionar filme
        }
    }

    // Atualiza um filme pelo título
    private void updateMovieByTitle() {
        System.out.print("\nDigite o nome do filme que deseja atualizar: ");
        String title = console.nextLine();

     try {
        Movie movie = movieDAO.findMovieByTitle(title); // Busca o filme
        Movie m2 = movieDAO.findMovieByTitle(title);
        if (movie == null) {
            System.out.println("Filme não encontrado.");
            return;
        }

        // Atualiza os campos do filme se o usuário inserir novos valores
        System.out.print("Novo título (Deixe em branco para não mudar): ");
        String newTitle = console.nextLine();
        if (!newTitle.trim().isEmpty()) movie.setTitle(newTitle.trim());

    // Validação da data
        while (true) {
            System.out.print("Nova data de lançamento (formato: yyyy-MM-dd, Deixe em branco para não mudar): ");
            String dateStr = console.nextLine();
            if (dateStr.trim().isEmpty()) break;
            try {
                LocalDate date = LocalDate.parse(dateStr.trim());
                movie.setReleaseDate(date);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Use o formato yyyy-MM-dd.");
            }
    }

        System.out.print("Novos gêneros (separados por vírgula), deixe em branco para não mudar: ");
        String genresStr = console.nextLine();
        if (!genresStr.trim().isEmpty()) movie.setGenres(genresStr.trim().split(",\\s*"));

    // Validação da pontuação
        while (true) {
            System.out.print("Nova pontuação (número entre 0 e 10, Deixe em branco para não mudar): ");
            String scoreStr = console.nextLine();
            if (scoreStr.trim().isEmpty()) break;
            try {
                float score = Float.parseFloat(scoreStr.trim());
                if (score < 0 || score > 10) {
                    System.out.println("Pontuação deve ser entre 0 e 10.");
                } else {
                    movie.setScore(score);
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Pontuação inválida. Digite um número decimal válido.");
            }
    }

        System.out.print("Novo elenco (separados por vírgula), deixe em branco para não mudar: ");
        String castStr = console.nextLine();
        if (!castStr.trim().isEmpty()) movie.setCast(castStr.trim().split(",\\s*"));

        // Tenta atualizar o filme
        if (movieDAO.updateMovie(m2,movie)) {
            System.out.println("Filme atualizado com sucesso");
        } else {
            System.out.println("Erro ao atualizar o filme.");
        }
        } catch (Exception e) {
            System.out.println("Erro inesperado ao atualizar o filme: " + e.getMessage());
            e.printStackTrace();
        }

    }
    // Deleta um filme pelo título
    private void deleteMovieByTitle() {
        System.out.print("\nDigite o título do filme que deseja deletar.");
        String title = console.nextLine();

        try {
            Movie movie = movieDAO.findMovieByTitle(title); // Busca o filme
            if (movie == null) {
                System.out.println("Filme não encontrado.");
                return;
            }

            // Confirma a deleção com o usuário
            System.out.print("Confirmar deleção? (S/N): ");
            char resp = console.nextLine().charAt(0);
            if (resp == 'S' || resp == 's') {
                if (movieDAO.deleteMovie(movie.getTitle())) { // Deleta o filme
                    System.out.println("Filme deletado com sucesso.");
                } else {
                    System.out.println("Erro ao deletar filme.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao deletar filme."); // Trata erro na deleção
        }
    }

    // Importa filmes de um arquivo CSV
    private void importMoviesFromCSV() {
        System.out.print("\nDigite o nome do arquivo CSV: ");
        String filePath = console.nextLine();

        try {
            movieDAO.importFromCSV(filePath); // Chama o método de importação
        } catch (Exception e) {
            System.out.println("Erro ao importar planilha"); // Trata erro de importação
        }
    }
}
