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

import cava.model.dto.CavaDto;
import cava.model.entity.Cava;
import cava.model.entity.CavaPartida;
import cava.model.entity.Familia;
import cava.model.entity.Partida;
import cava.model.service.CavaPartidaService;
import cava.model.service.CavaService;
import cava.model.service.FamiliaService;
import cava.model.service.PartidaService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/cava")
@CrossOrigin(origins = "*")
public class CavaController {
	
	@Autowired
	private CavaService cservice;
	@Autowired
	private PartidaService pservice;
	@Autowired
	private FamiliaService fservice;
	@Autowired
	private CavaPartidaService cpservice;
	@Autowired
	private ModelMapper mapper;
	
    // Obtener todas las partidas
    @GetMapping
    public ResponseEntity<List<CavaDto>> obtenerTodos() {
    	List<Cava> cavas = cservice.buscarTodos();
    	List<CavaDto> resultado = new ArrayList<>();
    	for(Cava cava : cavas) {
    		CavaDto dto = mapper.map(cava, CavaDto.class);
    		resultado.add(dto);
    	}

        return ResponseEntity.ok(resultado);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable String id) {
    	Cava cava = cservice.buscar(id);
    	if(cava == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
    	}else {
    		CavaDto dto = mapper.map(cava, CavaDto.class);
    		return ResponseEntity.ok(dto);
    	}
    }
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody CavaDto cavaDto) {

        // Verificar existencia de la familia
        Familia familia = fservice.buscar(Long.parseLong(cavaDto.getFamiliaId()));
        if (familia == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No existe una familia con el ID " + cavaDto.getFamiliaId());
        }

        // Verificar si ya existe un cava con ese ID
        if (cservice.buscar(cavaDto.getId()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: Ya existe un cava con el ID " + cavaDto.getId());
        }

        // Mapear el DTO a entidad
        Cava cava = mapper.map(cavaDto, Cava.class);
        cava.setFamilia(familia); // Relación que no puede mapear directamente

        // Guardar el cava
        cava = cservice.insertar(cava);

        // Devolver el DTO como respuesta
        CavaDto respuestaDto = mapper.map(cava, CavaDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuestaDto);
    }
 
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable String id, @RequestBody CavaDto cavaDto) {
        try {
            // Comprobamos que el ID concuerda
            if (!cavaDto.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID en la URL y el body no coinciden");
            }
            
            Familia familia = fservice.buscar(Long.parseLong(cavaDto.getFamiliaId()));
            if (familia == null) {
                throw new EntityNotFoundException("Familia no encontrada");
            }

            // Montamos el objeto Cava
            Cava cava = mapper.map(cavaDto, Cava.class);

            // Modificamos
            Cava actualizado = cservice.modificar(cava);
            CavaDto dto = mapper.map(actualizado, CavaDto.class);

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
		Cava existente = cservice.buscar(id);
		if(existente == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
		}
		cservice.borrar(id);
		return ResponseEntity.noContent().build();
	}
	
}
