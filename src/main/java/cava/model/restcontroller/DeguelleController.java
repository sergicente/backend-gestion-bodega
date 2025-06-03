package cava.model.restcontroller;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import cava.model.dto.DeguelleDto;
import cava.model.dto.MaterialDto;
import cava.model.dto.MovimientoMaterialDto;
import cava.model.entity.Cava;
import cava.model.entity.CavaPartida;
import cava.model.entity.Material;
import cava.model.entity.MaterialCava;
import cava.model.entity.Deguelle;
import cava.model.entity.MovimientoMaterial;
import cava.model.entity.Partida;
import cava.model.entity.TipoMovimientoMaterial;
import cava.model.service.CavaPartidaService;
import cava.model.service.CavaService;
import cava.model.service.MaterialCavaService;
import cava.model.service.MaterialService;
import cava.model.service.DeguelleService;
import cava.model.service.MovimientoMaterialService;
import cava.model.service.PartidaService;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/deguelle")
@CrossOrigin(origins = "*")
public class DeguelleController {

	@Autowired
	private DeguelleService mservice;
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
	private CavaPartidaService cpservice;
	@Autowired
	private ModelMapper mapper;

	// Obtener todas las partidas
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
		List<Deguelle> lista = mservice.buscarTodos();
		List<DeguelleDto> dtos = new ArrayList<>();

		for (Deguelle m : lista) {
			DeguelleDto dto = mapper.map(m, DeguelleDto.class);
			dtos.add(dto);
		}

