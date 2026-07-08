package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CarroService {

    @Autowired
    public CarroRepository carroRepository;

    // Regra de negócio existente (Ano do Carro)
    private void validarRegrasDeNegocio(Carro c) {
        int anoAtual = LocalDate.now().getYear();

        if (c.getAno() > anoAtual) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ano do carro não pode ser maior que o ano atual.");
        }
    }

    public Carro save(Carro c) {
        validarRegrasDeNegocio(c);
        return carroRepository.save(c);
    }

    // Valida se o carro existe antes de atualizar para não estourar o Hibernate
    public Carro update(Carro c) {
        validarRegrasDeNegocio(c);

        // Se o ID for nulo ou não existir no banco de dados, lança 404 Not Found
        if (c.getId() == null || !carroRepository.existsById(c.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não é possível atualizar: Carro não encontrado.");
        }

        return carroRepository.save(c);
    }

    // Valida se o carro existe antes de deletar
    public void deleteById(Long id) {
        if (!carroRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não é possível deletar: Carro não encontrado.");
        }
        carroRepository.deleteById(id);
    }

    // Se buscar por um ID e não achar, já lança o 404 direto daqui
    public Carro findByIdOrThrow(Long id) {
        return carroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado com o ID: " + id));
    }

    public Optional<Carro> findById(Long id) {
        return carroRepository.findById(id);
    }

    public List<Carro> findAll() {
        return carroRepository.findAll();
    }
}