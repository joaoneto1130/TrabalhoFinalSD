package com.exemplo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class PecaServerApi {
    public static void main(String[] args) {
        SpringApplication.run(PecaServerApi.class, args);
    }
}

@RestController
@RequestMapping("/pecas")
class PecaController {
    private final List<Peca> pecas = new ArrayList<>();

    @GetMapping
    public List<Peca> getPecas() {
        return pecas;
    }

    @PostMapping
    public void addPeca(@RequestBody Peca peca) {
        pecas.add(peca);
    }

    @GetMapping("/{codigo}")
    public Peca findPecaByCodigo(@PathVariable String codigo) {
        Optional<Peca> peca = pecas.stream()
                                   .filter(p -> p.getCodigo().equals(codigo))
                                   .findFirst();
        return peca.orElse(null);
    }

    @GetMapping("/quantidade-total")
    public int getQuantidadeTotal() {
        return pecas.stream().mapToInt(Peca::getQuantidade).sum();
    }
}

class Peca {
    private String nome;
    private String codigo;
    private int quantidade;

    public Peca() {}

    public Peca(String nome, String codigo, int quantidade) {
        this.nome = nome;
        this.codigo = codigo;
        this.quantidade = quantidade;
    }

    public String getNome() { return nome; }
    public String getCodigo() { return codigo; }
    public int getQuantidade() { return quantidade; }
}
