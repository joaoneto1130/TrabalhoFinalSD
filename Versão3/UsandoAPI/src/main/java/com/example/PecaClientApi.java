package com.exemplo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class PecaClientApi {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080/pecas";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Escolha uma opção: \n1 - Adicionar Peça\n2 - Listar Peças\n3 - Atualizar Peça\n4 - Deletar Peça\n5 - Sair");
            System.out.print("Opção: ");
            int opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1:
                    adicionarPeca(scanner);
                    break;
                case 2:
                    listarPecas();
                    break;
                case 3:
                    atualizarPeca(scanner);
                    break;
                case 4:
                    deletarPeca(scanner);
                    break;
                case 5:
                    System.out.println("Saindo...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void adicionarPeca(Scanner scanner) {
        System.out.print("Digite o nome da peça: ");
        String nome = scanner.nextLine();
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
        sendRequest(request);
    }

    private static void listarPecas() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
        sendRequest(request);
    }

    private static void atualizarPeca(Scanner scanner) {
        System.out.print("Digite o ID da peça a ser atualizada: ");
        String id = scanner.nextLine();
        String id2 = id;

        System.out.print("Digite o novo nome da peça: ");
        String nome = scanner.nextLine();
        System.out.print("Digite a nova quantidade de peças: ");
        int quantidade = Integer.parseInt(scanner.nextLine());

        String json = String.format("{\"nome\":\"%s\", \"codigo\":\"%s\", \"quantidade\":%d}", nome, id2, quantidade);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id2))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        sendRequest(request);
    }


    private static void deletarPeca(Scanner scanner) {
        System.out.print("Digite o ID da peça a ser deletada: ");
        String id = scanner.nextLine();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();
        sendRequest(request);
    }

    private static void sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Resposta: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
