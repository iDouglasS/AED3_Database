import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MovieDAO {
    private Arquivo<Movie> movieFile;
    private BPlusTree arvore;
    // Construtor, inicializa o arquivo de filmes

    public MovieDAO() throws Exception {
        movieFile = new Arquivo<>("movies", Movie.class.getConstructor());
        this.arvore = new BPlusTree();
    }

    public List<String> retornFilm() throws Exception{
       
        return  movieFile.retornaListaFilme();
    }

    // Procura um filme pelo título
    public Movie findMovieByTitle(String title) throws Exception {

        long ponteiro = this.arvore.search(title);

        Movie m = movieFile.searchByPonteiro(ponteiro);
        return m;
    }

    // Adiciona um filme no arquivo
    public void addMovie(Movie movie) throws Exception {
        String filme =  movieFile.create(movie);

        String[] vetor = filme.split("##");

        String titulo = vetor[0];
        long ponteiro = Long.parseLong(vetor[1]);
        this.arvore.insert(titulo, ponteiro);

    }

    // Atualiza um filme no arquivo
    public boolean updateMovie(Movie m2,Movie newMovie) throws Exception {
        try {
            // Busca o ponteiro atual usando o título (pode ser nulo!)
            Long oldPointerObj = arvore.search(m2.getTitle());
            if (oldPointerObj == null) {
                System.out.println("Filme não encontrado na árvore.");
                return false;
            }

            long oldPointer = oldPointerObj;

            // Carrega o filme original a partir do ponteiro
            Movie oldMovie = movieFile.searchByPonteiro(oldPointer);
            if (oldMovie == null) {
                System.out.println("Registro físico do filme não encontrado.");
                return false;
            }

            // Atualiza o registro no arquivo
            long newPointer = movieFile.update(oldPointer, newMovie);

            // Se o título mudou, remove o antigo da árvore e insere o novo
            if (!oldMovie.getTitle().equals(newMovie.getTitle())) {
                arvore.delete(oldMovie.getTitle());
                arvore.insert(newMovie.getTitle(), newPointer);
            } else {
                arvore.update(newMovie.getTitle(), newPointer);
            }

            return true;
        } catch (Exception e) {
            System.out.println("Erro ao atualizar o filme: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
}

    
    

    // Deleta um filme pelo título
    public boolean deleteMovie(String title) throws Exception {

        Long ponteiro = arvore.search(title);
    
        if (ponteiro != null) {
            arvore.delete(title);
            movieFile.delete(ponteiro);
            return true;
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

    public BPlusTree getArvore(){
        return arvore;
    }
}

