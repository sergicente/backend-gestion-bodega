package cava.model.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cava.model.dto.CompraMaterialDto;
import cava.model.entity.CompraMaterial;
import cava.model.service.CompraMaterialService;

@RestController
@RequestMapping("/api/compra-material")
@CrossOrigin(origins = "*")
public class CompraMaterialController {

    @Autowired
    private CompraMaterialService cmservice;

    @Autowired
    private ModelMapper mapper;

    // Obtener todas las compras
    @GetMapping
    public ResponseEntity<?> obtenerTodas() {
        List<CompraMaterial> compras = cmservice.buscarTodos();
        List<CompraMaterialDto> dtos = new ArrayList<>();

        for (CompraMaterial c : compras) {
            CompraMaterialDto dto = new CompraMaterialDto();
            dto.setId(c.getId());
            dto.setCantidad(c.getCantidad());
            dto.setPrecioTotal(c.getPrecioTotal());
            dto.setDescripcion(c.getDescripcion());
            dto.setFecha(c.getFecha());
            dto.setMaterialId(c.getMaterial().getId());
            dto.setMaterialNombre(c.getMaterial().getNombre());
            dto.setMaterialCategoriaNombre(c.getMaterial().getCategoria().getNombre());
            dto.setProveedorId(c.getProveedor().getId());
            dto.setProveedorNombre(c.getProveedor().getNombre());
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
    }

    // Obtener una compra por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable Long id) {
        CompraMaterial c = cmservice.buscar(id);
        if (c == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la compra");
        } else {
            CompraMaterialDto dto = new CompraMaterialDto();
            dto.setId(c.getId());
            dto.setCantidad(c.getCantidad());
            dto.setPrecioTotal(c.getPrecioTotal());
            dto.setDescripcion(c.getDescripcion());
            dto.setFecha(c.getFecha());
            dto.setMaterialId(c.getMaterial().getId());
            dto.setMaterialNombre(c.getMaterial().getNombre());
            dto.setMaterialCategoriaNombre(c.getMaterial().getCategoria().getNombre());
            dto.setProveedorId(c.getProveedor().getId());
            dto.setProveedorNombre(c.getProveedor().getNombre());
            return ResponseEntity.ok(dto);
        }
    }

    // Insertar nueva compra
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody CompraMaterialDto dto) {
        if (dto.getId() != null && cmservice.buscar(dto.getId()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe una compra con el ID " + dto.getId());
        }

        CompraMaterial compra = mapper.map(dto, CompraMaterial.class);
        CompraMaterial nueva = cmservice.insertar(compra);
        CompraMaterialDto respuesta = mapper.map(nueva, CompraMaterialDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // Modificar compra existente
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody CompraMaterialDto dto) {
        if (!id.equals(dto.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID en la URL y en el cuerpo no coinciden");
        }

        CompraMaterial compra = mapper.map(dto, CompraMaterial.class);
        CompraMaterial actualizada = cmservice.modificar(compra);
        CompraMaterialDto respuesta = mapper.map(actualizada, CompraMaterialDto.class);
        return ResponseEntity.ok(respuesta);
    }

    // Borrar compra
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
        CompraMaterial existente = cmservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la compra");
        }
        cmservice.borrar(id);
        return ResponseEntity.noContent().build();
    }
}