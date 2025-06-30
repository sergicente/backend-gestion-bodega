package cava.model.restcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cava.model.entity.*;
import cava.model.service.*;
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
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/deguelle")
public class DeguelleController {

	@Autowired
	private DeguelleService dservice;
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
	private IncidenciaService iservice;
	@Autowired
	private ModelMapper mapper;

	// Obtener todas las partidas
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
		List<Deguelle> lista = dservice.buscarTodos();
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
		Deguelle movimiento = dservice.buscar(id);

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

	    // Validar stock en rima
	    int nuevaCantidadRima = partida.getBotellasRima() - dto.getCantidad() -dto.getMerma();
	    if (nuevaCantidadRima < 0) {
	        throw new IllegalArgumentException("No hay suficientes botellas en rima");
	    }

		// Validación del lote
		if (dto.getLot() == null || dto.getLot().trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El lote no puede estar vacío");
		}

		if (dservice.existsByLotIgnoreCase(dto.getLot())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Aquest lot ja existeix");
		}

	    partida.setBotellasRima(nuevaCantidadRima);
	    partida.setBotellasStock(partida.getBotellasStock() + dto.getCantidad());
	    partida.setBotellasMerma(partida.getBotellasMerma()+ dto.getMerma());


	    // Lista para acumular movimientos creados
	    List<MovimientoMaterial> movimientosCreados = new ArrayList<>();

	    List<MaterialCava> lista = mcservice.findByCavaId(dto.getCavaId());
	    for (MaterialCava mat : lista) {
	        Material material = mat.getMaterial();
	        int cantidadARestar = Math.round(mat.getMaterial().getCantidadGastada() * dto.getCantidad());
	        int nuevoStock = material.getCantidad() - cantidadARestar;

	        material.setCantidad(nuevoStock);
	        matservice.insertar(material);

	        MovimientoMaterial mov = new MovimientoMaterial();
	        mov.setFecha(dto.getFecha());
	        mov.setTipo(TipoMovimientoMaterial.SALIDA);
	        mov.setDescripcion("Desgorg " + dto.getCantidad() + " ampolles de " + cava.getNombre() + " (" + dto.getLot() + ")");
	        mov.setCantidad(cantidadARestar);
	        mov.setMaterial(material);
	        mov.setStockResultante(nuevoStock);

	        mov = mmservice.insertar(mov); // guardar y obtener con ID
	        movimientosCreados.add(mov);
	    }

	    pservice.insertar(partida);

	    // Ahora insertamos el degüelle
	    Deguelle deguelle = new Deguelle();
	    deguelle.setPartida(partida);
	    deguelle.setCava(cava);
	    deguelle.setFecha(dto.getFecha());
	    deguelle.setCantidad(dto.getCantidad());
	    deguelle.setMerma(dto.getMerma());
	    deguelle.setLot(dto.getLot());
		deguelle.setLotTap(dto.getLotTap());
	    deguelle.setLimpieza(dto.isLimpieza());
	    deguelle.setObservaciones(dto.getObservaciones());
	    deguelle.setLicor(dto.getLicor());
	    deguelle = dservice.insertar(deguelle);

		if (dto.getMerma() > 0) {
			Incidencia merma = new Incidencia();
			merma.setCantidad(dto.getMerma());
			merma.setCava(cava);
			merma.setPartida(partida);
			merma.setTipo(TipoIncidencia.DEGÜELLE);
			merma.setFecha(dto.getFecha());
			merma.setDetalles("Merma del desgorg " + dto.getLot());
			merma.setDeguelle(deguelle);
			iservice.insertar(merma);
		}

	    // Ahora actualizamos los movimientos con el degüelle creado
	    for (MovimientoMaterial mov : movimientosCreados) {
	        mov.setDeguelle(deguelle);
	        mmservice.insertar(mov); // usar save() que haga merge/update
	    }

