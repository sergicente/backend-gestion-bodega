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

import cava.model.dto.ProveedorDto;
import cava.model.dto.VentaDto;
import cava.model.entity.Proveedor;
import cava.model.entity.Venta;
import cava.model.service.ProveedorService;
import cava.model.service.VentaService;


@RestController
@RequestMapping("/api/proveedor")
@CrossOrigin(origins = "*")
public class ProveedorController {
	
	@Autowired
	private ProveedorService pservice;
	@Autowired
	private ModelMapper mapper;
	
	// Obtener todos los proveedores
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
	    List<Proveedor> proveedores = pservice.buscarTodos();
	    List<ProveedorDto> dtos = new ArrayList<>();

	    for (Proveedor p : proveedores) {
	        ProveedorDto dto = mapper.map(p, ProveedorDto.class);
	        dtos.add(dto);
	    }

	    return ResponseEntity.ok(dtos);
	}

	// Obtener un proveedor por ID
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerUno(@PathVariable Long id) {
	    Proveedor p = pservice.buscar(id);
	    if (p == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el proveedor");
	    } else {
	        ProveedorDto dto = mapper.map(p, ProveedorDto.class);
	        return ResponseEntity.ok(dto);
	    }
	}

	// Insertar nuevo proveedor
	@PostMapping("/insertar")
	public ResponseEntity<?> insertarUno(@RequestBody ProveedorDto dto) {
	    if (dto.getProveedorId() != null && pservice.buscar(dto.getProveedorId()) != null) {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body("Ya existe un proveedor con el ID " + dto.getProveedorId());
	    }

	    Proveedor proveedor = mapper.map(dto, Proveedor.class);
	    Proveedor nuevo = pservice.insertar(proveedor);
	    ProveedorDto respuesta = mapper.map(nuevo, ProveedorDto.class);
	    return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	// Modificar proveedor existente
	@PutMapping("/modificar/{id}")
	public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody ProveedorDto dto) {
	    if (!id.equals(dto.getProveedorId())) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID en la URL y en el cuerpo no coinciden");
	    }

	    Proveedor proveedor = mapper.map(dto, Proveedor.class);
	    Proveedor actualizado = pservice.modificar(proveedor);
	    ProveedorDto respuesta = mapper.map(actualizado, ProveedorDto.class);
	    return ResponseEntity.ok(respuesta);
	}

	// Borrar proveedor
	@DeleteMapping("/borrar/{id}")
	public ResponseEntity<?> borrar(@PathVariable Long id) {
	    Proveedor existente = pservice.buscar(id);
	    if (existente == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el proveedor");
	    }
	    pservice.borrar(id);
	    return ResponseEntity.noContent().build();
	}
	
}
