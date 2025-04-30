package cava.model.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cava.model.entity.Partida;
import cava.model.service.PartidaService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/partida")
@CrossOrigin(origins = "*")
public class PartidaController {
	
	@Autowired
	private PartidaService pservice;
	
    // Obtener todas las partidas
    @GetMapping
    public List<Partida> obtenerTodas() {
        return pservice.buscarTodos();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable String id) {
    	Partida cava = pservice.buscar(id);
    	if(cava == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
    	}else {
    		return ResponseEntity.ok(cava);
    	}
    }
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody Partida cava){
    	
        // Verifica si el ID ya existe en la base de datos
        Partida partidaExistente = pservice.buscar(cava.getId());

        if (partidaExistente != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: Ya existe una partida con el ID " + cava.getId());
        }
    	
    	Partida nuevaPartida = pservice.insertar(cava);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPartida);
    }
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable String id, @RequestBody Partida cava) {
        try {
            // Aseguramos que el ID del objeto concuerda con el path variable (opcional)
            if (!cava.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID en la URL y el body no coinciden");
            }
            Partida actualizado = pservice.modificar(cava);
            return ResponseEntity.ok(actualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }
	
	// Borrar una partida
	@DeleteMapping("/borrar/{id}")
	public ResponseEntity<?> borrar(@PathVariable String id) {
		Partida existente = pservice.buscar(id);
		if(existente == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
		}
		pservice.borrar(id);
		return ResponseEntity.noContent().build();
	}
	
}
