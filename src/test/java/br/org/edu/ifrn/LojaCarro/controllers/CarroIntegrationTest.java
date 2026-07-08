package br.org.edu.ifrn.LojaCarro.controllers;

import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "admin", roles = "GERENTE")
public class CarroIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarroRepository carroRepository;

    // =========================================================================
    // 1. OPERAÇÃO: SALVAR (POST)
    // =========================================================================

    @Test
    public void test1_SalvarCarro_Sucesso() throws Exception {
        System.out.println("\n=== [POST] SALVAR CARRO: CENÁRIO DE SUCESSO ===");
        carroRepository.deleteAll();

        Carro novo = new Carro();
        novo.setModelo("Volkswagen Polo");
        novo.setAno(2025);

        mockMvc.perform(post("/carro/salvar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novo)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Volkswagen Polo"));
    }

    @Test
    public void test1_SalvarCarro_Falha() throws Exception {
        System.out.println("\n=== [POST] SALVAR CARRO: CENÁRIO DE FALHA (ANO NO FUTURO) ===");
        int anoInvalido = java.time.LocalDate.now().getYear() + 1;

        Carro invalido = new Carro();
        invalido.setModelo("DeLorean");
        invalido.setAno(anoInvalido); // Ativa a regra de negócio do CarroService

        mockMvc.perform(post("/carro/salvar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andDo(print())
                .andExpect(status().isBadRequest()); // Passa porque ESPERAMOS o erro 400
    }

    // =========================================================================
    // 2. OPERAÇÃO: PROCURAR POR ID (GET)
    // =========================================================================

    @Test
    @Sql(scripts = "/inserir_carros.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void test2_BuscarPorId_Sucesso() throws Exception {
        System.out.println("\n=== [GET] BUSCAR POR ID: CENÁRIO DE SUCESSO ===");
        mockMvc.perform(get("/carro/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Honda Civic"));
    }

    @Test
    public void test2_BuscarPorId_Falha() throws Exception {
        System.out.println("\n=== [GET] BUSCAR POR ID: CENÁRIO DE FALHA (ID INEXISTENTE) ===");
        mockMvc.perform(get("/carro/999"))
                .andDo(print())
                .andExpect(status().isNotFound()); // Passa porque ESPERAMOS o erro 404
    }

    // =========================================================================
    // 3. OPERAÇÃO: LISTAR TODOS (GET)
    // =========================================================================

    @Test
    @Sql(scripts = "/inserir_carros.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void test3_ListarTodos_Sucesso() throws Exception {
        System.out.println("\n=== [GET] LISTAR TODOS: CENÁRIO DE SUCESSO ===");
        mockMvc.perform(get("/carro")) // Altere para a sua rota de listagem se for diferente, ex: "/carro/listar"
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()); // Valida se retornou uma lista
    }

    @Test
    public void test3_ListarTodos_Falha() throws Exception {
        System.out.println("\n=== [GET] LISTAR TODOS: CENÁRIO DE FALHA (BANCO VAZIO) ===");
        carroRepository.deleteAll(); // Garante que não há nada cadastrado

        // Se a API retorna 404 ou uma lista vazia com status 204 quando não há registros,
        // ajuste aqui. Assume que ela lança uma exceção/retorna 404 caso não ache registros.
        mockMvc.perform(get("/carro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // =========================================================================
    // 4. OPERAÇÃO: ATUALIZAR (PUT)
    // =========================================================================

    @Test
    @Sql(scripts = "/inserir_carros.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void test4_AtualizarCarro_Sucesso() throws Exception {
        System.out.println("\n=== [PUT] ATUALIZAR CARRO: CENÁRIO DE SUCESSO ===");
        Carro atualizado = new Carro();
        atualizado.setModelo("Honda Civic Alterado");
        atualizado.setAno(2024);

        mockMvc.perform(put("/carro/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Honda Civic Alterado"));
    }

    @Test
    public void test4_AtualizarCarro_Falha() throws Exception {
        System.out.println("\n=== [PUT] ATUALIZAR CARRO: CENÁRIO DE FALHA (ID INEXISTENTE) ===");
        Carro atualizado = new Carro();
        atualizado.setModelo("Carro Fantasma");
        atualizado.setAno(2020);

        mockMvc.perform(put("/carro/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andDo(print())
                .andExpect(status().isNotFound()); // Passa se a sua API validar e devolver 404 para ID inexistente
    }

    // =========================================================================
    // 5. OPERAÇÃO: DELETAR (DELETE)
    // =========================================================================

    @Test
    @Sql(scripts = "/inserir_carros.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void test5_DeletarCarro_Sucesso() throws Exception {
        System.out.println("\n=== [DELETE] DELETAR CARRO: CENÁRIO DE SUCESSO ===");
        mockMvc.perform(delete("/carro/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void test5_DeletarCarro_Falha() throws Exception {
        System.out.println("\n=== [DELETE] DELETAR CARRO: CENÁRIO DE FALHA (ID INEXISTENTE) ===");
        mockMvc.perform(delete("/carro/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}