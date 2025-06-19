# AED3_Database
## 📽️ Sistema de Gerenciamento da Locadora Yellow

PUC Minas
📚 Curso: Engenharia da Computação
🖥️ Disciplina: Algoritmos e Estruturas de Dados III
👨‍🏫 Professor: Walisson Ferreira de Carvalho

👥 Alunos

Wander Júnior Cruz (844970)

Douglas Silva Santana (874490)

    🚀 Etapa 1: Criação da Base de Dados : ✅ Implementação: Concluída
    🚀 Etapa 2: Indexação: Implementação : ✅ Implementação: Concluída
    🚀 Etapa 3: Compactação : ✅ Implementação: Concluída
    🚀 Etapa 4: Casamento de Padrões e Criptografia: ✅ Implementação: Concluída



## 📌 Descrição do Projeto

O sistema simula o gerenciamento de uma locadora de filmes chamada Yellow. Ele permite realizar operações sobre um banco de dados de filmes de maneira simples e eficiente.

## 🛠️ Funcionalidades

Ao iniciar o programa, o usuário terá duas opções no menu principal:

### 1️⃣ Gerenciar Filmes

    🔍 Procurar um filme pelo título
    
    ➕ Adicionar um novo filme
    
    ✏️ Atualizar informações de um filme existente
    
    ❌ Deletar um filme do banco de dados
    
    📂 Importar uma planilha de filmes no formato CSV
    
    ✇ Compactar e descompactar o banco de dados usando o algoritmo LZW e Huffman
    
    📑 Buscar palavras em todo o arquivo e verificar suas ocorrências

### 🔒 Criptografia

Os campos `genres` e `cast` são criptografados utilizando, respectivamente, as cifras de César e de Vigenère. A implementação desses algoritmos está localizada no arquivo `Encryption.java`, sendo utilizada no arquivo `Movie.java` nos métodos getters, setters, no construtor e na função `toString()`. Isso garante que os campos sejam sempre armazenados criptografados na base de dados e descriptografados automaticamente quando necessário para leitura.


### 📥 Importação da Planilha de Filmes

No primeiro acesso, é necessário importar a base de dados no formato CSV. Para isso, insira o caminho do arquivo no seguinte formato:

caminho\para\o\arquivo.csv

Após a importação, todas as funcionalidades estarão disponíveis para gerenciamento dos filmes.

O repositório atualmente já contem um arquivo chamado `imdb_movies.csv` que pode ser usado para importação inicial.

## 🚀 Como rodar o projeto

### ✅ Pré-requisitos
- Java JDK 17 ou superior instalado.

#### 🔧 Como rodar o projeto

Clone o repositório usando o comando:

```bash
git clone https://github.com/iDouglasS/AED3_Database.git
```

Abra o terminal dentro da pasta do projeto e execute:

```bash
javac *.java # compila o projeto
java Principal # executa o arquivo principal
```
