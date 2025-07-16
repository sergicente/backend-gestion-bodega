package cava.model.restcontroller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cava.model.dto.CavaPartidaDto;
import cava.model.dto.VentaRequestDto;
import cava.model.entity.Cava;
import cava.model.entity.CavaPartida;
import cava.model.entity.Partida;
import cava.model.service.CavaPartidaService;
import cava.model.service.CavaService;
import cava.model.service.PartidaService;

@RestController
@RequestMapping("/api/cavapartida")
public class CavaPartidaController {

	@Autowired
	private CavaPartidaService cpservice;
	@Autowired
	private CavaService cservice;
	@Autowired
	private PartidaService pservice;
	@Autowired
	private ModelMapper mapper;

	// Obtener todas las relaciones
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
	    List<CavaPartida> lista = cpservice.buscarTodos();
	    List<CavaPartidaDto> dtos = new ArrayList<>();

	    for (CavaPartida cp : lista) {
	    	CavaPartidaDto dto = mapper.map(cp, CavaPartidaDto.class);
	    	dto.setId(cp.getId());
	        dtos.add(dto);
	    }

	    return ResponseEntity.ok(dtos);
	}

	// Obtener todas las relaciones de un cava en concreto
	@GetMapping("/{idCava}")
	public ResponseEntity<?> obtenerMaterialCava(@PathVariable String idCava) {
	    List<CavaPartida> relaciones = cpservice.findByCavaId(idCava);
	    List<CavaPartidaDto> relacionesDto = new ArrayList<>();

	    for (CavaPartida relacion : relaciones) {
	    	CavaPartidaDto dto = mapper.map(relacion, CavaPartidaDto.class);
	        relacionesDto.add(dto);
	    }

	    return ResponseEntity.ok(relacionesDto);
	}
	
	@GetMapping("/relacion/{id}")
	public ResponseEntity<?> obtenerRelacionPorId(@PathVariable Long id) {
	    CavaPartida relacion = cpservice.buscar(id);
	    CavaPartidaDto relacionDto = mapper.map(relacion, CavaPartidaDto.class);
	    if (relacion != null) {
	        return ResponseEntity.ok(relacionDto);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Relación no encontrada");
	    }
	}

	@PostMapping("/{idCava}/{idPartida}")
	public ResponseEntity<?> asignarPartida(
	    @PathVariable String idCava,
	    @PathVariable String idPartida,
	    @RequestBody CavaPartidaDto datos) {
		
		
	    try {
	        Cava cava = cservice.buscar(idCava);
	        if (cava == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No se encontró la cava con ID: " + idCava);
	        }

	        Partida partida = pservice.buscar(idPartida);
	        if (partida == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No se encontró la partida con ID: " + idPartida);
	        }

	        // Verificar si ya existe
	        Optional<CavaPartida> existente = cpservice.buscarPorCavaYPartida(idCava, idPartida);
	        
	        

	        if (existente.isPresent()) {
	            CavaPartida relacion = existente.get();

	            System.out.println("Cantidad previa: " + relacion.getCantidad());
	            System.out.println("Cantidad a añadir: " + datos.getCantidad());

	            int nuevaCantidad = relacion.getCantidad() + datos.getCantidad();
	            relacion.setCantidad(nuevaCantidad);

	            System.out.println("Cantidad resultante: " + relacion.getCantidad());

	            CavaPartida actualizado = cpservice.modificar(relacion); // usa `modificar` en vez de `insertar` si lo tienes

	            return ResponseEntity.ok(mapper.map(actualizado, CavaPartidaDto.class));
	        }else {
	            CavaPartida nuevaRelacion = new CavaPartida();
	            nuevaRelacion.setCava(cava);
	            nuevaRelacion.setPartida(partida);
	            nuevaRelacion.setCantidad(datos.getCantidad());
	            nuevaRelacion.setActual(false);

	            CavaPartida guardado = cpservice.insertar(nuevaRelacion);
	            CavaPartidaDto dto = mapper.map(guardado, CavaPartidaDto.class);
	            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	        }

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al asignar la partida al cava: " + e.getMessage());
	    }
	}
	
	
	@GetMapping("/existe/{idCava}/{idPartida}")
	public ResponseEntity<Boolean> existeRelacion(@PathVariable String idCava, @PathVariable String idPartida) {
	    Optional<CavaPartida> relacion = cpservice.buscarPorCavaYPartida(idCava, idPartida);
	    return ResponseEntity.ok(relacion.isPresent());
	}
	
	
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
        CavaPartida existente = cpservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la relación con id " + id);
        }
        // Verificar que vendido y cantidad sean ambos igual a 0 antes de borrar
        if (existente.getVendido() > 0 || existente.getCantidad() > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aquesta relació ja conté dades guardades");
        }
        cpservice.borrar(id);
        return ResponseEntity.noContent().build();
    }
	
	
    @PutMapping("/activar/{id}")
    public ResponseEntity<?> activar(@PathVariable Long id) {
    	CavaPartida existente = cpservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la relación con id " + id);
        }
        
        List<CavaPartida> relaciones = cpservice.findByCavaId(existente.getCava().getId());
        for(CavaPartida partida: relaciones) {
        	if(partida.isActual()) {
        		partida.setActual(false);
        	}
        	existente.setActual(true);
        }
        
        cpservice.modificar(existente);
        return ResponseEntity.ok().build();
    }
    
    
    
    
    @PutMapping("/vender/{id}")
    public ResponseEntity<?> actualizarStock(
        @PathVariable Long id,
        @RequestBody VentaRequestDto request
    ) {
        CavaPartida cp = cpservice.buscar(id);
        int disponibles = cp.getCantidad();
        int vendidasNueva = request.getUnidades();
        int vendidasPrevias = request.getUnidadesPrevias();

        int diferencia = vendidasNueva - vendidasPrevias;

        if (diferencia < 0 && Math.abs(diferencia) > cp.getVendido()) {
            return ResponseEntity.badRequest().body("No se puede revertir más de lo vendido.");
        }

        if (diferencia > disponibles) {
            return ResponseEntity.badRequest().body("No hay suficiente stock disponible.");
        }

        cp.setCantidad(disponibles - diferencia);
        cp.setVendido(cp.getVendido() + diferencia);
        cp.setUltimaActualizacion(LocalDateTime.now());

        Partida partida = cp.getPartida();
        partida.setBotellasStock(partida.getBotellasStock() - diferencia);
        partida.setBotellasVendidas(partida.getBotellasVendidas() + diferencia);

        cpservice.modificar(cp);

        Map<String, Object> body = new HashMap<>();
        body.put("mensaje", "Stock actualizado");
        body.put("nuevoStock", cp.getCantidad());
        body.put("vendido", cp.getVendido());

        CavaPartidaDto dto = mapper.map(cp, CavaPartidaDto.class);
        return ResponseEntity.ok(dto);
    }
    

    
}
