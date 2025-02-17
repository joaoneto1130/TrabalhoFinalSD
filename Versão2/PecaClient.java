import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

class PecaClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            PecaService pecaService = (PecaService) registry.lookup("PecaService");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Digite as peças (digite 'sair' para terminar):");
                System.out.print("Digite o nome da peça: ");
                String nome = scanner.nextLine();
                if (nome.equalsIgnoreCase("sair")) break;

                System.out.print("Digite o código da peça: ");
                String codigo = scanner.nextLine();

                System.out.print("Digite a quantidade de peças: ");
                int quantidade = Integer.parseInt(scanner.nextLine());

                Peca peca = new Peca(nome, codigo, quantidade);
                pecaService.addPeca(peca);
            }

            List<Peca> pecas = pecaService.getPecas();
            for (Peca p : pecas) {
                System.out.println("Nome: " + p.getNome() + ", Código: " + p.getCodigo() + ", Quantidade: " + p.getQuantidade());
            }

            System.out.println("Quantidade total de peças: " + pecaService.getQuantidadeTotal());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}