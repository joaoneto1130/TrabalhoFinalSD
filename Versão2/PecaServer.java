import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Interface remota
interface PecaService extends Remote {
    List<Peca> getPecas() throws RemoteException;
    void addPeca(Peca peca) throws RemoteException;
    Peca findPecaByCodigo(String codigo) throws RemoteException;
    int getQuantidadeTotal() throws RemoteException;
}

// Implementação do serviço remoto
class PecaServiceImpl extends UnicastRemoteObject implements PecaService {
    private List<Peca> pecas;

    protected PecaServiceImpl() throws RemoteException {
        super();
        pecas = new ArrayList<>();
    }

    @Override
    public List<Peca> getPecas() throws RemoteException {
        return pecas;
    }

    @Override
    public void addPeca(Peca peca) throws RemoteException {
        pecas.add(peca);
    }

    @Override
    public Peca findPecaByCodigo(String codigo) throws RemoteException {
        return pecas.stream()
                   .filter(p -> p.getCodigo().equals(codigo))
                   .findFirst()
                   .orElse(null);
    }

    @Override
    public int getQuantidadeTotal() throws RemoteException {
        return pecas.stream().mapToInt(Peca::getQuantidade).sum();
    }
}

// Classes POJOs
class Peca implements Serializable {
    private String nome;
    private String codigo;
    private int quantidade;

    public Peca(String nome, String codigo, int quantidade) {
        this.nome = nome;
        this.codigo = codigo;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getQuantidade() {
        return quantidade;
    }
}

class Amortecedor extends Peca {
    public Amortecedor(String nome, String codigo, int quantidade) {
        super(nome, codigo, quantidade);
    }
}

class Motor extends Peca {
    public Motor(String nome, String codigo, int quantidade) {
        super(nome, codigo, quantidade);
    }
}

class Pneu extends Peca {
    public Pneu(String nome, String codigo, int quantidade) {
        super(nome, codigo, quantidade);
    }
}

// Servidor RMI
public class PecaServer {
    public static void main(String[] args) {
        try {
            PecaService pecaService = new PecaServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("PecaService", pecaService);
            System.out.println("Servidor RMI pronto.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Cliente RMI
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