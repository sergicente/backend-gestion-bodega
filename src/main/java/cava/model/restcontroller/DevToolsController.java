package cava.model.restcontroller;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cava.model.service.ResetService;

@RestController
@RequestMapping("/api/dev")
@Profile("dev") 
public class DevToolsController {

    private final ResetService resetService;

    public DevToolsController(ResetService resetService) {
        this.resetService = resetService;
    }

    @PostMapping("/reset-bbdd")
    public ResponseEntity<?> resetBBDD() {
        resetService.reiniciarBaseDeDatos();
        return ResponseEntity.ok(Map.of("mensaje", "Base de datos restablecida"));
    }
}