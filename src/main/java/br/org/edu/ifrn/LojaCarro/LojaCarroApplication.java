package br.org.edu.ifrn.LojaCarro;

import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LojaCarroApplication {

	public static void main(String[] args) {
		SpringApplication.run(LojaCarroApplication.class, args);
	}

	@Bean
	CommandLineRunner start(UsuarioRepository repo, PasswordEncoder encoder) {
		return args -> {
			if (repo.findByUsername("admin").isEmpty()) {
				Usuario admin = new Usuario();
				admin.setUsername("admin");
				admin.setPassword(encoder.encode("admin123"));
				admin.setRole("ROLE_GERENTE");
				repo.save(admin);
				System.out.println("=== USUÁRIO MASTER CRIADO: admin / admin123 (ROLE_GERENTE) ===");
			}
		};
	}
}