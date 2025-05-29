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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cava.model.dto.CavaPartidaDto;
import cava.model.dto.MaterialCavaDto;
import cava.model.dto.MaterialDto;
import cava.model.entity.Cava;
import cava.model.entity.CavaPartida;
import cava.model.entity.Material;
import cava.model.entity.MaterialCava;
import cava.model.entity.Partida;
import cava.model.service.CavaPartidaService;
import cava.model.service.CavaService;
import cava.model.service.MaterialCavaService;
import cava.model.service.MaterialService;
import cava.model.service.PartidaService;

@RestController
@RequestMapping("/api/cavapartida")
@CrossOrigin(origins = "*")
public class CavaPartidaController {

	@Autowired
	private CavaPartidaService cpservice;
	@Autowired
	private CavaService cservice;
	@Autowired
	private PartidaService pservice;
	@Autowired
	private ModelMapper mapper;

	// Obtener todas las relaciones
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
	    List<CavaPartida> lista = cpservice.buscarTodos();
	    List<CavaPartidaDto> dtos = new ArrayList<>();

	    for (CavaPartida cp : lista) {
	    	CavaPartidaDto dto = mapper.map(cp, CavaPartidaDto.class);
	    	dto.setId(cp.getId());
	        dtos.add(dto);
	    }

	    return ResponseEntity.ok(dtos);
	}

	// Obtener todas las relaciones de un cava en concreto
	@GetMapping("/{idCava}")
	public ResponseEntity<?> obtenerMaterialCava(@PathVariable String idCava) {
	    List<CavaPartida> relaciones = cpservice.findByCavaId(idCava);
	    List<CavaPartidaDto> relacionesDto = new ArrayList<>();

	    for (CavaPartida relacion : relaciones) {
	    	Partida partida = relacion.getPartida();
	    	CavaPartidaDto dto = mapper.map(relacion.getCava(), CavaPartidaDto.class);
	        // Completar campos adicionales desde las relaciones
	    	dto.setId(relacion.getId());
	        dto.setPartidaId(relacion.getPartida().getId());
	        dto.setPartidaBotellasRima(partida.getBotellasRima());
	        dto.setActual(relacion.isActual());

	        relacionesDto.add(dto);
	    }

	    return ResponseEntity.ok(relacionesDto);
	}

	@PostMapping("/{idCava}/{idPartida}")
	public ResponseEntity<?> asignarPartida(@PathVariable String idCava, @PathVariable String idPartida) {
	    try {
	        Cava cava = cservice.buscar(idCava);
	        System.out.println(cava);
	        if (cava == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No se encontró la cava con ID: " + idCava);
	        }

	        Partida partida = pservice.buscar(idPartida);
	        if (partida == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No se encontró el material con ID: " + idPartida);
	        }
	        System.out.println(partida);

	        CavaPartida nuevaRelacion = new CavaPartida();
	        nuevaRelacion.setCava(cava);
	        nuevaRelacion.setPartida(partida);
	        nuevaRelacion.setActual(false);

	        CavaPartida guardado = cpservice.insertar(nuevaRelacion);

	        CavaPartidaDto dto = mapper.map(guardado, CavaPartidaDto.class);
	        return ResponseEntity.status(HttpStatus.CREATED).body(dto);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al asignar el material al cava: " + e.getMessage());
	    }
	}
	
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
    	CavaPartida existente = cpservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la relación con id " + id);
        }
        cpservice.borrar(id);
        return ResponseEntity.noContent().build();
    }
	
	
    @PutMapping("/activar/{id}")
    public ResponseEntity<?> activar(@PathVariable Long id) {
    	CavaPartida existente = cpservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la relación con id " + id);
        }
        
        List<CavaPartida> relaciones = cpservice.findByCavaId(existente.getCava().getId());
        for(CavaPartida partida: relaciones) {
        	if(partida.isActual()) {
        		partida.setActual(false);
        	}
        	existente.setActual(true);
        }
        
        cpservice.modificar(existente);
        return ResponseEntity.ok().build();
    }
}
