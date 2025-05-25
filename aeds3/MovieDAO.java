import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MovieDAO {
    private Arquivo<Movie> movieFile;

    // Construtor, inicializa o arquivo de filmes
    public MovieDAO() throws Exception {
        movieFile = new Arquivo<>("movies", Movie.class.getConstructor());
    }

    // Procura um filme pelo título
    public Movie findMovieByTitle(String title) throws Exception {
        return movieFile.searchByTitle(title);
    }

    // Adiciona um filme no arquivo
    public boolean addMovie(Movie movie) throws Exception {
        return movieFile.create(movie) > 0;
    }

    // Atualiza um filme no arquivo
    public boolean updateMovie(Movie movie) throws Exception {
        return movieFile.update(movie);
    }

    // Deleta um filme pelo título
    public boolean deleteMovie(String title) throws Exception {
        Movie movie = findMovieByTitle(title);
        if (movie != null) {
            return movieFile.delete(movie.getId());
        }
        return false;
    }

    // Importa filmes de um arquivo CSV
    public void importFromCSV(String filePath) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int count = 0;
        int skipped = 0;
        reader.readLine(); // Pular o cabeçalho
    
        while ((line = reader.readLine()) != null) {
            try (Scanner scanner = new Scanner(line)) {
                scanner.useDelimiter(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Regex para ignorar vírgulas dentro de aspas
    
                String title = scanner.next().replace("\"", "").trim();
                LocalDate releaseDate = LocalDate.parse(scanner.next().replace("\"", "").trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                float score = Float.parseFloat(scanner.next().replace("\"", "").trim());
                String[] genres = scanner.next().replace("\"", "").trim().split(", *");
                String _overview = scanner.next().replace("\"", "").trim(); // Se precisar
                String[] cast = scanner.next().replace("\"", "").trim().split(", *");
    
                Movie movie = new Movie(title, releaseDate, genres, score, cast);
                addMovie(movie);
                count++;
            } catch (Exception e) {
                skipped++;
                System.out.println("Skipping line due to error: " + line);
                e.printStackTrace();
            }
        }
    
        reader.close();
        System.out.println("Imported " + count + " movies from CSV. Skipped " + skipped + " invalid lines.");
    }
    

    // Fecha o arquivo de filmes
    public void close() throws Exception {
        movieFile.close();
    }
}
