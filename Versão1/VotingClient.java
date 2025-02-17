import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class VotingClient {
    private final String serverAddress;
    private final int serverPort;

    public VotingClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void startClient() {
        try (Socket socket = new Socket(serverAddress, serverPort);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(in.readUTF());

            String username = reader.readLine();
            out.writeUTF(username);

            System.out.println(in.readUTF());

            String password = reader.readLine();
            out.writeUTF(password);

            String loginResponse = in.readUTF();
            System.out.println(loginResponse);

            if (loginResponse.equals("Login inválido") || loginResponse.contains("servidor encerrado")) {
                System.out.println("Não foi possível conectar. Verifique suas credenciais ou o status do servidor.");
                return;
            }

            if (loginResponse.equals("Login bem-sucedido")) {
                if (username.equals("admin")) {
                    System.out.println(in.readUTF());
                    System.out.println(in.readUTF());
                    System.out.println(in.readUTF());
                    System.out.println(in.readUTF());
                    int choice = Integer.parseInt(reader.readLine());
                    out.writeUTF(String.valueOf(choice));
                    if (choice == 1) {
                        System.out.println(in.readUTF());
                        String novoCandidato = reader.readLine();
                        out.writeUTF(novoCandidato);
                        System.out.println(in.readUTF());
                    } else if (choice == 2) {
                        System.out.println(in.readUTF());
                        String candidatoRemovido = reader.readLine();
                        out.writeUTF(candidatoRemovido);
                        System.out.println(in.readUTF());
                    } else if (choice == 3) {
                        System.out.println("Resultados da votação:");
                        String result;
                        while (!(result = in.readUTF()).equals("FIM")) {
                            System.out.println(result);
                        }
                    } else {
                        System.out.println("Opção inválida.");
                    }
                } else {
                    System.out.println(in.readUTF());
                    List<String> candidates = new ArrayList<>();
                    String candidate;
                    while (!(candidate = in.readUTF()).equals("FIM")) {
                        candidates.add(candidate);
                        System.out.println(candidate);
                    }
                    
                    System.out.println("Escolha um candidato para votar:");
                    for (int i = 0; i < candidates.size(); i++) {
                        System.out.println((i + 1) + ". " + candidates.get(i));
                    }

                    int choice = Integer.parseInt(reader.readLine()) - 1;
                    String selectedCandidate = candidates.get(choice);
                    out.writeUTF(selectedCandidate);

                    String voteResponse = in.readUTF();
                    System.out.println(voteResponse);
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor. Verifique se ele está ativo e acessível.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        VotingClient client = new VotingClient("localhost", 8080);
        client.startClient();
    }
}
