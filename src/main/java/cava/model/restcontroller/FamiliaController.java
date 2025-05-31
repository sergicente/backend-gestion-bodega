package cava.model.restcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import cava.model.dto.FamiliaDto;
import cava.model.dto.MaterialDto;
import cava.model.dto.PartidaDto;
import cava.model.entity.Cava;
import cava.model.entity.CavaPartida;
import cava.model.entity.Familia;
import cava.model.entity.Material;
import cava.model.service.CavaService;
import cava.model.service.FamiliaService;
import cava.model.service.MaterialService;

@RestController
@RequestMapping("/api/familia")
@CrossOrigin(origins = "*")
public class FamiliaController {
	
	@Autowired
	private FamiliaService fservice;
	@Autowired
	private MaterialService mservice;
	@Autowired
	private CavaService cservice;
	@Autowired
	private ModelMapper mapper;
	
    // Obtener todas las partidas
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        List<Familia> familias = fservice.buscarTodos();
        List<FamiliaDto> listado = new ArrayList<>();

        for (Familia f : familias) {
            listado.add(mapper.map(f, FamiliaDto.class));
        }

        return ResponseEntity.ok(listado);
    }
    
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable long id) {
    	Familia familia = fservice.buscar(id);
    	if(familia == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la familia");
    	}else {
    		FamiliaDto dto = mapper.map(familia, FamiliaDto.class);
    		return ResponseEntity.ok(dto);
    	}
    }
    
    
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody FamiliaDto dto) {
    	
        if (dto.getId() == null) {
            return ResponseEntity.badRequest().body("El ID es obligatorio si no se genera automáticamente");
        }

        if (fservice.buscar(dto.getId()) != null) {
            return ResponseEntity.badRequest().body("Ya existe una familia con ese ID");
        }

        // Convertir DTO a entidad
        Familia familia = new Familia();
        familia.setId(dto.getId());
        familia.setNombre(dto.getNombre());

        // Guardar
        Familia guardada = fservice.insertar(familia);

        // Convertir a DTO de respuesta
        FamiliaDto nuevoDto = new FamiliaDto(
            guardada.getId(),
            guardada.getNombre()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }
    
    
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<FamiliaDto> modificar(@PathVariable Long id, @RequestBody FamiliaDto dto) {
        
        // Buscar la familia existente
        Familia existente = fservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizar campos
        existente.setNombre(dto.getNombre());

        // Guardar cambios
        Familia actualizada = fservice.insertar(existente);

        // Devolver DTO actualizado
        FamiliaDto respuesta = new FamiliaDto(
            actualizada.getId(),
            actualizada.getNombre()
        );

        return ResponseEntity.ok(respuesta);
    }
	
    
    
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        
        Familia existente = fservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            fservice.borrar(id);
            return ResponseEntity.ok().body(Map.of("mensaje", "Familia eliminada correctamente"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "No se puede eliminar la familia porque está en uso"));
        }
    }
    
    @GetMapping("/material/{id}")
    public ResponseEntity<?> buscarMateriales(@PathVariable Long id) {
        Familia familia = fservice.buscar(id);
        if (familia == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Familia no encontrada con ID: " + id);
        }

        List<Material> lista = mservice.findByFamilia(familia);
        List<MaterialDto> listadoDto = new ArrayList<>();
        for(Material m : lista){
        	MaterialDto dto = mapper.map(m, MaterialDto.class);
        	listadoDto.add(dto);
        }
        return ResponseEntity.ok(listadoDto);
    }
    
    @GetMapping("/cavas/{id}")
    public ResponseEntity<?> buscarCavas(@PathVariable Long id) {
        Familia familia = fservice.buscar(id);
        if (familia == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Familia no encontrada con ID: " + id);
        }

        List<Cava> lista = cservice.findByFamilia(familia);
        List<CavaDto> listadoDto = new ArrayList<>();

        
        for (Cava c : lista) {
            CavaDto dto = mapper.map(c, CavaDto.class);

            for (CavaPartida cp : c.getPartidasRelacionadas()) {
                if (cp.isActual()) {
                    dto.setPartidaActual(mapper.map(cp.getPartida(), PartidaDto.class));
                    break;
                }
            }

            listadoDto.add(dto);
        }

        return ResponseEntity.ok(listadoDto);
    }
	
}
