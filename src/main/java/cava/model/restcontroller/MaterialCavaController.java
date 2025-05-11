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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	MaterialCavaService mcservice;

	@Autowired
	CavaService cservice;

	@Autowired
	MaterialService mservice;

	// Obtener todas las relaciones
	@GetMapping
	public List<MaterialCava> obtenerTodos() {
		return mcservice.buscarTodos();
	}

	// Obtener todas las relaciones de un cava en concreto
	@GetMapping("/{idCava}")
	public List<MaterialCava> obtenerMaterialCava(@PathVariable String idCava) {
		return mcservice.findByCavaId(idCava);
	}

	@PostMapping("/{idCava}/{idMaterial}")
	public ResponseEntity<?> asignarMaterial(@PathVariable String idCava, @PathVariable long idMaterial) {
	    try {
	        // Buscar la cava
	        Cava cava = cservice.buscar(idCava);
	        if (cava == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("Error: No se encontró la cava con ID: " + idCava);
	        }

	        // Buscar el material
	        Material material = mservice.buscar(idMaterial);
	        if (material == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("Error: No se encontró el material con ID: " + idMaterial);
	        }

	        // Crear la nueva relación
	        MaterialCava nuevaRelacion = new MaterialCava();
	        nuevaRelacion.setCava(cava);
	        nuevaRelacion.setMaterial(material);
	        nuevaRelacion.setCantidadNecesariaPorBotella(1);

	        // Intentar guardar
	        MaterialCava guardado = mcservice.insertar(nuevaRelacion);

	        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);

	    } catch (Exception e) {
	        // Captura cualquier excepción que ocurra en el proceso
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
