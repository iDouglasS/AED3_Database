import java.io.IOException;

public interface Recordable {
    // Define o ID do objeto
    public void setId(int i);

    // Retorna o ID do objeto
    public int getId();

    // Converte o objeto para um array de bytes
    public byte[] toByteArray() throws IOException;

    // Converte um array de bytes para o objeto
    public void fromByteArray(byte[] b) throws IOException;
}
