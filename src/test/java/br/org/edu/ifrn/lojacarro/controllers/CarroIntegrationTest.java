package br.org.edu.ifrn.lojacarro.controllers;

import br.org.edu.ifrn.lojacarro.model.Carro;
import br.org.edu.ifrn.lojacarro.repository.CarroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "admin", roles = "GERENTE")
class CarroIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarroRepository carroRepository;

    @Test
    void test1_SalvarCarro_Sucesso() throws Exception {
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
    void test1_SalvarCarro_Falha() throws Exception {
        int anoInvalido = java.time.LocalDate.now().getYear() + 1;

        Carro invalido = new Carro();
        invalido.setModelo("DeLorean");
        invalido.setAno(anoInvalido);

        mockMvc.perform(post("/carro/salvar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(scripts = "/inserir_carros.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test2_BuscarPorId_Sucesso() throws Exception {
        mockMvc.perform(get("/carro/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Honda Civic"));
    }

    @Test
    void test2_BuscarPorId_Falha() throws Exception {
        mockMvc.perform(get("/carro/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(scripts = "/inserir_carros.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test3_ListarTodos_Sucesso() throws Exception {
        mockMvc.perform(get("/carro"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void test3_ListarTodos_Falha() throws Exception {
        carroRepository.deleteAll();

        mockMvc.perform(get("/carro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Sql(scripts = "/inserir_carros.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test4_AtualizarCarro_Sucesso() throws Exception {
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
    void test4_AtualizarCarro_Falha() throws Exception {
        Carro atualizado = new Carro();
        atualizado.setModelo("Carro Fantasma");
        atualizado.setAno(2020);

        mockMvc.perform(put("/carro/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(scripts = "/inserir_carros.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void test5_DeletarCarro_Sucesso() throws Exception {
        mockMvc.perform(delete("/carro/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void test5_DeletarCarro_Falha() throws Exception {
        mockMvc.perform(delete("/carro/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}