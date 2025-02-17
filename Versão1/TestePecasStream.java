import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Peca {
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

class PecasInputStream extends InputStream {
    private InputStream in;

    public PecasInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    public Peca[] readPecas() throws IOException {
        DataInputStream dataIn = new DataInputStream(in);
        int numPecas = dataIn.readInt();
        Peca[] pecas = new Peca[numPecas];

        for (int i = 0; i < numPecas; i++) {
            int nomeLength = dataIn.readInt();
            byte[] nomeBytes = new byte[nomeLength];
            dataIn.readFully(nomeBytes);
            String nome = new String(nomeBytes);
            System.out.println("Lido nome com " + nomeLength + " bytes: " + nome);

            int codigoLength = dataIn.readInt();
            byte[] codigoBytes = new byte[codigoLength];
            dataIn.readFully(codigoBytes);
            String codigo = new String(codigoBytes);
            System.out.println("Lido código com " + codigoLength + " bytes: " + codigo);

            int quantidade = dataIn.readInt();

            pecas[i] = new Peca(nome, codigo, quantidade);
        }
        return pecas;
    }
}

class PecasOutputStream extends OutputStream {
    private Peca[] pecas;
    private OutputStream out;

    public PecasOutputStream(Peca[] pecas, OutputStream out) {
        this.pecas = pecas;
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    public void writePecas() throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeInt(pecas.length);
        int pecasEnviadas = 0;
        
        for (Peca peca : pecas) {
            byte[] nomeBytes = peca.getNome().getBytes();
            byte[] codigoBytes = peca.getCodigo().getBytes();

            System.out.println("Gravando nome com " + nomeBytes.length + " bytes: " + peca.getNome());
            dataOut.writeInt(nomeBytes.length);
            dataOut.write(nomeBytes);

            System.out.println("Gravando código com " + codigoBytes.length + " bytes: " + peca.getCodigo());
            dataOut.writeInt(codigoBytes.length);
            dataOut.write(codigoBytes);

            dataOut.writeInt(peca.getQuantidade());

            pecasEnviadas++; 
        }

        System.out.println("\nNúmero de peças enviadas: " + pecasEnviadas);
    }
}

public class TestePecasStream {
    public static void main(String[] args) {
        try {
            List<Peca> listaPecas = new ArrayList<>();
            Scanner scanner = new Scanner(System.in);

            System.out.println("Digite as peças (digite 'sair' para terminar):");
            while (true) {
                System.out.print("Digite o nome da peça: ");
                String nome = scanner.nextLine();
                if (nome.equalsIgnoreCase("sair")) break;

                System.out.print("Digite o código da peça: ");
                String codigo = scanner.nextLine();

                System.out.print("Digite a quantidade de peças: ");
                int quantidade = Integer.parseInt(scanner.nextLine());

                listaPecas.add(new Peca(nome, codigo, quantidade));
            }

            Peca[] pecas = listaPecas.toArray(new Peca[0]);

            System.out.println("\nTestando saída padrão (System.out):");
            try (PecasOutputStream pos = new PecasOutputStream(pecas, System.out)) {
                pos.writePecas();
            }

            System.out.println("\nTestando escrita em arquivo (pecas.dat):");
            try (FileOutputStream fos = new FileOutputStream("pecas.dat");
                PecasOutputStream posFile = new PecasOutputStream(pecas, fos)) {
                posFile.writePecas();
            }

            System.out.println("\nLendo dados do arquivo (pecas.dat):");
            try (FileInputStream fis = new FileInputStream("pecas.dat");
                PecasInputStream pisFile = new PecasInputStream(fis)) {
                Peca[] pecasLidas = pisFile.readPecas();
                for (Peca p : pecasLidas) {
                    System.out.println("Nome: " + p.getNome() + ", Código: " + p.getCodigo() + ", Quantidade: " + p.getQuantidade());
                }
            }

            System.out.println("\nTestando comunicação com servidor remoto (TCP):");

            new Thread(() -> {
                try (ServerSocket server = new ServerSocket(5000);
                    Socket client = server.accept();
                    PecasInputStream pis = new PecasInputStream(client.getInputStream())) {
                    System.out.println("Servidor: Recebendo dados...");
                    Peca[] pecasRecebidas = pis.readPecas();
                    for (Peca p : pecasRecebidas) {
                        System.out.println("Servidor recebeu - Nome: " + p.getNome() + ", Código: " + p.getCodigo() + ", Quantidade: " + p.getQuantidade());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            try (Socket socket = new Socket("localhost", 5000);
                PecasOutputStream posSocket = new PecasOutputStream(pecas, socket.getOutputStream())) {
                System.out.println("Cliente: Enviando dados...");
                posSocket.writePecas();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
