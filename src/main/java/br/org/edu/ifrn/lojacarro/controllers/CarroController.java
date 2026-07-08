package br.org.edu.ifrn.lojacarro.controllers;

import br.org.edu.ifrn.lojacarro.dto.CarroRequestDto;
import br.org.edu.ifrn.lojacarro.dto.CarroResponseDto;
import br.org.edu.ifrn.lojacarro.model.Carro;
import br.org.edu.ifrn.lojacarro.services.CarroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carro")
public class CarroController {

    private final CarroService carroService;

    public CarroController(CarroService carroService) {
        this.carroService = carroService;
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<CarroResponseDto> salvarCarro(@Valid @RequestBody CarroRequestDto request) {
        Carro carro = new Carro();
        carro.setModelo(request.getModelo());
        carro.setAno(request.getAno());

        Carro savedCarro = carroService.save(carro);
        return ResponseEntity.ok(toResponse(savedCarro));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<CarroResponseDto> atualizarCarro(@PathVariable Long id, @RequestBody CarroRequestDto request) {
        Carro carro = new Carro();
        carro.setId(id);
        carro.setModelo(request.getModelo());
        carro.setAno(request.getAno());

        Carro atualizado = carroService.update(carro);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> deletarCarro(@PathVariable Long id) {
        carroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'GERENTE')")
    public ResponseEntity<CarroResponseDto> pesquisarCarroPorId(@PathVariable Long id) {
        Carro carro = carroService.findByIdOrThrow(id);
        return ResponseEntity.ok(toResponse(carro));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('VENDEDOR', 'GERENTE')")
    public ResponseEntity<List<CarroResponseDto>> pesquisarTodosCarros() {
        List<CarroResponseDto> carros = carroService.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(carros);
    }

    private CarroResponseDto toResponse(Carro carro) {
        return new CarroResponseDto(carro.getId(), carro.getModelo(), carro.getAno());
    }
}