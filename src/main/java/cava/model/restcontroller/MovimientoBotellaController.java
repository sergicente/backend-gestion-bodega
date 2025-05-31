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

import cava.model.dto.MovimientoBotellaDto;
import cava.model.dto.MovimientoMaterialDto;
import cava.model.entity.Cava;
import cava.model.entity.Material;
import cava.model.entity.MaterialCava;
import cava.model.entity.MovimientoBotella;
import cava.model.entity.MovimientoMaterial;
import cava.model.entity.Partida;
import cava.model.entity.TipoMovimientoMaterial;
import cava.model.service.CavaService;
import cava.model.service.MaterialCavaService;
import cava.model.service.MaterialService;
import cava.model.service.MovimientoBotellaService;
import cava.model.service.MovimientoMaterialService;
import cava.model.service.PartidaService;
import jakarta.transaction.Transactional;


@RestController
@RequestMapping("/api/movimiento_botella")
@CrossOrigin(origins = "*")
public class MovimientoBotellaController {
	
	@Autowired
	private MovimientoBotellaService mservice;
	@Autowired
	private MovimientoMaterialService mmservice;
	@Autowired
	private PartidaService pservice;
	@Autowired
	private CavaService cservice;
	@Autowired
	private MaterialService matservice;
	@Autowired
	private MaterialCavaService mcservice;
	@Autowired
	private ModelMapper mapper;
	
	
	
	
    // Obtener todas las partidas
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
	    List<MovimientoBotella> lista = mservice.buscarTodos();
	    List<MovimientoBotellaDto> dtos = new ArrayList<>();

	    for (MovimientoBotella m : lista) {
	        MovimientoBotellaDto dto = mapper.map(m, MovimientoBotellaDto.class);
	        dtos.add(dto);
	    }

	    return ResponseEntity.ok(dtos);
	}
    
    
    // Obtener un movimiento
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerUno(@PathVariable Long id) {
	    MovimientoBotella movimiento = mservice.buscar(id);

	    if (movimiento == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el movimiento");
	    } else {
	        MovimientoBotellaDto dto = mapper.map(movimiento, MovimientoBotellaDto.class);
	        return ResponseEntity.ok(dto);
	    }
	}

    
    
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
    	MovimientoBotella existente = mservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el movimiento con ID " + id);
        }
        mservice.borrar(id);
        return ResponseEntity.noContent().build();
    }
    
    @Transactional
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody MovimientoBotellaDto dto) {

        Partida partida = pservice.buscar(dto.getPartidaId());
        Cava cava = cservice.buscar(dto.getCavaId());
        
        // Actualizar estado anterior (resta)
        int nuevaCantidadRima = partida.getBotellasRima() - dto.getCantidad();
        if (nuevaCantidadRima < 0) {
            throw new IllegalArgumentException("No hay suficientes botellas en rima");
        }
        partida.setBotellasRima(nuevaCantidadRima);
        
        
        
        // Actualizar estado nuevo (suma)

        partida.setBotellasStock(partida.getBotellasStock() + dto.getCantidad());
        
        // Restar materiales
        
        List<MaterialCava> lista = mcservice.findByCavaId(dto.getCavaId());
        for(MaterialCava mat : lista) {
        	Material material = mat.getMaterial();
        	int cantidadARestar = Math.round(mat.getMaterial().getCantidadGastada() * dto.getCantidad());
        	int nuevoStock = material.getCantidad() - cantidadARestar;
        	if(nuevoStock < 0 ) {
        		throw new IllegalArgumentException("No hay suficiente stock del material: " + material.getNombre());
        	}
        	material.setCantidad(nuevoStock);
        	matservice.insertar(material);
        	
        	MovimientoMaterial mov = new MovimientoMaterial();
        	mov.setFecha(dto.getFecha());
        	mov.setTipo(TipoMovimientoMaterial.SALIDA);
        	mov.setDescripcion("Degüelle " + dto.getCantidad() + " botellas de " + cava.getNombre() + " (" + dto.getLot() + ")");
        	mov.setCantidad(cantidadARestar);
        	mov.setMaterial(material);
        	mov.setStockResultante(nuevoStock);
        	mmservice.insertar(mov);
        	
        
        }
                
        
        pservice.insertar(partida);

        MovimientoBotella movimiento = new MovimientoBotella();
        movimiento.setPartida(partida);
        movimiento.setCava(cava);
        movimiento.setFecha(dto.getFecha());
        movimiento.setDescripcion(dto.getDescripcion());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setLot(dto.getLot());
        movimiento.setEstadoNuevo(dto.getEstadoNuevo());
        movimiento.setEstadoAnterior(dto.getEstadoAnterior());

        movimiento = mservice.insertar(movimiento);

        MovimientoBotellaDto nuevoDto = new MovimientoBotellaDto();
        nuevoDto.setId(movimiento.getId());
        nuevoDto.setFecha(movimiento.getFecha());
        nuevoDto.setDescripcion(movimiento.getDescripcion());
        nuevoDto.setCantidad(movimiento.getCantidad());
        nuevoDto.setPartidaId(partida.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }
    

}
