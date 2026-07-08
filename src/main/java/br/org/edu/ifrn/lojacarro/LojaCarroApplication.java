package br.org.edu.ifrn.lojacarro;

import br.org.edu.ifrn.lojacarro.model.Usuario;
import br.org.edu.ifrn.lojacarro.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LojaCarroApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(LojaCarroApplication.class);

	@Value("${app.admin.username:admin}")
	private String adminUsername;

	@Value("${app.admin.password:admin-dev-change-me}")
	private String adminPassword;

	public static void main(String[] args) {
		SpringApplication.run(LojaCarroApplication.class, args);
	}

	@Bean
	CommandLineRunner start(UsuarioRepository repo, PasswordEncoder encoder) {
		return args -> {
			if (repo.findByUsername(adminUsername).isEmpty()) {
				Usuario admin = new Usuario();
				admin.setUsername(adminUsername);
				admin.setPassword(encoder.encode(adminPassword));
				admin.setRole("ROLE_GERENTE");
				repo.save(admin);
				LOGGER.info("Usuário administrador inicial criado.");
			}
		};
	}
}