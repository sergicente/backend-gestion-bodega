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
	@Autowired
	private ModelMapper mapper;
	
    // Obtener todas las partidas
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        List<MovimientoMaterial> lista = mservice.buscarTodos();
        List<MovimientoMaterialDto> dtos = new ArrayList<>();

        for (MovimientoMaterial m : lista) {
            MovimientoMaterialDto dto = mapper.map(m, MovimientoMaterialDto.class);
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
    }
    
    
    // Obtener un movimiento
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable Long id) {
        MovimientoMaterial movimiento = mservice.buscar(id);
        if (movimiento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el movimiento");
        } else {
            MovimientoMaterialDto dto = mapper.map(movimiento, MovimientoMaterialDto.class);
            return ResponseEntity.ok(dto);
        }
    }
    
    // Obtener un movimiento
    @GetMapping("/articulo/{id}")
    public ResponseEntity<?> obtenerMovimientosDeUnArticulo(@PathVariable Long id) {
        List<MovimientoMaterial> movimientos = mservice.findByMaterialId(id);

        List<MovimientoMaterialDto> dtos = new ArrayList<>();
        for (MovimientoMaterial m : movimientos) {
            MovimientoMaterialDto dto = mapper.map(m, MovimientoMaterialDto.class);
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
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

        int stockFinal;

        // Actualizar stock
        switch (dto.getTipo()) {
            case ENTRADA:
                stockFinal = material.getCantidad() + dto.getCantidad();
                break;
            case SALIDA:
                stockFinal = material.getCantidad() - dto.getCantidad();
                if (stockFinal < 0) {
                    throw new IllegalArgumentException("No hay suficiente stock para realizar la salida");
                }
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento desconocido");
        }

        material.setCantidad(stockFinal);
        matservice.insertar(material);

        // Guardar el movimiento
        MovimientoMaterial movimiento = mapper.map(dto, MovimientoMaterial.class);
        movimiento.setMaterial(material);
        movimiento.setStockResultante(stockFinal);

        movimiento = mservice.insertar(movimiento);

        // Preparar DTO de respuesta
        MovimientoMaterialDto nuevoDto = mapper.map(movimiento, MovimientoMaterialDto.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }
}
