package LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import br.org.edu.ifrn.LojaCarro.services.CarroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarroServiceTest {

    @Mock
    private CarroRepository carroRepository;

    @InjectMocks
    private CarroService carroService;

    private Carro carroValido;

    @BeforeEach
    public void setUp() {
        carroValido = new Carro();
        carroValido.setId(1L);
        carroValido.setModelo("Toyota Corolla");
        carroValido.setAno(2024);
    }

    @Test
    public void testSaveComSucesso() {
        System.out.println("=== TESTE SERVICE: SALVAR CARRO ===");

        when(carroRepository.save(any(Carro.class))).thenReturn(carroValido);

        Carro salvo = carroService.save(carroValido);

        assertNotNull(salvo);
        assertEquals("Toyota Corolla", salvo.getModelo());
        verify(carroRepository, times(1)).save(carroValido);
    }

    @Test
    public void testSaveFalhaAnoFuturo() {
        System.out.println("=== TESTE SERVICE: SALVAR COM ANO NO FUTURO ===");

        Carro carroInvalido = new Carro();
        carroInvalido.setModelo("Honda Civic");
        carroInvalido.setAno(LocalDate.now().getYear() + 1);

        assertThrows(ResponseStatusException.class, () -> {
            carroService.save(carroInvalido);
        });

        verify(carroRepository, never()).save(any(Carro.class));
    }

    @Test
    public void testUpdateComSucesso() {
        System.out.println("=== TESTE SERVICE: ATUALIZAR CARRO ===");

        when(carroRepository.existsById(1L)).thenReturn(true);
        when(carroRepository.save(any(Carro.class))).thenReturn(carroValido);

        Carro atualizado = carroService.update(carroValido);

        assertNotNull(atualizado);
        verify(carroRepository, times(1)).existsById(1L);
        verify(carroRepository, times(1)).save(carroValido);
    }

    @Test
    public void testUpdateFalhaIdInexistente() {
        System.out.println("=== TESTE SERVICE: ATUALIZAR COM ID INEXISTENTE ===");

        Carro fantasma = new Carro();
        fantasma.setId(999L);
        fantasma.setModelo("Chevrolet Onix");
        fantasma.setAno(2023);

        when(carroRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> {
            carroService.update(fantasma);
        });

        verify(carroRepository, never()).save(fantasma);
    }

    @Test
    public void testDeleteComSucesso() {
        System.out.println("=== TESTE SERVICE: DELETAR CARRO ===");

        when(carroRepository.existsById(1L)).thenReturn(true);
        doNothing().when(carroRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            carroService.deleteById(1L);
        });

        verify(carroRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteFalhaIdInexistente() {
        System.out.println("=== TESTE SERVICE: DELETAR COM ID INEXISTENTE ===");

        when(carroRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> {
            carroService.deleteById(999L);
        });

        verify(carroRepository, never()).deleteById(999L);
    }

    @Test
    public void testFindByIdComSucesso() {
        System.out.println("=== TESTE SERVICE: BUSCAR POR ID ===");

        when(carroRepository.findById(1L)).thenReturn(Optional.of(carroValido));

        Carro encontrado = carroService.findByIdOrThrow(1L);

        assertNotNull(encontrado);
        assertEquals("Toyota Corolla", encontrado.getModelo());
    }

    @Test
    public void testFindByIdFalhaIdInexistente() {
        System.out.println("=== TESTE SERVICE: BUSCAR POR ID INEXISTENTE ===");

        when(carroRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            carroService.findByIdOrThrow(999L);
        });
    }

    @Test
    public void testFindAllComDados() {
        System.out.println("=== TESTE SERVICE: LISTAR TODOS COM REGISTROS ===");

        List<Carro> lista = new ArrayList<>();
        lista.add(carroValido);
        when(carroRepository.findAll()).thenReturn(lista);

        List<Carro> resultado = carroService.findAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    public void testFindAllRetornandoListaVazia() {
        System.out.println("=== TESTE SERVICE: LISTAR TODOS COM BANCO VAZIO ===");

        when(carroRepository.findAll()).thenReturn(Collections.emptyList());

        List<Carro> resultado = carroService.findAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
