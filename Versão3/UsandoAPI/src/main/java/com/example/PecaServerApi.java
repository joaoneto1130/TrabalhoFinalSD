package com.exemplo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> addPeca(@RequestBody Peca peca) {
        synchronized (pecas) {
            pecas.add(peca);
        }
        return ResponseEntity.ok("Peça adicionada com sucesso!");
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Peca> findPecaByCodigo(@PathVariable String codigo) {
        Optional<Peca> peca = pecas.stream()
                                   .filter(p -> p.getCodigo().equals(codigo))
                                   .findFirst();
        return peca.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<String> atualizarPeca(@PathVariable String codigo, @RequestBody Peca novaPeca) {
        synchronized (pecas) {
            for (int i = 0; i < pecas.size(); i++) {
                if (pecas.get(i).getCodigo().equals(codigo)) {
                    pecas.set(i, novaPeca);
                    return ResponseEntity.ok("Peça atualizada com sucesso!");
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<String> deletarPeca(@PathVariable String codigo) {
        synchronized (pecas) {
            Optional<Peca> peca = pecas.stream()
                                       .filter(p -> p.getCodigo().equals(codigo))
                                       .findFirst();
            if (peca.isPresent()) {
                pecas.remove(peca.get());
                return ResponseEntity.ok("Peça removida com sucesso!");
            }
        }
        return ResponseEntity.notFound().build();
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
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