	    DeguelleDto deguelleDto = new DeguelleDto();
	    deguelleDto.setId(deguelle.getId());
	    deguelleDto.setFecha(deguelle.getFecha());
		deguelleDto.setLot(deguelle.getLot());
		deguelleDto.setLotTap(deguelle.getLotTap());
	    deguelleDto.setCantidad(deguelle.getCantidad());
		deguelleDto.setMerma(deguelle.getMerma());
		deguelleDto.setLimpieza(deguelle.isLimpieza());
		deguelleDto.setObservaciones(deguelle.getObservaciones());
		deguelleDto.setLicor(deguelle.getLicor());
	    deguelleDto.setPartidaId(partida.getId());
		deguelleDto.setCavaId(deguelle.getCava().getId());
		deguelleDto.setCavaNombre(deguelle.getCava().getNombre());
		deguelleDto.setCavaFamiliaNombre(deguelle.getCava().getFamilia().getNombre());
	    return ResponseEntity.status(HttpStatus.CREATED).body(deguelleDto);
	}


@Transactional
@PutMapping("/modificar/{id}")
public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody DeguelleDto dto) {
	Deguelle existente = dservice.buscar(id);
	if (existente == null) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Degüelle no encontrado");
	}

	// Validación del lote
	if (dto.getLot() == null || dto.getLot().trim().isEmpty()) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El lote no puede estar vacío");
	}

	Deguelle otro = dservice.findByLotIgnoreCase(dto.getLot());
	if (otro != null && !otro.getId().equals(id)) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body("Aquest lot ja existeix");
	}

	Partida partidaAnterior = existente.getPartida();
	Cava cavaAnterior = existente.getCava();

	Partida nuevaPartida = pservice.buscar(dto.getPartidaId());
	Cava nuevaCava = cservice.buscar(dto.getCavaId());

	if (nuevaPartida == null || nuevaCava == null) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cava o Partida no encontrada");
	}

	// Validar que se puede revertir sin afectar ventas
	verificarReversionPosibleEnCavaPartidaAnterior(existente);
	verificarReversionPosible(existente, dto.getCantidad());

	// Revertir stock anterior
	partidaAnterior.setBotellasRima(partidaAnterior.getBotellasRima() + existente.getCantidad() + existente.getMerma());
	partidaAnterior.setBotellasStock(partidaAnterior.getBotellasStock() - existente.getCantidad());
	partidaAnterior.setBotellasMerma(partidaAnterior.getBotellasMerma() - existente.getMerma());
	pservice.modificar(partidaAnterior);

	// Revertir cantidad en la CavaPartida anterior
	Optional<CavaPartida> cpAnteriorOptional = cpservice.buscarPorCavaYPartida(cavaAnterior.getId(), partidaAnterior.getId());
	if (cpAnteriorOptional.isPresent()) {
	    CavaPartida cpAnterior = cpAnteriorOptional.get();
	    cpAnterior.setCantidad(cpAnterior.getCantidad() - existente.getCantidad());
	    cpservice.modificar(cpAnterior);
	}

	// Validar stock suficiente en la nueva partida
	int rimaDisponible = nuevaPartida.getBotellasRima();
	int totalNecesario = dto.getCantidad() + dto.getMerma();
	if (rimaDisponible < totalNecesario) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay suficientes botellas en rima en la nueva partida");
	}

	// Validar que hay materiales suficientes, compensando los ya descontados
