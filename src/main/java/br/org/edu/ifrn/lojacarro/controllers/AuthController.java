package br.org.edu.ifrn.lojacarro.controllers;

import br.org.edu.ifrn.lojacarro.model.Usuario;
import br.org.edu.ifrn.lojacarro.repository.UsuarioRepository;
import br.org.edu.ifrn.lojacarro.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('GERENTE')") // Apenas usuários com papel GERENTE podem cadastrar outros usuários
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario) {
        // Garante que o papel venha com o prefixo correto esperado pelo Spring Security
        if(!usuario.getRole().startsWith("ROLE_")) {
            usuario.setRole("ROLE_" + usuario.getRole().toUpperCase());
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Usuario loginReq) {
        Usuario usuario = usuarioRepository.findByUsername(loginReq.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (passwordEncoder.matches(loginReq.getPassword(), usuario.getPassword())) {
            String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRole());
            return ResponseEntity.ok(Map.of("token", token));
        }

        return ResponseEntity.status(401).body(Map.of("erro", "Credenciais inválidas"));
    }
}