package cava.model.restcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import cava.model.dto.IncidenciaDto;
import cava.model.dto.ProveedorDto;
import cava.model.dto.VentaDto;
import cava.model.entity.Cava;
import cava.model.entity.CavaPartida;
import cava.model.entity.Incidencia;
import cava.model.entity.Partida;
import cava.model.entity.Proveedor;
import cava.model.entity.TipoIncidencia;
import cava.model.entity.Venta;
import cava.model.service.CavaPartidaService;
import cava.model.service.CavaService;
import cava.model.service.IncidenciaService;
import cava.model.service.PartidaService;
import cava.model.service.ProveedorService;
import cava.model.service.VentaService;


@RestController
@RequestMapping("/api/incidencia")
@CrossOrigin(origins = "*")
public class IncidenciaController {
	
	@Autowired
	private IncidenciaService iservice;
	@Autowired
	private PartidaService pservice;
	@Autowired
	private CavaService cservice;
	@Autowired
	private CavaPartidaService cpservice;
	@Autowired
	private ModelMapper mapper;
	
	// Obtener todos los proveedores
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
	    List<Incidencia> incidencias = iservice.buscarTodos();
	    List<IncidenciaDto> dtos = new ArrayList<>();

	    for (Incidencia i : incidencias) {
	    	IncidenciaDto dto = mapper.map(i, IncidenciaDto.class);
	        dtos.add(dto);
	    }

	    return ResponseEntity.ok(dtos);
	}

	
	// Obtener un proveedor por ID
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerUno(@PathVariable Long id) {
		Incidencia p = iservice.buscar(id);
	    if (p == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el proveedor");
	    } else {
	    	IncidenciaDto dto = mapper.map(p, IncidenciaDto.class);
	        return ResponseEntity.ok(dto);
	    }
	}

	
	@PostMapping("/insertar")
	public ResponseEntity<?> insertarUno(@RequestBody IncidenciaDto dto) {
	    // Buscar la partida asociada
	    Partida partida = pservice.buscar(dto.getPartidaId());
	    if (partida == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Partida no encontrada con ID " + dto.getPartidaId());
	    }

	    // Buscar la cava (si aplica)
	    Cava cava = null;
	    if (dto.getCavaId() != null) {
	        cava = cservice.buscar(dto.getCavaId());
	        if (cava == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cava no encontrada con ID " + dto.getCavaId());
	        }
	    }

	    // Crear y asignar manualmente la incidencia
	    Incidencia incidencia = new Incidencia();
	    incidencia.setFecha(dto.getFecha());
	    incidencia.setTipo(dto.getTipo());
	    incidencia.setCantidad(dto.getCantidad());
	    incidencia.setPartida(partida);
	    incidencia.setDetalles(dto.getDetalles());
	    incidencia.setCava(cava);
	    
	    if (dto.getTipo() == TipoIncidencia.RIMA){
		    partida.setBotellasRotas(dto.getCantidad());
		    partida.setBotellasRima(partida.getBotellasRima()-dto.getCantidad());
		    pservice.insertar(partida);
	    }else if(dto.getTipo() == TipoIncidencia.STOCK){
	        Optional<CavaPartida> cpOptional = cpservice.buscarPorCavaYPartida(
	                cava.getId().toString(),
	                partida.getId().toString()
	            );

	            if (cpOptional.isEmpty()) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("No existe relación entre la cava y la partida");
	            }

	            CavaPartida cp = cpOptional.get();

	            int nuevoStock = cp.getCantidad() - dto.getCantidad();
	            if (nuevoStock < 0) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("No hay suficiente stock en la cava para esta rotura");
	            }

	            cp.setCantidad(nuevoStock);
	            cpservice.insertar(cp);

	            // Actualiza también la partida
	            partida.setBotellasRotas(partida.getBotellasRotas() + dto.getCantidad());
	            pservice.insertar(partida);
	        }

	    

	    Incidencia nuevo = iservice.insertar(incidencia);
	    IncidenciaDto respuesta = mapper.map(nuevo, IncidenciaDto.class);
	    return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	
	@PutMapping("/modificar/{id}")
	public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody IncidenciaDto dto) {
	    Incidencia existente = iservice.buscar(id);
	    if (existente == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la incidencia con ID " + id);
	    }

	    // Buscar la partida asociada
	    Partida partida = pservice.buscar(dto.getPartidaId());
	    if (partida == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Partida no encontrada con ID " + dto.getPartidaId());
	    }

	    // Buscar la cava (si aplica)
	    Cava cava = null;
	    if (dto.getCavaId() != null) {
	        cava = cservice.buscar(dto.getCavaId());
	        if (cava == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cava no encontrada con ID " + dto.getCavaId());
	        }
	    }

	    // Actualizar campos
	    existente.setFecha(dto.getFecha());
	    existente.setTipo(dto.getTipo());
	    existente.setCantidad(dto.getCantidad());
	    existente.setPartida(partida);
	    existente.setCava(cava);

	    Incidencia actualizado = iservice.modificar(existente);
	    IncidenciaDto respuesta = mapper.map(actualizado, IncidenciaDto.class);
	    return ResponseEntity.ok(respuesta);
	}

	
	// Borrar proveedor
	@DeleteMapping("/borrar/{id}")
	public ResponseEntity<?> borrar(@PathVariable Long id) {
		Incidencia existente = iservice.buscar(id);
	    if (existente == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la incidencia");
	    }
	    iservice.borrar(id);
	    return ResponseEntity.noContent().build();
	}
	
}
