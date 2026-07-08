package br.org.edu.ifrn.lojacarro.dto;

public class CarroRequestDto {

    private String modelo;
    private Integer ano;

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}