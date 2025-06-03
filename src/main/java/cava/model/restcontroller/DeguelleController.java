package cava.model.restcontroller;

import java.time.LocalDate;
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

    
    
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
    	Deguelle existente = mservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el movimiento con ID " + id);
        }
        mservice.borrar(id);
        return ResponseEntity.noContent().build();
    }
    
    @Transactional
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody DeguelleDto dto) {
        Deguelle deguelle = mservice.buscar(id);
        if (deguelle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Degüelle no encontrado");
        }

        Partida partida = pservice.buscar(dto.getPartidaId());
        Cava cava = cservice.buscar(dto.getCavaId());
        
        int cantidadMaterialOriginal = deguelle.getCantidad();
        int cantidadMaterialNueva = dto.getCantidad();

        Optional<CavaPartida> optionalRelacion = cpservice.buscarPorCavaYPartida(dto.getCavaId(), dto.getPartidaId());
        if (optionalRelacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Relación Cava-Partida no encontrada");
        }

        CavaPartida relacion = optionalRelacion.get();

        int cantidadAnterior = deguelle.getCantidad();
        int cantidadNueva = dto.getCantidad();
        int diferencia = cantidadNueva - cantidadAnterior;

        int cantidadVendida = relacion.getVendido();
        if (cantidadNueva < cantidadVendida) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("No puedes reducir el degüelle por debajo de las botellas ya vendidas (" + cantidadVendida + ")");
        }

        // Verificar que haya suficientes botellas en rima para cubrir la diferencia
        int nuevaCantidadRima = partida.getBotellasRima() - diferencia;
        if (nuevaCantidadRima < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("No hay suficientes botellas en rima para esta operación");
        }

        // Actualizar partida
        partida.setBotellasRima(nuevaCantidadRima);
        partida.setBotellasStock(partida.getBotellasStock() + diferencia);
        pservice.insertar(partida);

     // Actualizar relación
        int stockRecalculado = cantidadNueva - cantidadVendida;
        if (stockRecalculado < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Stock negativo en la relación después de la modificación");
        }
        relacion.setCantidad(stockRecalculado);  // cantidad = stock actual (degüelladas - vendidas)
        cpservice.insertar(relacion);

        // Actualizar degüelle
        deguelle.setCantidad(cantidadNueva);
        deguelle.setDescripcion(dto.getDescripcion());
        deguelle.setFecha(dto.getFecha());
        deguelle.setLot(dto.getLot());
        deguelle.setEstadoAnterior(dto.getEstadoAnterior());
        deguelle.setEstadoNuevo(dto.getEstadoNuevo());
        deguelle.setPartida(partida);
        deguelle.setCava(cava);

        Deguelle actualizado = mservice.modificar(deguelle);
        
        
        
        
        // Restar materiales
        
        List<MaterialCava> lista = mcservice.findByCavaId(dto.getCavaId());
        for(MaterialCava mat : lista) {
            Material material = mat.getMaterial();

           

            int cantidadARestaurar = Math.round(material.getCantidadGastada() * cantidadMaterialOriginal);
            int cantidadANuevaSalida = Math.round(material.getCantidadGastada() * cantidadMaterialNueva);

            int diferenciaMaterial = cantidadARestaurar - cantidadANuevaSalida;
            int nuevoStock = material.getCantidad() + diferenciaMaterial;

            if(nuevoStock < 0 ) {
                throw new IllegalArgumentException("No hay suficiente stock del material: " + material.getNombre());
            }

            material.setCantidad(nuevoStock);
            matservice.insertar(material);

            if (diferenciaMaterial != 0) {
                MovimientoMaterial mov = new MovimientoMaterial();
                mov.setFecha(dto.getFecha());
                mov.setTipo(diferenciaMaterial > 0 ? TipoMovimientoMaterial.ENTRADA : TipoMovimientoMaterial.SALIDA);
                mov.setDescripcion("Ajuste por modificación de degüelle (" + deguelle.getLot() + ")");
                mov.setCantidad(Math.abs(diferenciaMaterial));
                mov.setMaterial(material);
                mov.setStockResultante(nuevoStock);
                mmservice.insertar(mov);
            }
        }
        
        

        DeguelleDto respuesta = new DeguelleDto();
        respuesta.setId(actualizado.getId());
        respuesta.setCantidad(actualizado.getCantidad());
        respuesta.setDescripcion(actualizado.getDescripcion());
        respuesta.setFecha(actualizado.getFecha());
        respuesta.setLot(actualizado.getLot());
        respuesta.setPartidaId(partida.getId());
        respuesta.setCavaId(cava.getId());
        respuesta.setEstadoAnterior(actualizado.getEstadoAnterior());
        respuesta.setEstadoNuevo(actualizado.getEstadoNuevo());

        return ResponseEntity.ok(respuesta);
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
    

}
