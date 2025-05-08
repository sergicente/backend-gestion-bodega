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
import cava.model.dto.MovimientoDto;
import cava.model.entity.Cava;
import cava.model.entity.Material;
import cava.model.entity.Movimiento;
import cava.model.entity.Partida;
import cava.model.entity.TipoMaterial;
import cava.model.entity.TipoMovimiento;
import cava.model.service.CavaService;
import cava.model.service.MaterialService;
import cava.model.service.MovimientoService;
import cava.model.service.PartidaService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/movimiento")
@CrossOrigin(origins = "*")
public class MovimientoController {
	
	@Autowired
	private MovimientoService mservice;
	
	@Autowired
	private PartidaService pservice;
	
	@Autowired
	private MaterialService matservice;
	
    // Obtener todas las partidas
    @GetMapping
    public List<Movimiento> obtenerTodos() {
        return mservice.buscarTodos();
    }
    
    
    // Obtener un movimiento
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable Long id) {
    	Movimiento movimiento = mservice.buscar(id);
    	if(movimiento == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
    	}else {
    		return ResponseEntity.ok(movimiento);
    	}
    }
    
    // Obtener un movimiento
    @GetMapping("/articulo/{id}")
    public ResponseEntity<?> obtenerMovimientosDeUnArticulo(@PathVariable Long id) {
    	List <Movimiento> movimientos = mservice.findByMaterialId(id);
    	if(movimientos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentran movimientos");
    	}else {
    		return ResponseEntity.ok(movimientos);
    	}
    }
    
    
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
        Movimiento existente = mservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el movimiento con ID " + id);
        }
        mservice.borrar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody MovimientoDto dto) {
        // Convertir DTO a entidad
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(dto.getFecha());
        movimiento.setTipo(dto.getTipo());
        movimiento.setDescripcion(dto.getDescripcion());
        movimiento.setCantidad(dto.getCantidad());

        if (dto.getPartidaId() != null) {
            Partida partida = pservice.buscar(dto.getPartidaId());
            if (partida == null) {
                throw new EntityNotFoundException("No se encontró la partida: " + dto.getPartidaId());
            }
            movimiento.setPartida(partida);
        }

        if (dto.getMaterialId() != null) {
            Material material = matservice.buscar(dto.getMaterialId());
            if (material == null) {
                throw new EntityNotFoundException("No se encontró el material: " + dto.getMaterialId());
            }

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

            movimiento.setMaterial(material);
        }

        // Guardar el movimiento
        movimiento = mservice.insertar(movimiento);

        // Preparar DTO de respuesta
        MovimientoDto nuevoDto = new MovimientoDto();
        nuevoDto.setId(movimiento.getId());
        nuevoDto.setFecha(movimiento.getFecha());
        nuevoDto.setTipo(movimiento.getTipo());
        nuevoDto.setDescripcion(movimiento.getDescripcion());
        nuevoDto.setCantidad(movimiento.getCantidad());

        if (movimiento.getPartida() != null) {
            nuevoDto.setPartidaId(movimiento.getPartida().getId());
        }
        if (movimiento.getMaterial() != null) {
            nuevoDto.setMaterialId(movimiento.getMaterial().getId());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }
    

}
