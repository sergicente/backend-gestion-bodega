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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cava.model.dto.MaterialCavaDto;
import cava.model.dto.MaterialDto;
import cava.model.entity.Cava;
import cava.model.entity.Material;
import cava.model.entity.MaterialCava;
import cava.model.service.CavaService;
import cava.model.service.MaterialCavaService;
import cava.model.service.MaterialService;

@RestController
@RequestMapping("/api/materialcava")
@CrossOrigin(origins = "*")
public class MaterialCavaController {

	@Autowired
	private MaterialCavaService mcservice;
	@Autowired
	private CavaService cservice;
	@Autowired
	private MaterialService mservice;
	@Autowired
	private ModelMapper mapper;

	// Obtener todas las relaciones
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
	    List<MaterialCava> lista = mcservice.buscarTodos();
	    List<MaterialCavaDto> dtos = new ArrayList<>();

	    for (MaterialCava mc : lista) {
	        MaterialCavaDto dto = mapper.map(mc, MaterialCavaDto.class);
	        dtos.add(dto);
	    }

	    return ResponseEntity.ok(dtos);
	}

	// Obtener todas las relaciones de un cava en concreto
	@GetMapping("/{idCava}")
	public ResponseEntity<?> obtenerMaterialCava(@PathVariable String idCava) {
	    List<MaterialCava> relaciones = mcservice.findByCavaId(idCava);
	    List<MaterialCavaDto> materiales = new ArrayList<>();

	    for (MaterialCava mc : relaciones) {
	        MaterialCavaDto dto = mapper.map(mc.getMaterial(), MaterialCavaDto.class);
	        // Completar campos adicionales desde las relaciones
	        dto.setId(mc.getId());
	        dto.setMaterialId(mc.getMaterial().getId());
	        dto.setMaterialNombre(mc.getMaterial().getNombre());
	        dto.setCantidad(mc.getMaterial().getCantidad());
	        dto.setCategoriaNombre(mc.getMaterial().getCategoria().getNombre());
	        dto.setCavaNombre(mc.getCava().getNombre());

	        materiales.add(dto);
	    }

	    return ResponseEntity.ok(materiales);
	}

	@PostMapping("/{idCava}/{idMaterial}")
	public ResponseEntity<?> asignarMaterial(@PathVariable String idCava, @PathVariable long idMaterial) {
	    try {
	        Cava cava = cservice.buscar(idCava);
	        if (cava == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No se encontró la cava con ID: " + idCava);
	        }

	        Material material = mservice.buscar(idMaterial);
	        if (material == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No se encontró el material con ID: " + idMaterial);
	        }

	        MaterialCava nuevaRelacion = new MaterialCava();
	        nuevaRelacion.setCava(cava);
	        nuevaRelacion.setMaterial(material);
	        nuevaRelacion.setCantidadNecesariaPorBotella(1);

	        MaterialCava guardado = mcservice.insertar(nuevaRelacion);

	        MaterialCavaDto dto = mapper.map(guardado, MaterialCavaDto.class);
	        return ResponseEntity.status(HttpStatus.CREATED).body(dto);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al asignar el material al cava: " + e.getMessage());
	    }
	}
	
	
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable int id) {
        MaterialCava existente = mcservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la relación con id " + id);
        }
        mcservice.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
