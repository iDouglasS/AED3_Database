import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

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

    // Cria um novo objeto no arquivo
    public int create(T obj) throws Exception {
        arquivo.seek(0);
        int newID = arquivo.readInt() + 1; // Gera novo ID
        arquivo.seek(0);
        arquivo.writeInt(newID); // Atualiza ID
        obj.setId(newID);
        byte[] data = obj.toByteArray(); // Converte objeto para byte array

        long address = getDeleted(data.length); // Procura espaço para o novo registro
        if (address == -1) { // Se não encontrar, adiciona no final
            arquivo.seek(arquivo.length());
            address = arquivo.getFilePointer();
            arquivo.writeByte(' ');  // Marca como ativo
            arquivo.writeShort(data.length);
            arquivo.write(data);
        } else { // Se encontrar, reusa o espaço deletado
            arquivo.seek(address);
            arquivo.writeByte(' ');  // Marca como ativo
            arquivo.skipBytes(2);
            arquivo.write(data);
        }
        return obj.getId(); // Retorna o ID do objeto
    }

    // Lê um objeto a partir do ID
    public T read(int id) throws Exception {
        seekToHeader(); // Pula o cabeçalho
        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte(); // Verifica se o registro foi deletado
            short size = arquivo.readShort(); // Lê o tamanho do registro
            byte[] data = new byte[size];
            arquivo.read(data); // Lê os dados do registro

            if (lapide == ' ') { // Se não for deletado
                T obj = construtor.newInstance();
                obj.fromByteArray(data); // Converte byte array para objeto
                if (obj.getId() == id) {
                    return obj; // Retorna o objeto correspondente ao ID
                }
            }
        }
        return null; // Retorna null se não encontrar
    }

    // Deleta um objeto a partir do ID
    public boolean delete(int id) throws Exception {
        seekToHeader(); // Pula o cabeçalho
        while (arquivo.getFilePointer() < arquivo.length()) {
            long position = arquivo.getFilePointer();
            byte lapide = arquivo.readByte();
            short size = arquivo.readShort();
            byte[] data = new byte[size];
            arquivo.read(data);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(data);
                if (obj.getId() == id) {
                    arquivo.seek(position);
                    arquivo.writeByte('*'); // Marca como deletado
                    addDeleted(size, position); // Adiciona à lista de deletados
                    return true;
                }
            }
        }
        return false; // Retorna false se não encontrar o objeto
    }

    // Atualiza um objeto no arquivo
    public boolean update(T newObj) throws Exception {
        seekToHeader();
        while (arquivo.getFilePointer() < arquivo.length()) {
            long position = arquivo.getFilePointer();
            byte lapide = arquivo.readByte();
            short size = arquivo.readShort();
            byte[] data = new byte[size];
            arquivo.read(data);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(data);
                if (obj.getId() == newObj.getId()) {
                    byte[] newData = newObj.toByteArray();
                    short newSize = (short) newData.length;

                    if (newSize <= size) {
                        arquivo.seek(position + 3);
                        arquivo.write(newData); // Atualiza no mesmo espaço
                    } else { // Se o novo objeto for maior, realoca
                        arquivo.seek(position);
                        arquivo.writeByte('*');
                        addDeleted(size, position);

                        long newAddress = getDeleted(newData.length);
                        if (newAddress == -1) {
                            arquivo.seek(arquivo.length());
                            newAddress = arquivo.getFilePointer();
                            arquivo.writeByte(' ');
                            arquivo.writeShort(newSize);
                            arquivo.write(newData);
                        } else {
                            arquivo.seek(newAddress);
                            arquivo.writeByte(' ');
                            arquivo.skipBytes(2);
                            arquivo.write(newData);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
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
    public T searchByTitle(String title) throws Exception {
        seekToHeader();
        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short size = arquivo.readShort();
            byte[] data = new byte[size];
            arquivo.read(data);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(data);
                if (obj instanceof Movie && ((Movie) obj).getTitle().equalsIgnoreCase(title.trim())) {
                    return obj; // Retorna o objeto se o título corresponder
                }
            }
        }
        return null; // Retorna null se não encontrar
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
