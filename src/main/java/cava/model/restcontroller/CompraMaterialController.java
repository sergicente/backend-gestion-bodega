package cava.model.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cava.model.dto.CompraMaterialDto;
import cava.model.dto.MovimientoMaterialDto;
import cava.model.entity.CompraMaterial;
import cava.model.entity.Material;
import cava.model.entity.MovimientoMaterial;
import cava.model.entity.Proveedor;
import cava.model.entity.TipoMovimientoMaterial;
import cava.model.service.CompraMaterialService;
import cava.model.service.MaterialService;
import cava.model.service.MovimientoMaterialService;
import cava.model.service.ProveedorService;

@RestController
@RequestMapping("/api/compra-material")
@CrossOrigin(origins = "*")
public class CompraMaterialController {

    @Autowired
    private CompraMaterialService cmservice;
    
    @Autowired
    private ProveedorService pservice;
    
    @Autowired
    private MaterialService mservice;
    
    @Autowired
    private MovimientoMaterialService mmservice;

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
    
    
    // Obtener un movimiento
    @GetMapping("/articulo/{id}")
    public ResponseEntity<?> obtenerMovimientosDeUnArticulo(@PathVariable Long id) {
        List<CompraMaterial> movimientos = cmservice.findByMaterialId(id);

        List<CompraMaterialDto> dtos = new ArrayList<>();
        for (CompraMaterial m : movimientos) {
        	CompraMaterialDto dto = mapper.map(m, CompraMaterialDto.class);
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
    }

 // Insertar nueva compra
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody CompraMaterialDto dto) {
        if (dto.getId() != null && cmservice.buscar(dto.getId()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe una compra con el ID " + dto.getId());
        }

        if (dto.getProveedorId() == null) {
            return ResponseEntity.badRequest().body("Debe especificarse el ID del proveedor");
        }

        if (dto.getMaterialId() == null) {
            return ResponseEntity.badRequest().body("Debe especificarse el ID del material");
        }

        Proveedor proveedor = pservice.buscar(dto.getProveedorId());
        if (proveedor == null) {
            return ResponseEntity.badRequest().body("Proveedor no encontrado con ID: " + dto.getProveedorId());
        }

        Material material = mservice.buscar(dto.getMaterialId());
        if (material == null) {
            return ResponseEntity.badRequest().body("Material no encontrado con ID: " + dto.getMaterialId());
        }

        CompraMaterial compra = mapper.map(dto, CompraMaterial.class);
        compra.setProveedor(proveedor);
        compra.setMaterial(material);

        CompraMaterial nueva = cmservice.insertar(compra);
        
        MovimientoMaterial movimiento = new MovimientoMaterial();
        movimiento.setCantidad(compra.getCantidad());
        movimiento.setDescripcion(compra.getDescripcion());
        movimiento.setFecha(compra.getFecha());
        movimiento.setMaterial(compra.getMaterial());
        movimiento.setTipo(TipoMovimientoMaterial.ENTRADA);
        movimiento.setStockResultante(material.getCantidad() + compra.getCantidad());
        movimiento.setCompraMaterial(compra);
        mmservice.insertar(movimiento);
        
        // Actualizar stock del material
        material.setCantidad(material.getCantidad() + compra.getCantidad());
        mservice.modificar(material);
        
        
        CompraMaterialDto respuesta = mapper.map(nueva, CompraMaterialDto.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

 // Modificar compra existente
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody CompraMaterialDto dto) {
        if (!id.equals(dto.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El ID en la URL y en el cuerpo no coinciden");
        }

        // Validar existencia de la compra
        if (cmservice.buscar(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ninguna compra con ID: " + id);
        }

        if (dto.getProveedorId() == null) {
            return ResponseEntity.badRequest().body("Debe especificarse el ID del proveedor");
        }

        if (dto.getMaterialId() == null) {
            return ResponseEntity.badRequest().body("Debe especificarse el ID del material");
        }

        Proveedor proveedor = pservice.buscar(dto.getProveedorId());
        if (proveedor == null) {
            return ResponseEntity.badRequest().body("Proveedor no encontrado con ID: " + dto.getProveedorId());
        }

        Material material = mservice.buscar(dto.getMaterialId());
        if (material == null) {
            return ResponseEntity.badRequest().body("Material no encontrado con ID: " + dto.getMaterialId());
        }

        CompraMaterial compra = mapper.map(dto, CompraMaterial.class);
        compra.setProveedor(proveedor);
        compra.setMaterial(material);

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