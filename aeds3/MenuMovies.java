import java.util.Scanner;
import java.time.LocalDate;

public class MenuMovies {
    private MovieDAO movieDAO;
    private Scanner console = new Scanner(System.in);

    // Construtor inicializa o DAO de filmes
    public MenuMovies() throws Exception {
        movieDAO = new MovieDAO();
    }

    // Exibe o menu e gerencia as opções
    public void menu() {
        int option;
        do {
            System.out.println("\n\nSistema de Gerenciamento Locadora Yellow");
            System.out.println("------------------------");
            System.out.println("1 - Procurar filme");
            System.out.println("2 - Adicionar filme");
            System.out.println("3 - Atualizar filme");
            System.out.println("4 - Deletar filme");
            System.out.println("5 - Importar planilha de filmes");
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
                case 0:
                    System.out.println("Saindo do menu.");
                    break;
                default:
                    System.out.println("Opção inválida!"); // Trata opções inválidas
                    break;
            }
        } while (option != 0); // Continua até o usuário escolher sair
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
            if (movieDAO.addMovie(movie)) {
                System.out.println("Filme adicionado com sucesso.");
            } else {
                System.out.println("Erro adicionando filme.");
            }
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
            if (movie == null) {
                System.out.println("Filme não encontrado.");
                return;
            }

            // Atualiza os campos do filme se o usuário inserir novos valores
            System.out.print("Novo título (Deixe em branco para não mudar): ");
            String newTitle = console.nextLine();
            if (!newTitle.isEmpty()) movie.setTitle(newTitle);

            System.out.print("Nova data de lançamento (formato: yyyy-MM-dd, Deixe em branco para não mudar): ");
            String dateStr = console.nextLine();
            if (!dateStr.isEmpty()) movie.setReleaseDate(LocalDate.parse(dateStr));

            System.out.print("Novos gêneros (separados por vírgula), deixe em branco para não mudar): ");
            String genresStr = console.nextLine();
            if (!genresStr.isEmpty()) movie.setGenres(genresStr.split(", *"));

            System.out.print("Nova pontuação (Deixe em branco para não mudar): ");
            String scoreStr = console.nextLine();
            if (!scoreStr.isEmpty()) movie.setScore(Float.parseFloat(scoreStr));

            System.out.print("Novo elenco (separados por vírgula), deixe em branco para não mudar): ");
            String castStr = console.nextLine();
            if (!castStr.isEmpty()) movie.setCast(castStr.split(", *"));

            if (movieDAO.updateMovie(movie)) {
                System.out.println("Filme atualizado com sucesso");
            } else {
                System.out.println("Erro ao atualizar o filme.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar o filme."); // Trata erro na atualização
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
