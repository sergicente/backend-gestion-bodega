package cava.model.restcontroller;

import cava.model.dto.*;
import cava.model.entity.*;
import cava.model.service.PedidoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedido")
public class PedidoController {
	
	@Autowired
	private ModelMapper mapper;
    @Autowired
    private PedidoService pservice;
	
    // Obtener todas las partidas
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        List<Pedido> pedidos = pservice.buscarTodos();
        List<PedidoDto> listado = new ArrayList<>();

        for (Pedido p : pedidos) {
            listado.add(mapper.map(p, PedidoDto.class));
        }

        return ResponseEntity.ok(listado);
    }
    
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable long id) {
    	Pedido pedido = pservice.buscar(id);
    	if(pedido == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el pedido");
    	}else {
    		PedidoDto dto = mapper.map(pedido, PedidoDto.class);
    		return ResponseEntity.ok(dto);
    	}
    }
    
    
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody PedidoDto dto) {

        Pedido pedido = convertirDtoAPedido(dto);

        // Guardar
        Pedido guardado = pservice.insertar(pedido);

        // Convertir a DTO de respuesta
        PedidoDto nuevoDto = new PedidoDto();
        nuevoDto.setId(guardado.getId());
        nuevoDto.setCliente(guardado.getCliente());
        nuevoDto.setEstado(guardado.getEstado());
        nuevoDto.setUrgente(guardado.isUrgente());
        nuevoDto.setFechaLimite(guardado.getFechaLimite());
        nuevoDto.setFechaCreacion(guardado.getFechaCreacion());
        nuevoDto.setGls(guardado.isGls());
        nuevoDto.setNota1(guardado.getNota1());
        nuevoDto.setNota2(guardado.getNota2());
        nuevoDto.setLineas(convertirLineasAPedidoDto(guardado.getLineas()));
        nuevoDto.setTareas(convertirTareasAPedidoDto(guardado.getTareas()));
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }


    
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<PedidoDto> modificar(@PathVariable Long id, @RequestBody PedidoDto dto) {
        Pedido pedidoExistente = pservice.buscar(id);
        if (pedidoExistente == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualiza campos generales
        pedidoExistente.setCliente(dto.getCliente());
        pedidoExistente.setEstado(dto.getEstado());
        pedidoExistente.setFechaCreacion(dto.getFechaCreacion());
        pedidoExistente.setFechaLimite(dto.getFechaLimite());
        pedidoExistente.setUrgente(dto.isUrgente());
        pedidoExistente.setObservacionesGenerales(dto.getObservacionesGenerales());
        pedidoExistente.setNota1(dto.getNota1());
        pedidoExistente.setNota2(dto.getNota2());
        pedidoExistente.setGls(dto.isGls());

        // Elimina las líneas antiguas y añade las nuevas correctamente
        pedidoExistente.getLineas().clear();
        for (LineaPedidoDto lineaDto : dto.getLineas()) {
            LineaPedido lp = new LineaPedido();
            lp.setBotellas(lineaDto.getBotellas());
            lp.setCava(lineaDto.getCava());
            lp.setLote(lineaDto.getLote());
            lp.setNumeroPalet(lineaDto.getNumeroPalet());
            lp.setObservaciones(lineaDto.getObservaciones());
            lp.setPedido(pedidoExistente); // ⬅️ esto es CRUCIAL
            pedidoExistente.getLineas().add(lp);
        }

        // Elimina las tareas antiguas y añade las nuevas
        pedidoExistente.getTareas().clear();
        if (dto.getTareas() != null) {
            for (PedidoTareaDto tareaDto : dto.getTareas()) {
                PedidoTarea tarea = new PedidoTarea();
                tarea.setTexto(tareaDto.getTexto());
                tarea.setCompletado(tareaDto.isCompletado());
                pedidoExistente.getTareas().add(tarea);
            }
        }

        // Guarda
        Pedido actualizado = pservice.insertar(pedidoExistente);

        // Devuelve la respuesta
        return ResponseEntity.ok(convertirAPedidoDto(actualizado));
    }

    
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Pedido existente = pservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            pservice.borrar(id);
            return ResponseEntity.ok().body(Map.of("mensaje", "Pedido eliminado correctamente"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "No se puede eliminar el pedido porque está en uso"));
        }
    }



    private List<LineaPedidoDto> convertirLineasAPedidoDto(List<LineaPedido> lineas) {
        List<LineaPedidoDto> lista = new ArrayList<>();
        for (LineaPedido lp : lineas) {
            LineaPedidoDto dto = new LineaPedidoDto();
            dto.setId(lp.getId());
            dto.setCava(lp.getCava());
            dto.setBotellas(lp.getBotellas());
            dto.setObservaciones(lp.getObservaciones());
            dto.setLote(lp.getLote());
            dto.setNumeroPalet(lp.getNumeroPalet());
            lista.add(dto);
        }
        return lista;
    }

    private List<PedidoTareaDto> convertirTareasAPedidoDto(List<PedidoTarea> tareas) {
        List<PedidoTareaDto> lista = new ArrayList<>();
        for (PedidoTarea tarea : tareas) {
            PedidoTareaDto dto = new PedidoTareaDto();
            dto.setId(tarea.getId());
            dto.setTexto(tarea.getTexto());
            dto.setCompletado(tarea.isCompletado());
            lista.add(dto);
        }
        return lista;
    }

    private Pedido convertirDtoAPedido(PedidoDto dto) {
        Pedido pedido = new Pedido();
        pedido.setId(dto.getId());
        pedido.setCliente(dto.getCliente());
        pedido.setEstado(dto.getEstado());
        pedido.setFechaCreacion(dto.getFechaCreacion());
        pedido.setFechaLimite(dto.getFechaLimite());
        pedido.setUrgente(dto.isUrgente());
        pedido.setGls(dto.isGls());
        pedido.setNota1(dto.getNota1());
        pedido.setNota2(dto.getNota2());
        List<LineaPedido> lineas = new ArrayList<>();
        if (dto.getLineas() != null) {
            for (LineaPedidoDto lineaDto : dto.getLineas()) {
                LineaPedido linea = new LineaPedido();
                linea.setCava(lineaDto.getCava());
                linea.setBotellas(lineaDto.getBotellas());
                linea.setObservaciones(lineaDto.getObservaciones());
                linea.setLote(lineaDto.getLote());
                linea.setNumeroPalet(lineaDto.getNumeroPalet());
                linea.setPedido(pedido);
                lineas.add(linea);
            }
        }
        pedido.setLineas(lineas);
        List<PedidoTarea> tareas = new ArrayList<>();
        if (dto.getTareas() != null){
            for(PedidoTareaDto tareaDto : dto.getTareas()){
                PedidoTarea tarea = new PedidoTarea();
                tarea.setCompletado(tareaDto.isCompletado());
                tarea.setTexto(tareaDto.getTexto());
                tareas.add(tarea);
            }
        }
        pedido.setTareas(tareas);
        return pedido;
    }

    private PedidoDto convertirAPedidoDto(Pedido pedido) {
        PedidoDto dto = new PedidoDto();
        dto.setId(pedido.getId());
        dto.setCliente(pedido.getCliente());
        dto.setEstado(pedido.getEstado());
        dto.setFechaCreacion(pedido.getFechaCreacion());
        dto.setFechaLimite(pedido.getFechaLimite());
        dto.setObservacionesGenerales(pedido.getObservacionesGenerales());
        dto.setNota1(pedido.getNota1());
        dto.setNota2(pedido.getNota2());
        dto.setUrgente(pedido.isUrgente());
        dto.setGls(pedido.isGls());

        List<LineaPedidoDto> lineasDto = new ArrayList<>();
        for (LineaPedido lp : pedido.getLineas()) {
            LineaPedidoDto lpDto = new LineaPedidoDto();
            lpDto.setId(lp.getId());
            lpDto.setBotellas(lp.getBotellas());
            lpDto.setCava(lp.getCava());
            lpDto.setLote(lp.getLote());
            lpDto.setNumeroPalet(lp.getNumeroPalet());
            lpDto.setObservaciones(lp.getObservaciones());
            lineasDto.add(lpDto);
        }
        dto.setLineas(lineasDto);

        List<PedidoTareaDto> tareasDto = new ArrayList<>();
        if (pedido.getTareas() != null) {
            for (PedidoTarea tarea : pedido.getTareas()) {
                PedidoTareaDto tareaDto = new PedidoTareaDto();
                tareaDto.setId(tarea.getId());
                tareaDto.setTexto(tarea.getTexto());
                tareaDto.setCompletado(tarea.isCompletado());
                tareasDto.add(tareaDto);
            }
        }
        dto.setTareas(tareasDto);

        return dto;
    }

}
