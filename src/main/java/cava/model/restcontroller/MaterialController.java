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

import cava.model.dto.CavaDto;
import cava.model.dto.MaterialDto;
import cava.model.entity.Cava;
import cava.model.entity.Material;
import cava.model.entity.Partida;
import cava.model.entity.TipoMaterial;
import cava.model.service.CavaService;
import cava.model.service.MaterialService;
import cava.model.service.PartidaService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/material")
@CrossOrigin(origins = "*")
public class MaterialController {
	
	@Autowired
	private MaterialService mservice;
	
	
	
    // Obtener todas las partidas
    @GetMapping
    public List<Material> obtenerTodos() {
        return mservice.buscarTodos();
    }
    
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable int id) {
    	Material material = mservice.buscar(id);
    	if(material == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
    	}else {
    		return ResponseEntity.ok(material);
    	}
    }
    
    
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody MaterialDto materialDto) {

        // Convertir DTO a entidad
        Material material = new Material(
                0, materialDto.getNombre(),
                TipoMaterial.valueOf(materialDto.getTipo()),
                materialDto.getObservaciones(),
                materialDto.getCantidad()
        );

        // Guardar
        Material nuevoMaterial = mservice.insertar(material);

        // Convertir a DTO
        MaterialDto nuevoDto = new MaterialDto(
                nuevoMaterial.getId(),
                nuevoMaterial.getNombre(),
                nuevoMaterial.getTipo().toString(),
                nuevoMaterial.getObservaciones(),
                nuevoMaterial.getCantidad()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }
    
    
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable int id, @RequestBody MaterialDto materialDto) {
        try {
            // Comprobamos que el ID en la URL y en el body coincidan
            if (!(materialDto.getId() == id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID en la URL y en el body no coinciden");
            }

            // Buscar el material existente
            Material existente = mservice.buscar(id);
            if (existente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Material no encontrado con ID " + id);
            }

            // Montamos el objeto Material actualizado
            Material material = new Material();
            material.setId(id);
            material.setNombre(materialDto.getNombre());
            material.setTipo(TipoMaterial.valueOf(materialDto.getTipo()));
            material.setObservaciones(materialDto.getObservaciones());
            material.setCantidad(materialDto.getCantidad());

            // Modificamos
            Material actualizado = mservice.modificar(material);

            // Convertimos a DTO para devolver
            MaterialDto actualizadoDto = new MaterialDto(
                    actualizado.getId(),
                    actualizado.getNombre(),
                    actualizado.getTipo().toString(),
                    actualizado.getObservaciones(),
                    actualizado.getCantidad()
            );

            return ResponseEntity.ok(actualizadoDto);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }
	
    
    
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable int id) {
        Material existente = mservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el material con ID " + id);
        }
        mservice.borrar(id);
        return ResponseEntity.noContent().build();
    }
	
}
