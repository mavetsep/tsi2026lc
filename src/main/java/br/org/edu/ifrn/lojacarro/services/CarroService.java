package br.org.edu.ifrn.lojacarro.services;

import br.org.edu.ifrn.lojacarro.model.Carro;
import br.org.edu.ifrn.lojacarro.repository.CarroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class CarroService {

    private final CarroRepository carroRepository;

    public CarroService(CarroRepository carroRepository) {
        this.carroRepository = carroRepository;
    }

    private void validarRegrasDeNegocio(Carro carro) {
        int anoAtual = LocalDate.now(ZoneId.of("America/Sao_Paulo")).getYear();

        if (carro.getAno() > anoAtual) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ano do carro não pode ser maior que o ano atual."
            );
        }
    }

    public Carro save(Carro carro) {
        validarRegrasDeNegocio(carro);
        return carroRepository.save(carro);
    }

    public Carro update(Carro carro) {
        validarRegrasDeNegocio(carro);

        if (carro.getId() == null || !carroRepository.existsById(carro.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Não é possível atualizar: Carro não encontrado."
            );
        }

        return carroRepository.save(carro);
    }

    public void deleteById(Long id) {
        if (!carroRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Não é possível deletar: Carro não encontrado."
            );
        }
        carroRepository.deleteById(id);
    }

    public Carro findByIdOrThrow(Long id) {
        return carroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Carro não encontrado com o ID: " + id
                ));
    }

    public Optional<Carro> findById(Long id) {
        return carroRepository.findById(id);
    }

    public List<Carro> findAll() {
        return carroRepository.findAll();
    }
}