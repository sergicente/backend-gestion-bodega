package cava.model.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
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

import cava.model.dto.PartidaDto;
import cava.model.entity.Partida;
import cava.model.service.PartidaService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/partida")
public class PartidaController {
	
	@Autowired
	private PartidaService pservice;
	@Autowired
	private ModelMapper mapper;
	
    // Obtener todas las partidas
    @GetMapping
    public ResponseEntity<?> obtenerTodas() {
        List<Partida> partidas = pservice.buscarTodos();
        List<PartidaDto> dtos = new ArrayList<>();

        for (Partida p : partidas) {
            PartidaDto dto = mapper.map(p, PartidaDto.class);
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
    }
    
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable String id) {
        Partida partida = pservice.buscar(id);
        if (partida == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
        } else {
            PartidaDto dto = mapper.map(partida, PartidaDto.class);
            return ResponseEntity.ok(dto);
        }
    }
    
    
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody Partida cava) {

        // Verifica si ya existe
        Partida partidaExistente = pservice.buscar(cava.getId());
        if (partidaExistente != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: Ya existe una partida con el ID " + cava.getId());
        }

        // Inserta la nueva partida
        Partida nuevaPartida = pservice.insertar(cava);

        // Mapea a DTO manualmente, sin streams
        PartidaDto dto = mapper.map(nuevaPartida, PartidaDto.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    
    
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable String id, @RequestBody Partida cava) {
        try {
            if (!cava.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID en la URL y el body no coinciden");
            }

            Partida actualizado = pservice.modificar(cava);
            PartidaDto dto = mapper.map(actualizado, PartidaDto.class);

            return ResponseEntity.ok(dto);

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
