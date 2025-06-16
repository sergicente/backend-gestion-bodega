package cava.model.restcontroller;
import cava.configuration.JwtUtil;
import cava.model.dto.LoginRequest;
import cava.model.dto.LoginResponse;
import cava.model.entity.Usuario;
import cava.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales incorrectas");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales incorrectas");
        }
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name());
        String mensaje = "Login exitoso";
        String nombre = usuario.getNombre();
        String rol = usuario.getRol().name();
        return ResponseEntity.ok(
                new LoginResponse(token, mensaje, nombre, rol)
        );
    }
}