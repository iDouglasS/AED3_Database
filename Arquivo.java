import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Arquivo<T extends Recordable> {
    private static final int TAM_CABECALHO = 12; // Tamanho do cabeçalho do arquivo

    private RandomAccessFile arquivo; // Arquivo de acesso aleatório

    private String nomeArquivo; // Nome do arquivo

    private Constructor<T> construtor; // Construtor do tipo T

    // Construtor que inicializa o arquivo e cria diretório se necessário
    public Arquivo(String nomeArquivo, Constructor<T> construtor) throws Exception {
        File directory = new File("./dados");
        if (!directory.exists()) directory.mkdir(); // Cria diretório 'dados' se não existir

        this.nomeArquivo = "./dados/" + nomeArquivo + ".db"; // Define caminho do arquivo
        this.construtor = construtor;
        this.arquivo = new RandomAccessFile(this.nomeArquivo, "rw");

        // Escreve cabeçalho se o arquivo for novo
        if (arquivo.length() < TAM_CABECALHO) {
            arquivo.writeInt(0);    // Último ID usado
            arquivo.writeLong(-1);  // Lista de registros deletados
        }
    }

/**
 * @return
 * @throws Exception
 */
public List<String> retornaListaFilme() throws Exception {
    seekToHeader();

    List<String> filmes = new ArrayList<>();

    while (arquivo.getFilePointer() < arquivo.length()) {
        byte lapide = arquivo.readByte();     // Lê a lápide
        short size = arquivo.readShort();     // Tamanho do registro
        byte[] data = new byte[size];

        arquivo.readFully(data); // Garante que todos os bytes são lidos

        try{
            if (lapide == ' ') { // ou 0, dependendo da sua implementação
                T obj = construtor.newInstance();
                obj.fromByteArray(data);
                filmes.add(obj.toString()); // Adiciona string do objeto
            }
        }
        catch (Exception e){
            System.out.println("Erro ao encontrar filme");
           }
    }
    return filmes;
}



    // Cria um novo objeto no arquivo e retorna o ponteiro para a arvore
    public String create(T obj) throws Exception {
        arquivo.seek(0);
        int newID = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(newID);
        obj.setId(newID);
        byte[] data = obj.toByteArray();
    
        long address = getDeleted(data.length);
        if (address == -1) {
            arquivo.seek(arquivo.length());
            address = arquivo.getFilePointer(); // <-- ponteiro exato
            arquivo.writeByte(' ');
            arquivo.writeShort(data.length);
            arquivo.write(data);
        } else {
            arquivo.seek(address);
            arquivo.writeByte(' ');
            arquivo.skipBytes(2);
            arquivo.write(data);
        }
        HashFunc funcHash = new HashFunc();
        funcHash.hashPorTitulo(obj.getTitle());
        String retorno = obj.getTitle() + "##" + address;
        return retorno;
    }
    


    // Deleta um objeto a partir do Ponteiro
    public boolean delete(long ponteiro) throws Exception {
        arquivo.seek(ponteiro);
        byte lapide = arquivo.readByte();
        short size = arquivo.readShort();
        byte[] data = new byte[size];
        arquivo.read(data);
    
        if (lapide == ' ') {
            arquivo.seek(ponteiro);
            arquivo.writeByte('*');
            addDeleted(size, ponteiro);
            return true;
        }
    
        return false;
    }
    

    // Atualiza um objeto no arquivo e retorna novo ponteiro para que seja atualizado na arvore
   public long update(long ponteiro, T newObj) throws Exception {
    if (ponteiro < 0 || ponteiro >= arquivo.length()) {
        throw new IllegalArgumentException("Ponteiro inválido.");
    }

    arquivo.seek(ponteiro);
    long position = ponteiro;

    // Verifica a lápide
    byte lapide = arquivo.readByte();
    if (lapide != ' ') {
        throw new IllegalStateException("Registro inativo ou excluído.");
    }

    // Lê o tamanho e os dados antigos
    short size = arquivo.readShort();
    byte[] data = new byte[size];
    arquivo.readFully(data);

    // Reconstrói o objeto antigo
    T objAntigo = construtor.newInstance();
    objAntigo.fromByteArray(data);

    // Confirma que é o mesmo objeto a ser atualizado
    if (objAntigo.getId() != newObj.getId()) {
        throw new IllegalArgumentException("IDs não coincidem. Não é possível atualizar.");
    }

    // Novo conteúdo
    byte[] newData = newObj.toByteArray();
    short newSize = (short) newData.length;

    if (newSize <= size) {
        // Atualiza no mesmo espaço
        arquivo.seek(position + 3); // Pula lápide (1 byte) e tamanho (2 bytes)
        arquivo.write(newData);
        return position;
    } else {
        // Marca o antigo como excluído
        arquivo.seek(position);
        arquivo.writeByte('*');
        addDeleted(size, position); // caso esteja gerenciando espaço livre

        // Escreve o novo no final
        arquivo.seek(arquivo.length());
        long newPosition = arquivo.getFilePointer();
        arquivo.writeByte(' ');
        arquivo.writeShort(newSize);
        arquivo.write(newData);
        return newPosition;
    }
}


    // Adiciona um endereço à lista de deletados
    private void addDeleted(int size, long address) throws Exception {
        long pos = 4;
        arquivo.seek(pos);
        long deletedAddress = arquivo.readLong();
        long next;

        if (deletedAddress == -1) {
            arquivo.seek(4);
            arquivo.writeLong(address);
            arquivo.seek(address + 3);
            arquivo.writeLong(-1);
        } else {
            do {
                arquivo.seek(deletedAddress + 1);
                int recordSize = arquivo.readShort();
                next = arquivo.readLong();

                if (recordSize > size) {
                    if (pos == 4)
                        arquivo.seek(pos);
                    else
                        arquivo.seek(pos + 3);

                    arquivo.writeLong(address);
                    arquivo.seek(address + 3);
                    arquivo.writeLong(deletedAddress);
                    break;
                }

                if (next == -1) {
                    arquivo.seek(deletedAddress + 3);
                    arquivo.writeLong(address);
                    arquivo.seek(address + 3);
                    arquivo.writeLong(-1);
                    break;
                }

                pos = deletedAddress;
                deletedAddress = next;
            } while (deletedAddress != -1);
        }
    }

    // Procura um espaço deletado adequado para um novo registro
    private long getDeleted(int requiredSize) throws Exception {
        long pos = 4;
        arquivo.seek(pos);
        long deletedAddress = arquivo.readLong();
        long next;
        int recordSize;

        while (deletedAddress != -1) {
            arquivo.seek(deletedAddress + 1);
            recordSize = arquivo.readShort();
            next = arquivo.readLong();

            if (recordSize > requiredSize) {
                if (pos == 4)
                    arquivo.seek(pos);
                else
                    arquivo.seek(pos + 3);
                arquivo.writeLong(next);
                return deletedAddress; // Retorna o endereço do espaço deletado
            }
            pos = deletedAddress;
            deletedAddress = next;
        }
        return -1; // Retorna -1 se não encontrar
    }

    // Procura um objeto pelo título (específico para tipo Movie)
    public T searchByPonteiro(long ponteiro) throws Exception {
        
        arquivo.seek(ponteiro);             
        byte lapide = arquivo.readByte();   
        short size = arquivo.readShort();   
        byte[] data = new byte[size];       
        arquivo.readFully(data);            

        if (lapide == ' ') {
            T obj = construtor.newInstance(); 
            obj.fromByteArray(data);          
            return obj;
            }
            return null; 
        }

    // Posiciona o ponteiro do arquivo após o cabeçalho
    private void seekToHeader() throws Exception {
        arquivo.seek(TAM_CABECALHO);
    }

    // Fecha o arquivo
    public void close() throws Exception {
        arquivo.close();
    }

}
