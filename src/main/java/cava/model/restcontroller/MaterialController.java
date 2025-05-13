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

import cava.model.dto.MaterialDto;
import cava.model.entity.Categoria;
import cava.model.entity.Familia;
import cava.model.entity.Material;
import cava.model.entity.TipoMaterial;
import cava.model.service.CategoriaService;
import cava.model.service.FamiliaService;
import cava.model.service.MaterialService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/material")
@CrossOrigin(origins = "*")
public class MaterialController {
	
	@Autowired
	private MaterialService mservice;
	@Autowired
	private FamiliaService fservice;
	@Autowired
	private CategoriaService catservice;
	
	
    // Obtener todas las partidas
    @GetMapping
    public List<Material> obtenerTodos() {
        return mservice.buscarTodos();
    }
    
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable long id) {
    	Material material = mservice.buscar(id);
    	if(material == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el material");
    	}else {
    		return ResponseEntity.ok(material);
    	}
    }
    
    
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody MaterialDto materialDto) {

        // Buscar familia
        Categoria categoria = catservice.buscar(materialDto.getCategoriaId());
        if (categoria == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria no encontrada");
        }
    	
        // Buscar familia
        Familia familia = fservice.buscar(materialDto.getFamiliaId());
        if (familia == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Familia no encontrada");
        }

        // Crear material
        Material material = new Material(
            null,
            materialDto.getNombre(),
            categoria,
            familia,
            materialDto.getObservaciones(),
            materialDto.getCantidad()
        );

        // Guardar
        Material nuevoMaterial = mservice.insertar(material);

        // Convertir a DTO
        MaterialDto nuevoDto = new MaterialDto(
            nuevoMaterial.getId(),
            nuevoMaterial.getNombre(),
            nuevoMaterial.getCategoria().getId(),
            nuevoMaterial.getFamilia().getId(),
            nuevoMaterial.getObservaciones(),
            nuevoMaterial.getCantidad()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }
    
    
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody MaterialDto materialDto) {
        try {
            if (!id.equals(materialDto.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID en la URL y en el body no coinciden");
            }

            Material existente = mservice.buscar(id);
            if (existente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Material no encontrado con ID " + id);
            }

            Familia familia = fservice.buscar(materialDto.getFamiliaId());
            if (familia == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Familia no encontrada");
            }
            
            // Buscar familia
            Categoria categoria = catservice.buscar(materialDto.getCategoriaId());
            if (categoria == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria no encontrada");
            }

            // Modificar directamente
            existente.setNombre(materialDto.getNombre());
            existente.setCategoria(categoria);
            existente.setFamilia(familia);
            existente.setObservaciones(materialDto.getObservaciones());
            existente.setCantidad(materialDto.getCantidad());

            Material actualizado = mservice.modificar(existente);

            MaterialDto actualizadoDto = new MaterialDto(
                actualizado.getId(),
                actualizado.getNombre(),
                actualizado.getCategoria().getId(),
                actualizado.getFamilia().getId(),
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
    public ResponseEntity<?> borrar(@PathVariable Long id) {
        Material existente = mservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el material con ID " + id);
        }
        mservice.borrar(id);
        return ResponseEntity.noContent().build();
    }
	
}
