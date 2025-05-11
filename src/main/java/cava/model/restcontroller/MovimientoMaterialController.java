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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import cava.model.dto.MovimientoMaterialDto;
import cava.model.entity.Material;
import cava.model.entity.MovimientoMaterial;

import cava.model.service.MaterialService;
import cava.model.service.MovimientoMaterialService;
import cava.model.service.PartidaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/movimiento_material")
@CrossOrigin(origins = "*")
public class MovimientoMaterialController {
	
	@Autowired
	private MovimientoMaterialService mservice;
	
	@Autowired
	private PartidaService pservice;
	
	@Autowired
	private MaterialService matservice;
	
    // Obtener todas las partidas
    @GetMapping
    public List<MovimientoMaterial> obtenerTodos() {
        return mservice.buscarTodos();
    }
    
    
    // Obtener un movimiento
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable Long id) {
    	MovimientoMaterial movimiento = mservice.buscar(id);
    	if(movimiento == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
    	}else {
    		return ResponseEntity.ok(movimiento);
    	}
    }
    
    // Obtener un movimiento
    @GetMapping("/articulo/{id}")
    public ResponseEntity<?> obtenerMovimientosDeUnArticulo(@PathVariable Long id) {
    	List <MovimientoMaterial> movimientos = mservice.findByMaterialId(id);
    	if(movimientos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentran movimientos");
    	}else {
    		return ResponseEntity.ok(movimientos);
    	}
    }
    
    
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
    	MovimientoMaterial existente = mservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el movimiento con ID " + id);
        }
        mservice.borrar(id);
        return ResponseEntity.noContent().build();
    }
    
    @Transactional
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody MovimientoMaterialDto dto) {

        if (dto.getMaterialId() == null) {
            throw new IllegalArgumentException("Debe especificarse un ID de material");
        }

        // Buscar el material
        Material material = matservice.buscar(dto.getMaterialId());
        if (material == null) {
            throw new EntityNotFoundException("No se encontró el material: " + dto.getMaterialId());
        }

        // Actualizar stock
        switch (dto.getTipo()) {
            case ENTRADA:
                material.setCantidad(material.getCantidad() + dto.getCantidad());
                break;
            case SALIDA:
                int nuevaCantidad = material.getCantidad() - dto.getCantidad();
                if (nuevaCantidad < 0) {
                    throw new IllegalArgumentException("No hay suficiente stock para realizar la salida");
                }
                material.setCantidad(nuevaCantidad);
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento desconocido");
        }

        matservice.insertar(material);

        // Guardar el movimiento
        MovimientoMaterial movimiento = new MovimientoMaterial();
        movimiento.setFecha(dto.getFecha());
        movimiento.setTipo(dto.getTipo());
        movimiento.setDescripcion(dto.getDescripcion());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setMaterial(material);

        movimiento = mservice.insertar(movimiento);

        // Preparar DTO de respuesta
        MovimientoMaterialDto nuevoDto = new MovimientoMaterialDto(
            movimiento.getId(),
            movimiento.getFecha(),
            movimiento.getTipo(),
            movimiento.getDescripcion(),
            movimiento.getCantidad(),
            material.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }
}
