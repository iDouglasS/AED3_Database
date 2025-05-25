import java.io.*;
import java.time.LocalDate;

public class Movie implements Recordable {
    private int id;
    private String title;
    private LocalDate releaseDate;
    private String[] genres;
    private float score;
    private String[] cast;

    // Construtor padrão
    public Movie() {
        this.id = -1;
        this.title = "";
        this.releaseDate = LocalDate.now();
        this.genres = new String[0];
        this.score = 0f;
        this.cast = new String[0];
    }

    // Construtor com parâmetros para inicializar o filme
    public Movie(String title, LocalDate releaseDate, String[] genres, float score, String[] cast) {
        this.id = -1;
        this.title = title;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.score = score;
        this.cast = cast;
    }

    // Getters e setters para os atributos do filme
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String[] getGenres() { return genres; }
    public void setGenres(String[] genres) { this.genres = genres; }
    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }
    public String[] getCast() { return cast; }
    public void setCast(String[] cast) { this.cast = cast; }

    // Converte o filme para um array de bytes (para gravação)
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Grava os dados do filme
        dos.writeInt(this.id);
        dos.writeUTF(this.title);
        dos.writeLong(this.releaseDate.toEpochDay());

        // Grava os gêneros
        dos.writeInt(this.genres.length);
        for (String genre : this.genres) {
            dos.writeUTF(genre);
        }

        dos.writeFloat(this.score);

        // Grava o elenco
        dos.writeInt(this.cast.length);
        for (String member : this.cast) {
            dos.writeUTF(member);
        }

        return baos.toByteArray(); // Retorna os dados em formato de byte array
    }

    // Converte um array de bytes para os dados do filme
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        // Lê os dados gravados
        this.id = dis.readInt();
        this.title = dis.readUTF();
        this.releaseDate = LocalDate.ofEpochDay(dis.readLong());

        int genresCount = dis.readInt();
        this.genres = new String[genresCount];
        for (int i = 0; i < genresCount; i++) {
            this.genres[i] = dis.readUTF();
        }

        this.score = dis.readFloat();

        int castCount = dis.readInt();
        this.cast = new String[castCount];
        for (int i = 0; i < castCount; i++) {
            this.cast[i] = dis.readUTF();
        }
    }

    // Exibe os dados do filme em formato legível
    @Override
    public String toString() {
        return "\nID: " + this.id +
                "\nTitle: " + this.title +
                "\nRelease Date: " + this.releaseDate +
                "\nGenres: " + String.join(", ", this.genres) +
                "\nScore: " + this.score +
                "\nCast: " + String.join(", ", this.cast);
    }
}
