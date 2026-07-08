
package br.org.edu.ifrn.lojacarro.controllers;


import br.org.edu.ifrn.lojacarro.model.Carro;
import br.org.edu.ifrn.lojacarro.services.CarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/carro")
public class CarroController {

    @Autowired
    private CarroService carroService;

    // Salvar carro (corrigido para POST)
    @PostMapping("salvar")
    @PreAuthorize("hasRole('GERENTE')") // Só gerente salva
    public ResponseEntity<Carro> salvarCarro(@Valid @RequestBody Carro c) {
        Carro savedCarro = carroService.save(c);
        return ResponseEntity.ok(savedCarro);
    }

    // Atualizar carro (por ID)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')") // Só gerente atualiza
    public ResponseEntity<Carro> atualizarCarro(@PathVariable Long id, @RequestBody Carro c) {
        c.setId(id); // Garante que o objeto vai com o ID da URL
        Carro atualizado = carroService.update(c); // O service vai validar se existe
        return ResponseEntity.ok(atualizado);
    }

    // Deletar carro (por ID)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')") // Só gerente deleta
    public ResponseEntity<Void> deletarCarro(@PathVariable Long id) {
        carroService.deleteById(id); // O service vai validar se existe antes de deletar
        return ResponseEntity.noContent().build(); // Retorna 204 se der certo
    }

    // Pesquisar carro por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'GERENTE')") // Ambos podem visualizar
    public ResponseEntity<Carro> pesquisarCarroPorId(@PathVariable Long id) {
        Carro carro = carroService.findByIdOrThrow(id); // Já joga o 404 se não achar
        return ResponseEntity.ok(carro);
    }

    // Pesquisar todos os carros
    @GetMapping
    @PreAuthorize("hasAnyRole('VENDEDOR', 'GERENTE')") // Ambos podem listar
    public ResponseEntity<List<Carro>> pesquisarTodosCarros() {
        List<Carro> carros = carroService.findAll();
        return ResponseEntity.ok(carros);
    }
}