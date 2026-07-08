package br.org.edu.ifrn.lojacarro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;

@Entity
public class Carro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O modelo é obrigatório.")
    @Size(max = 50, message = "O modelo não pode ter mais de 50 caracteres.")
    // Bloqueia caracteres menores/maiores que (< >), matando tentativas de injetar tags HTML/Script
    @Pattern(regexp = "^[^<>]*$", message = "Injeção de Script detectada (XSS). Caracteres '<' e '>' não são permitidos.")
    private String modelo;

    @Min(value = 1886, message = "Ano inválido. O primeiro carro foi inventado em 1886.")
    private int ano;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }
}