		return ResponseEntity.ok(dtos);
	}

	// Obtener un movimiento
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerUno(@PathVariable Long id) {
		Deguelle movimiento = mservice.buscar(id);

		if (movimiento == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el movimiento");
		} else {
			DeguelleDto dto = mapper.map(movimiento, DeguelleDto.class);
			return ResponseEntity.ok(dto);
		}
	}

	@Transactional
	@PostMapping("/insertar")
	public ResponseEntity<?> insertarUno(@RequestBody DeguelleDto dto) {

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
		for (MaterialCava mat : lista) {
			Material material = mat.getMaterial();
			int cantidadARestar = Math.round(mat.getMaterial().getCantidadGastada() * dto.getCantidad());
			int nuevoStock = material.getCantidad() - cantidadARestar;
			if (nuevoStock < 0) {
				throw new IllegalArgumentException("No hay suficiente stock del material: " + material.getNombre());
			}
			material.setCantidad(nuevoStock);
			matservice.insertar(material);

			MovimientoMaterial mov = new MovimientoMaterial();
			mov.setFecha(dto.getFecha());
			mov.setTipo(TipoMovimientoMaterial.SALIDA);
			mov.setDescripcion(
					"Degüelle " + dto.getCantidad() + " botellas de " + cava.getNombre() + " (" + dto.getLot() + ")");
			mov.setCantidad(cantidadARestar);
			mov.setMaterial(material);
			mov.setStockResultante(nuevoStock);
			mmservice.insertar(mov);

		}

		pservice.insertar(partida);

		Deguelle deguelle = new Deguelle();
		deguelle.setPartida(partida);
		deguelle.setCava(cava);
		deguelle.setFecha(dto.getFecha());
		deguelle.setDescripcion(dto.getDescripcion());
		deguelle.setCantidad(dto.getCantidad());
		deguelle.setLot(dto.getLot());
		deguelle.setEstadoNuevo(dto.getEstadoNuevo());
		deguelle.setEstadoAnterior(dto.getEstadoAnterior());

		deguelle = mservice.insertar(deguelle);

		DeguelleDto deguelleDto = new DeguelleDto();
		deguelleDto.setId(deguelle.getId());
		deguelleDto.setFecha(deguelle.getFecha());
		deguelleDto.setDescripcion(deguelle.getDescripcion());
		deguelleDto.setCantidad(deguelle.getCantidad());
		deguelleDto.setPartidaId(partida.getId());

		return ResponseEntity.status(HttpStatus.CREATED).body(deguelleDto);
	}

	@Transactional
	@PutMapping("/modificar/{id}")
	public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody DeguelleDto dto) {
	    try {
	        // Obtener degüelle original
	        Deguelle degOriginal = mservice.buscar(id);
	        if (degOriginal == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Degüellé no encontrado");
	        }

	        // 1. Verificaciones previas antes de modificar nada
	        try {
	            verificarReversionPosible(degOriginal);
	            verificarStockMateriales(dto, degOriginal.getCantidad());
	        } catch (IllegalArgumentException ex) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("No se puede modificar el degüellé: " + ex.getMessage());
	        }

	        // 2. Revertir degüelle anterior
	        borrar(id);

	        // 3. Actualizar o crear nueva relación Cava-Partida
	        Partida nuevaPartida = pservice.buscar(dto.getPartidaId());
	        Cava nuevaCava = cservice.buscar(dto.getCavaId());

	        Optional<CavaPartida> relacionNuevaOpt = cpservice.buscarPorCavaYPartida(nuevaCava.getId(), nuevaPartida.getId());
	        CavaPartida nuevaRelacion;

	        if (relacionNuevaOpt.isPresent()) {
	            nuevaRelacion = relacionNuevaOpt.get();
	            nuevaRelacion.setCantidad(nuevaRelacion.getCantidad() + dto.getCantidad());
	        } else {
	            nuevaRelacion = new CavaPartida();
	            nuevaRelacion.setCava(nuevaCava);
	            nuevaRelacion.setPartida(nuevaPartida);
	            nuevaRelacion.setCantidad(dto.getCantidad());
	            nuevaRelacion.setVendido(0);
	            nuevaRelacion.setActual(true);
	            nuevaRelacion.setUltimaActualizacion(LocalDateTime.now());
	        }
	        cpservice.insertar(nuevaRelacion);

	        // 4. Insertar nuevo degüelle
	        return insertarUno(dto);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body("Error al modificar el degüellé: " + e.getMessage());
	    }
	}

	@Transactional
	@DeleteMapping("/borrar/{id}")
	public ResponseEntity<?> borrar(@PathVariable Long id) {
	    Deguelle deguelle = mservice.buscar(id);
	    if (deguelle == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Degüellé no encontrado");
	    }

	    try {
	        verificarReversionPosible(deguelle);

	        // 1. DESHACER DEGUELLE ANTERIOR
	        Partida partidaAnterior = deguelle.getPartida();
	        Cava cavaAnterior = deguelle.getCava();
	        int cantidadAnterior = deguelle.getCantidad();

	        partidaAnterior.setBotellasRima(partidaAnterior.getBotellasRima() + cantidadAnterior);
	        partidaAnterior.setBotellasStock(partidaAnterior.getBotellasStock() - cantidadAnterior);
	        pservice.insertar(partidaAnterior);

	        Optional<CavaPartida> relacionAnterior = cpservice.buscarPorCavaYPartida(cavaAnterior.getId(), partidaAnterior.getId());
	        if (relacionAnterior.isPresent()) {
	            CavaPartida relacion = relacionAnterior.get();
	            relacion.setCantidad(relacion.getCantidad() - cantidadAnterior);
	            cpservice.insertar(relacion);
	        }

	        List<MaterialCava> materiales = mcservice.findByCavaId(cavaAnterior.getId());
	        for (MaterialCava mat : materiales) {
	            Material material = mat.getMaterial();
	            int cantidadADevolver = Math.round(mat.getMaterial().getCantidadGastada() * cantidadAnterior);
	            material.setCantidad(material.getCantidad() + cantidadADevolver);
	            matservice.insertar(material);

	            MovimientoMaterial entrada = new MovimientoMaterial();
	            entrada.setFecha(deguelle.getFecha());
	            entrada.setTipo(TipoMovimientoMaterial.ENTRADA);
	            entrada.setDescripcion("Reversión degüelle modificado (" + deguelle.getLot() + ")");
	            entrada.setCantidad(cantidadADevolver);
	            entrada.setMaterial(material);
	            entrada.setStockResultante(material.getCantidad());
	            mmservice.insertar(entrada);
	        }

	        mservice.borrar(id);

	        return ResponseEntity.ok(Map.of("mensaje", "Degüelle borrado correctamente"));

	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("No se puede deshacer el degüellé: " + e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al modificar el degüellé: " + e.getMessage());
	    }
	}
	
	
	private void verificarStockMateriales(DeguelleDto dto, int cantidadYaDescontada) {
	    List<MaterialCava> materiales = mcservice.findByCavaId(dto.getCavaId());
	    for (MaterialCava mat : materiales) {
	        Material material = mat.getMaterial();
	        int cantidadARestar = Math.round(material.getCantidadGastada() * dto.getCantidad());
	        int cantidadRealDisponible = material.getCantidad() + Math.round(material.getCantidadGastada() * cantidadYaDescontada);

	        if (cantidadRealDisponible < cantidadARestar) {
	            throw new IllegalArgumentException("No hay suficiente stock del material: " + material.getNombre());
	        }
	    }
	}
	
	private void verificarReversionPosible(Deguelle deg) {
	    Partida partida = deg.getPartida();
	    Cava cava = deg.getCava();
	    int cantidadAEliminar = deg.getCantidad();

	    // Buscar relación
	    Optional<CavaPartida> relacionOpt = cpservice.buscarPorCavaYPartida(cava.getId(), partida.getId());
	    if (relacionOpt.isEmpty()) return;

	    CavaPartida cp = relacionOpt.get();

	    // Calcular el total de botellas degüelladas para esa combinación
	    List<Deguelle> deguellesRelacionados = mservice.buscarPorCavaYPartida(cava.getId(), partida.getId());
	    int totalDeguellado = deguellesRelacionados.stream()
	        .mapToInt(Deguelle::getCantidad)
	        .sum();

	    int nuevoTotalDeguellado = totalDeguellado - cantidadAEliminar;

	    if (nuevoTotalDeguellado < cp.getVendido()) {
	        throw new IllegalArgumentException("No se puede eliminar este degüelle porque dejaría ventas sin cubrir");
	    }
	}

}
