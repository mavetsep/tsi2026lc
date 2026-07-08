# Integração e Teste Unitários

---

## 1. Fundamentação Teórica

### O que é um Teste de Integração?
O teste de integração é a fase do ciclo de vida de testes onde módulos e camadas individuais do sistema são combinados e avaliados em conjunto. Diferente do teste unitário, o objetivo principal não é validar uma função isolada, mas sim garantir que a comunicação, o fluxo de dados e os protocolos de rede entre componentes distintos funcionem perfeitamente.

### Diferença entre Teste Unitário e Teste de Integração
* **Teste Unitário (`CarroServiceTest`):** Foca em isolar a lógica de negócio de uma classe específica. Utiliza ferramentas de simulação (**Mockito**) para criar dublês de componentes externos (como o banco de dados), garantindo que nenhuma consulta real seja feita à infraestrutura. É um teste focado em velocidade e exatidão algorítmica.
* **Teste de Integração (`CarroIntegrationTest`):** Valida a jornada completa da requisição através de todas as camadas físicas do sistema. Utiliza o contexto do Spring Boot (`@SpringBootTest`) e interage com um banco de dados real (**MySQL**), gerando queries SQL físicas para testar a comunicação de ponta a ponta.

---

## 2. Arquitetura do Projeto e Componentes Integrados

A aplicação baseia-se na arquitetura padrão do Spring Boot em três camadas principais, todas validadas na suíte de testes:

1.  **Camada de Controle (`CarroController`):** Responsável por expor os endpoints REST, gerenciar os verbos HTTP (`GET`, `POST`, `PUT`, `DELETE`), converter objetos Java em JSON (via *ObjectMapper*) e validar os contratos iniciais de entrada.
2.  **Camada de Serviço (`CarroService`):** Onde reside o núcleo das regras de negócio do sistema. Centraliza validações críticas como consistência de dados (verificação de existência por ID) e regras temporais dinâmicas.
3.  **Camada de Persistência (`CarroRepository`):** Camada gerenciada pelo Spring Data JPA e Hibernate responsável por traduzir as operações Java em queries relacionais executadas diretamente no banco de dados MySQL.

---

## 3. Detalhamento dos Cenários de Teste

Para garantir o comportamento do CRUD completo sob perspectivas distintas, foram mapeados **10 cenários de integração** e **10 cenários unitários**, divididos estritamente entre *Happy Paths* (Sucesso) e *Sad Paths* (Falhas controladas).

### Matriz de Comportamento dos Testes

| Operação CRUD | Cenário de Sucesso (*Happy Path*) | Cenário de Exceção (*Sad Path*) | Código HTTP Esperado (Falha) |
| :--- | :--- | :--- | :--- |
| **Salvar (POST)** | Cadastra veículo válido (ex: Polo 2025). | Tenta salvar veículo com ano maior que o atual. | `400 Bad Request` |
| **Buscar por ID (GET)**| Localiza veículo inserido via script SQL. | Tenta buscar ID inexistente (`999`). | `404 Not Found` |
| **Listar Todos (GET)** | Retorna lista populada com dados. | Trata banco vazio retornando array estável `[]`. | `200 OK` (Tamanho 0) |
| **Atualizar (PUT)** | Modifica dados de um registro existente. | Tenta modificar ID inexistente (`999`). | `404 Not Found` |
| **Deletar (DELETE)** | Remove fisicamente o registro do banco. | Tenta deletar ID inexistente (`999`). | `404 Not Found` |

---

## 4. Problemas Identificados e Mitigados

A introdução dos testes de software permitiu identificar e corrigir falhas de arquitetura cruciais antes do envio para produção:

* **Vulnerabilidade do Hibernate no `PUT`:** Inicialmente, tentar atualizar um ID inexistente causava um travamento de infraestrutura no Hibernate (`StaleObjectStateException`). O erro foi mitigado blindando o `CarroService` com uma validação preventiva usando `existsById`, fazendo a aplicação responder com um erro limpo.
* **Tratamento de Exceções RESTful:** A aplicação foi refinada para que falhas de negócio gerassem `ResponseStatusException` adequadas, fornecendo mensagens claras no corpo da resposta HTTP (ex: *"Não é possível deletar: Carro não encontrado"*).
* **Independência Temporal:** A validação do ano do carro foi desenvolvida utilizando `LocalDate.now().getYear()` de forma dinâmica. Isso impede o envelhecimento precoce do código (*hardcoding*), garantindo que o teste continue preciso nos anos subsequentes.

---

## 5. Integração Contínua (CI/CD)

O projeto foi acoplado a uma esteira de automação utilizando **GitHub Actions**. O pipeline executa as seguintes etapas automáticas a cada commit enviado ao repositório:

1.  **Inicialização de Containers:** Provisiona dinamicamente um container Docker isolado rodando **MySQL 8.0** com credenciais de ambiente seguras (`MYSQL_ALLOW_EMPTY_PASSWORD`).
2.  **Ambiente Java:** Configura o ambiente de execução com o JDK 17.
3.  **Validação de Permissões:** Corrige em tempo de execução os privilégios do arquivo `mvnw` (`chmod +x`) para execução em sistemas Unix/Linux.
4.  **Execução da Suíte:** Dispara o comando `./mvnw clean test`, rodando todos os 20 testes simultaneamente. O build só é considerado bem-sucedido se 100% dos cenários passarem.

---

## 6. Conclusão e Resultados

Todas as asserções desenvolvidas foram executadas com sucesso. Os testes de falha foram devidamente configurados para validar as defesas do sistema, o que significa que o JUnit interpretou as respostas de erro controladas como comportamento correto da aplicação, culminando em **100% dos testes validados (Painel Verde)**.

A aplicação encontra-se estável, devidamente documentada, protegida na camada de serviço e pronta para implantação segura.