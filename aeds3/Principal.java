import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        int option;

        try {
            do {
                // Exibe o menu principal
                System.out.println("\n\nSistema de Gerenciamento Locadora Yellow");
                System.out.println("-----------------------");
                System.out.println("1 - Gerenciar filme");
                System.out.println("0 - Sair");

                // Lê a opção escolhida pelo usuário
                System.out.print("\nOpção: ");
                try {
                    option = Integer.parseInt(console.nextLine());
                } catch (NumberFormatException e) {
                    option = -1;  // Caso de erro na conversão, define opção inválida
                }

                // Processa a opção escolhida
                switch (option) {
                    case 1:
                        // Chama o menu de filmes
                        MenuMovies menuMovies = new MenuMovies();
                        menuMovies.menu();
                        break;
                    case 0:
                        // Sai do sistema
                        System.out.println("Saindo do sistema");
                        break;
                    default:
                        // Caso a opção seja inválida
                        System.out.println("Opção inválida!");
                        break;
                }
            } while (option != 0);  // Continua até que o usuário escolha sair

        } catch (Exception e) {
            // Exibe erro crítico caso ocorra exceção
            System.err.println("Erro crítico no sistema:");
            e.printStackTrace();
        } finally {
            // Fecha o scanner no final
            console.close();
        }
    }
}
