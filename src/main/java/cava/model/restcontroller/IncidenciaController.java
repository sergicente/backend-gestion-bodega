package cava.model.restcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import jakarta.transaction.Transactional;


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

	// Obtener todos los proveedores
	@GetMapping("/partida/{id}")
	public ResponseEntity<?> obtenerTodosPartida(@PathVariable String id) {
		List<Incidencia> incidencias = iservice.findByPartidaId(id);
		List<IncidenciaDto> dtos = new ArrayList<>();

		for (Incidencia i : incidencias) {
			IncidenciaDto dto = mapper.map(i, IncidenciaDto.class);
			dtos.add(dto);
		}

		return ResponseEntity.ok(dtos);
	}


	// Obtener todos los proveedores
	@GetMapping("/cava/{id}")
	public ResponseEntity<?> obtenerTodosCava(@PathVariable String id) {
		List<Incidencia> incidencias = iservice.findByCavaId(id);
		List<IncidenciaDto> dtos = new ArrayList<>();

		for (Incidencia i : incidencias) {
			IncidenciaDto dto = mapper.map(i, IncidenciaDto.class);
			dtos.add(dto);
		}

		return ResponseEntity.ok(dtos);
	}


	@PostMapping("/insertar")
	@Transactional
	public ResponseEntity<?> insertarUno(@RequestBody IncidenciaDto dto) {
	    // Buscar la partida
	    Partida partida = pservice.buscar(dto.getPartidaId());
	    if (partida == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Partida no encontrada con ID " + dto.getPartidaId());
	    }

	    // Buscar el cava si aplica
	    Cava cava = null;
	    if (dto.getCavaId() != null) {
	        cava = cservice.buscar(dto.getCavaId());
	        if (cava == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("Cava no encontrada con ID " + dto.getCavaId());
	        }
	    }

	    // Crear la incidencia
	    Incidencia incidencia = new Incidencia();
	    incidencia.setFecha(dto.getFecha());
	    incidencia.setTipo(dto.getTipo());
	    incidencia.setCantidad(dto.getCantidad());
	    incidencia.setDetalles(dto.getDetalles());
	    incidencia.setPartida(partida);
	    incidencia.setCava(cava);

	    if (dto.getTipo() == TipoIncidencia.RIMA) {
	        partida.setBotellasMerma(partida.getBotellasMerma() + dto.getCantidad());
	        partida.setBotellasRima(partida.getBotellasRima() - dto.getCantidad());
	        pservice.modificar(partida);
	    }

	    else if (dto.getTipo() == TipoIncidencia.STOCK) {
			Optional<CavaPartida> cpOptional = cpservice.buscarPorCavaYPartida(cava.getId(),partida.getId());

	        if (cpOptional.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("No existe relación entre la cava y la partida");
	        }

	        CavaPartida cp = cpOptional.get();

	        int nuevoStock = cp.getCantidad() - dto.getCantidad();
	        if (nuevoStock < 0) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("No hay suficiente stock en esta partida");
	        }

	        cp.setCantidad(nuevoStock);
	        cpservice.modificar(cp);

	        partida.setBotellasMerma(partida.getBotellasMerma() + dto.getCantidad());
	        partida.setBotellasStock(partida.getBotellasStock() - dto.getCantidad());
	        pservice.modificar(partida);
	    }

	    Incidencia nuevo = iservice.insertar(incidencia);
	    IncidenciaDto respuesta = mapper.map(nuevo, IncidenciaDto.class);
	    return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}


    @Transactional
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody IncidenciaDto dto) {
        Incidencia existente = iservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la incidencia con ID " + id);
        }

        // Validar nueva partida
        Partida nuevaPartida = pservice.buscar(dto.getPartidaId());
        if (nuevaPartida == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Partida no encontrada con ID " + dto.getPartidaId());
        }

        // Validar nueva cava si aplica
        Cava nuevaCava = null;
        if (dto.getCavaId() != null) {
            nuevaCava = cservice.buscar(dto.getCavaId());
            if (nuevaCava == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cava no encontrada con ID " + dto.getCavaId());
            }
        }

        // Validar relación y stock suficiente (si tipo es STOCK)
        CavaPartida nuevaRelacionCP = null;
        if (dto.getTipo() == TipoIncidencia.STOCK && nuevaCava != null) {
            Optional<CavaPartida> cpOpt = cpservice.buscarPorCavaYPartida(nuevaCava.getId(), nuevaPartida.getId());
            if (cpOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No existe relación entre la cava y la partida");
            }

            nuevaRelacionCP = cpOpt.get();
            int nuevoStock = nuevaRelacionCP.getCantidad() - dto.getCantidad();
            if (nuevoStock < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay suficiente stock en la cava para esta rotura");
            }
        }

        // Si todas las validaciones pasan, revertir efectos anteriores
        Partida partidaAnterior = existente.getPartida();
        Cava cavaAnterior = existente.getCava();

        if (existente.getTipo() == TipoIncidencia.RIMA) {
            partidaAnterior.setBotellasMerma(partidaAnterior.getBotellasMerma() - existente.getCantidad());
            partidaAnterior.setBotellasRima(partidaAnterior.getBotellasRima() + existente.getCantidad());
            pservice.modificar(partidaAnterior);
        } else if (existente.getTipo() == TipoIncidencia.STOCK && cavaAnterior != null) {
            Optional<CavaPartida> cpOpt = cpservice.buscarPorCavaYPartida(cavaAnterior.getId(), partidaAnterior.getId());
            if (cpOpt.isPresent()) {
                CavaPartida cp = cpOpt.get();
                cp.setCantidad(cp.getCantidad() + existente.getCantidad());
                cpservice.modificar(cp);

                partidaAnterior.setBotellasMerma(partidaAnterior.getBotellasMerma() - existente.getCantidad());
                partidaAnterior.setBotellasStock(partidaAnterior.getBotellasStock() + existente.getCantidad());
                pservice.modificar(partidaAnterior);
            }
        }

        // Aplicar efectos nuevos
        if (dto.getTipo() == TipoIncidencia.RIMA) {
            nuevaPartida.setBotellasMerma(nuevaPartida.getBotellasMerma() + dto.getCantidad());
            nuevaPartida.setBotellasRima(nuevaPartida.getBotellasRima() - dto.getCantidad());
            pservice.modificar(nuevaPartida);
        } else if (dto.getTipo() == TipoIncidencia.STOCK && nuevaRelacionCP != null) {
            nuevaRelacionCP.setCantidad(nuevaRelacionCP.getCantidad() - dto.getCantidad());
            cpservice.modificar(nuevaRelacionCP);

            nuevaPartida.setBotellasMerma(nuevaPartida.getBotellasMerma() + dto.getCantidad());
            nuevaPartida.setBotellasStock(nuevaPartida.getBotellasStock() - dto.getCantidad());
            pservice.modificar(nuevaPartida);
        }

        // Actualizar incidencia
        existente.setTipo(dto.getTipo());
        existente.setCantidad(dto.getCantidad());
        existente.setDetalles(dto.getDetalles());
        existente.setFecha(dto.getFecha());
        existente.setPartida(nuevaPartida);
        existente.setCava(nuevaCava);

        iservice.modificar(existente);

        return ResponseEntity.ok(Map.of("mensaje", "Incidencia modificada correctamente"));
    }


	// Borrar incidencia
	@DeleteMapping("/borrar/{id}")
	public ResponseEntity<?> borrar(@PathVariable Long id) {
		Incidencia existente = iservice.buscar(id);
	    if (existente == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la incidencia");
	    }
		Partida partida = existente.getPartida();
		// Buscar el cava si aplica
		Cava cava = existente.getCava();

		if (existente.getTipo() == TipoIncidencia.RIMA) {
			partida.setBotellasMerma(partida.getBotellasMerma() - existente.getCantidad());
			partida.setBotellasRima(partida.getBotellasRima() + existente.getCantidad());
			pservice.modificar(partida);
		}

		else if (existente.getTipo() == TipoIncidencia.STOCK) {
			Optional<CavaPartida> cpOptional = cpservice.buscarPorCavaYPartida(cava.getId(),partida.getId());

			if (cpOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("No existe relación entre la cava y la partida");
			}

			CavaPartida cp = cpOptional.get();

			int nuevoStock = cp.getCantidad() + existente.getCantidad();

			cp.setCantidad(nuevoStock);
			cpservice.modificar(cp);

			partida.setBotellasMerma(partida.getBotellasMerma() - existente.getCantidad());
			partida.setBotellasStock(partida.getBotellasStock() + existente.getCantidad());
			pservice.modificar(partida);
		}

	    iservice.borrar(id);
	    return ResponseEntity.noContent().build();
	}

}
