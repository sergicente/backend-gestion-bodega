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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cava.model.dto.VentaDto;
import cava.model.entity.Venta;
import cava.model.service.VentaService;


@RestController
@RequestMapping("/api/venta")
@CrossOrigin(origins = "*")
public class VentaController {
	
	@Autowired
	private VentaService vservice;
	@Autowired
	private ModelMapper mapper;
	
    // Obtener todas las partidas
    @GetMapping
    public ResponseEntity<?> obtenerTodas() {
        List<Venta> ventas = vservice.buscarTodos();
        List<VentaDto> dtos = new ArrayList<>();

        for (Venta v : ventas) {
        	VentaDto dto = mapper.map(v, VentaDto.class);
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
    }
    
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable String id) {
        Venta venta = vservice.buscar(id);
        if (venta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
        } else {
        	VentaDto dto = mapper.map(venta, VentaDto.class);
            return ResponseEntity.ok(dto);
        }
    }
    
    
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody VentaDto dto) {
        if (dto.getId() != null && vservice.buscar(dto.getId()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: Ya existe una venta con el ID " + dto.getId());
        }

        Venta venta = mapper.map(dto, Venta.class);
        Venta nuevaVenta = vservice.insertar(venta);
        VentaDto respuesta = mapper.map(nuevaVenta, VentaDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
    
    
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable String id, @RequestBody VentaDto dto) {
        if (!id.equals(dto.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID en la URL y en el cuerpo no coinciden");
        }

        Venta venta = mapper.map(dto, Venta.class);
        Venta actualizada = vservice.modificar(venta);
        VentaDto respuesta = mapper.map(actualizada, VentaDto.class);
        return ResponseEntity.ok(respuesta);
    }
    
    
	
	// Borrar una venta
	@DeleteMapping("/borrar/{id}")
	public ResponseEntity<?> borrar(@PathVariable String id) {
		Venta existente = vservice.buscar(id);
		if(existente == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
		}
		vservice.borrar(id);
		return ResponseEntity.noContent().build();
	}
	
}
