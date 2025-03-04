package com.exemplo;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Scanner;

public class PecaClientApi {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080/pecas";

    public static void main(String[] args) {
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

            String json = String.format("{\"nome\":\"%s\", \"codigo\":\"%s\", \"quantidade\":%d}", nome, codigo, quantidade);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            try {
                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Obter lista de peças
        HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
        try {
            HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("Lista de Peças: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Obter quantidade total de peças
        HttpRequest totalRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/quantidade-total"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(totalRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("Quantidade total de peças: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        scanner.close();
    }
}
