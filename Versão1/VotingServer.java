import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VotingServer {
    private final int port;
    private volatile boolean running = true;
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final List<String> candidates = new ArrayList<>(Arrays.asList("Candidato1", "Candidato2", "Candidato3"));
    private final Map<String, Integer> voteCounts = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public VotingServer(int port) {
        this.port = port;
        users.put("admin", "admin");
        users.put("user1", "voter");
        users.put("user2", "voter");
        users.put("user3", "voter");
        users.put("user4", "voter");

        for (String candidate : candidates) {
            voteCounts.put(candidate, 0);
        }
    }

    public void startServer() {
        scheduler.schedule(() -> {
            System.out.println(java.time.LocalDateTime.now() + " - Tempo limite atingido. Somente admin pode logar.");
            running = false;
        }, 10, TimeUnit.MINUTES);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(java.time.LocalDateTime.now() + " - Servidor de voto iniciado na porta " + port);

            while (true) {
                System.out.println(java.time.LocalDateTime.now() + " - Aguardando conexões...");
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (SocketException e) {
                    System.out.println(java.time.LocalDateTime.now() + " - Erro ao aceitar conexão: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scheduler.shutdown();
            System.out.println(java.time.LocalDateTime.now() + " - Servidor encerrado.");
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

                out.writeUTF("Digite seu nome de usuário:");
                out.flush();
                String username = in.readUTF();

                out.writeUTF("Digite sua senha:");
                out.flush();
                String password = in.readUTF();

                if (!running && !username.equals("admin")) {
                    out.writeUTF("O período de votação terminou. Apenas o admin pode acessar.");
                    out.flush();
                    return;
                }

                if (!users.containsKey(username) || !users.get(username).equals(password)) {
                    out.writeUTF("Login inválido");
                    out.flush();
                    return;
                }

                System.out.println(java.time.LocalDateTime.now() + " - Usuário logado: " + username);
                out.writeUTF("Login bem-sucedido");

                if (username.equals("admin")) {
                    handleAdminActions(in, out);
                } else {
                    handleUserVoting(in, out);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleAdminActions(DataInputStream in, DataOutputStream out) throws IOException {
            out.writeUTF("Você é um administrador. O que deseja fazer?");
            out.writeUTF("1. Adicionar Candidato");
            out.writeUTF("2. Remover Candidato");
            out.writeUTF("3. Visualizar Resultados");
            out.flush();

            int adminChoice = Integer.parseInt(in.readUTF());

            if (adminChoice == 1) {
                out.writeUTF("Digite o nome do novo candidato:");
                String newCandidate = in.readUTF();
                candidates.add(newCandidate);
                voteCounts.put(newCandidate, 0);
                out.writeUTF("Candidato " + newCandidate + " adicionado com sucesso.");
            } else if (adminChoice == 2) {
                out.writeUTF("Digite o nome do candidato a ser removido:");
                String removeCandidate = in.readUTF();
                if (candidates.remove(removeCandidate)) {
                    voteCounts.remove(removeCandidate);
                    out.writeUTF("Candidato " + removeCandidate + " removido com sucesso.");
                } else {
                    out.writeUTF("Candidato não encontrado.");
                }
            } else if (adminChoice == 3) {
                out.writeUTF("Resultados da votação:");
                for (String candidate : candidates) {
                    out.writeUTF(candidate + ": " + voteCounts.get(candidate) + " votos");
                }
            } else {
                out.writeUTF("Opção inválida.");
            }
        }

        private void handleUserVoting(DataInputStream in, DataOutputStream out) throws IOException {
            out.writeUTF("Lista de Candidatos:");
            for (String candidate : candidates) {
                out.writeUTF(candidate);
            }
            out.writeUTF("FIM");

            String vote = in.readUTF();
            System.out.println(java.time.LocalDateTime.now() + " - Voto: " + vote);

            if (candidates.contains(vote)) {
                voteCounts.put(vote, voteCounts.get(vote) + 1);
                out.writeUTF("Voto registrado com sucesso para " + vote);
            } else {
                out.writeUTF("Voto inválido.");
            }
        }
    }

    public static void main(String[] args) {
        VotingServer server = new VotingServer(8080);
        server.startServer();
    }
}
