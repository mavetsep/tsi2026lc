package br.org.edu.ifrn.lojacarro.dto;

public class CarroResponseDto {

    private Long id;
    private String modelo;
    private Integer ano;

    public CarroResponseDto() {
    }

    public CarroResponseDto(Long id, String modelo, Integer ano) {
        this.id = id;
        this.modelo = modelo;
        this.ano = ano;
    }

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

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}