//	verificarStockMateriales(dto, existente.getCantidad());

	// Aplicar nuevos cambios
	nuevaPartida.setBotellasRima(rimaDisponible - totalNecesario);
	nuevaPartida.setBotellasStock(nuevaPartida.getBotellasStock() + dto.getCantidad());
	nuevaPartida.setBotellasMerma(nuevaPartida.getBotellasMerma() + dto.getMerma());
	pservice.modificar(nuevaPartida);

	// Actualizar cantidad en CavaPartida
	Optional<CavaPartida> cpOptional = cpservice.buscarPorCavaYPartida(nuevaCava.getId(), nuevaPartida.getId());
	if (cpOptional.isPresent()) {
	    CavaPartida cp = cpOptional.get();
	    cp.setCantidad(cp.getCantidad() + dto.getCantidad());
	    cpservice.modificar(cp);
	}

	existente.setPartida(nuevaPartida);
	existente.setCava(nuevaCava);
	existente.setFecha(dto.getFecha());
	existente.setCantidad(dto.getCantidad());
	existente.setMerma(dto.getMerma());
	existente.setLot(dto.getLot());

	// Revertir materiales anteriores
	List<MovimientoMaterial> movimientosAnteriores = mmservice.findByDeguelleId(existente.getId());
	for (MovimientoMaterial mov : movimientosAnteriores) {
	    Material mat = mov.getMaterial();
	    mat.setCantidad(mat.getCantidad() + mov.getCantidad());
	    matservice.insertar(mat);
	    mmservice.borrar(mov.getId());
	}

	// Insertar nuevos movimientos para materiales actuales
	List<MaterialCava> materialesActuales = mcservice.findByCavaId(dto.getCavaId());
	List<MovimientoMaterial> nuevosMovimientos = new ArrayList<>();
	for (MaterialCava matCava : materialesActuales) {
	    Material material = matCava.getMaterial();
	    int cantidadGastada = Math.round(material.getCantidadGastada() * dto.getCantidad());
	    int nuevoStock = material.getCantidad() - cantidadGastada;
	    material.setCantidad(nuevoStock);
	    matservice.insertar(material);

	    MovimientoMaterial nuevoMov = new MovimientoMaterial();
	    nuevoMov.setFecha(dto.getFecha());
	    nuevoMov.setTipo(TipoMovimientoMaterial.SALIDA);
	    nuevoMov.setDescripcion("Actualització desgorg " + dto.getCantidad() + " ampolles de " + nuevaCava.getNombre() + " (" + dto.getLot() + ")");
	    nuevoMov.setCantidad(cantidadGastada);
	    nuevoMov.setMaterial(material);
	    nuevoMov.setStockResultante(nuevoStock);
	    nuevoMov.setDeguelle(existente);
	    mmservice.insertar(nuevoMov);
	}
	existente.setLimpieza(dto.isLimpieza());
	existente.setObservaciones(dto.getObservaciones());
	existente.setLicor(dto.getLicor());
	dservice.modificar(existente);

	// Gestionar incidencia de merma
	Incidencia incidencia = iservice.findByDeguelleId(id);
	if (dto.getMerma() > 0) {
		if (incidencia == null) {
			incidencia = new Incidencia();
			incidencia.setDeguelle(existente);
			incidencia.setTipo(TipoIncidencia.DEGÜELLE);
		}
		incidencia.setCantidad(dto.getMerma());
		incidencia.setFecha(dto.getFecha());
		incidencia.setDetalles("Merma del desgorg " + dto.getLot());
		incidencia.setPartida(nuevaPartida);
		incidencia.setCava(nuevaCava);
		iservice.insertar(incidencia);
	} else if (incidencia != null) {
		iservice.borrar(incidencia.getId());
	}

	return ResponseEntity.ok(Map.of("mensaje", "Degüelle modificado correctamente"));

}

    private void verificarReversionPosibleEnCavaPartidaAnterior(Deguelle deg) {
        Partida partida = deg.getPartida();
        Cava cava = deg.getCava();

        Optional<CavaPartida> relacionOpt = cpservice.buscarPorCavaYPartida(cava.getId(), partida.getId());
        if (relacionOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe relación entre la cava y la partida");
        }

        CavaPartida cp = relacionOpt.get();
        List<Deguelle> deguellesRelacionados = dservice.buscarPorCavaYPartida(cava.getId(), partida.getId());

        int totalDeguellado = deguellesRelacionados.stream()
            .mapToInt(d -> d.getCantidad() + d.getMerma())
            .sum();

        int nuevoTotalDeguellado = totalDeguellado - deg.getCantidad() - deg.getMerma();

        if (nuevoTotalDeguellado < cp.getVendido()) {
            throw new IllegalArgumentException(
                "No se puede cambiar el degüelle de cava: hay " + cp.getVendido() +
                " botellas vendidas de " + cava.getNombre() + " (" + partida.getId() + "), pero quedarían solo " +
                nuevoTotalDeguellado + " degüelladas.");
        }
    }

	@Transactional
	@DeleteMapping("/borrar/{id}")
	public ResponseEntity<?> borrar(@PathVariable Long id) {
	    Deguelle deguelle = dservice.buscar(id);
	    if (deguelle == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No s'ha trobat el desgorg");
	    }

	    try {
	        verificarReversionPosible(deguelle, -1);

	        // 1. DESHACER DEGUELLE ANTERIOR
	        Partida partidaAnterior = deguelle.getPartida();
	        Cava cavaAnterior = deguelle.getCava();
	        int cantidadAnterior = deguelle.getCantidad();
	        int mermaAnterior = deguelle.getMerma();

	        partidaAnterior.setBotellasRima(partidaAnterior.getBotellasRima() + cantidadAnterior + mermaAnterior);
	        partidaAnterior.setBotellasStock(partidaAnterior.getBotellasStock() - cantidadAnterior);
	        partidaAnterior.setBotellasMerma(partidaAnterior.getBotellasMerma() - mermaAnterior);

	        pservice.insertar(partidaAnterior);

	        Optional<CavaPartida> relacionAnterior = cpservice.buscarPorCavaYPartida(cavaAnterior.getId(), partidaAnterior.getId());
	        if (relacionAnterior.isPresent()) {
	            CavaPartida relacion = relacionAnterior.get();
	            relacion.setCantidad(relacion.getCantidad() - cantidadAnterior);
	            cpservice.insertar(relacion);
	        }

	        List<MovimientoMaterial> movimientos = mmservice.findByDeguelleId(deguelle.getId());
	        for (MovimientoMaterial mov : movimientos) {
	            Material material = mov.getMaterial();

	            // Revertir stock
	            material.setCantidad(material.getCantidad() + mov.getCantidad());
	            matservice.insertar(material);

	            // Borrar el movimiento
	            mmservice.borrar(mov.getId());
	        }

			// Borrar mermas
			Incidencia merma = iservice.findByDeguelleId(deguelle.getId());
			if (merma != null) {
				iservice.borrar(merma.getId());
			}

	        dservice.borrar(id);

	        return ResponseEntity.ok(Map.of("mensaje", "Degüelle borrado correctamente"));

	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("No es pot modificar el desgorg: " + e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("No es pot modificar el desgorg: " + e.getMessage());
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

	private void verificarReversionPosible(Deguelle deg, int nuevaCantidad) {
	    Partida partida = deg.getPartida();
	    Cava cava = deg.getCava();
	    int cantidadAEliminar = deg.getCantidad() + deg.getMerma();

	    // Buscar relación
	    Optional<CavaPartida> relacionOpt = cpservice.buscarPorCavaYPartida(cava.getId(), partida.getId());
	    if (relacionOpt.isEmpty()) {
	        throw new IllegalArgumentException("No existe relación entre la cava y la partida");
	    }

	    CavaPartida cp = relacionOpt.get();

	    // Calcular el total de botellas degüelladas para esa combinación (incluyendo mermas)
	    List<Deguelle> deguellesRelacionados = dservice.buscarPorCavaYPartida(cava.getId(), partida.getId());
	    int totalDeguellado = deguellesRelacionados.stream()
	        .mapToInt(d -> d.getCantidad() + d.getMerma())
	        .sum();

	    int nuevoTotalDeguellado;
	    if (nuevaCantidad == -1) {
	        // Caso de eliminación completa
	        nuevoTotalDeguellado = totalDeguellado - deg.getCantidad() - deg.getMerma();
	    } else {
	        // Caso de modificación
	        nuevoTotalDeguellado = totalDeguellado - deg.getCantidad() - deg.getMerma() + nuevaCantidad;
	    }

	    if (nuevoTotalDeguellado < cp.getVendido()) {
	        throw new IllegalArgumentException("Hi ha " + cp.getVendido() + " ampolles venudes, però quedarien sols " + nuevoTotalDeguellado + " desgorjades.");
	    }
	}

}
