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
import jakarta.transaction.Transactional;

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
	@Transactional
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
        
        if (dto.getPrecioUnitario() <= 0) {
            return ResponseEntity.badRequest().body("El precio unitario debe ser mayor que 0");
        }

        CompraMaterial compra = mapper.map(dto, CompraMaterial.class);
        compra.setProveedor(proveedor);
        compra.setMaterial(material);
        compra.setPrecioUnitario(dto.getPrecioTotal() / dto.getCantidad());

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
        material.setPrecioActual((float) dto.getPrecioUnitario());
        mservice.modificar(material);
        
        
        CompraMaterialDto respuesta = mapper.map(nueva, CompraMaterialDto.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

	@PutMapping("/modificar/{id}")
	@Transactional
	public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody CompraMaterialDto dto) {
	    if (!id.equals(dto.getId())) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("El ID en la URL y en el cuerpo no coinciden");
	    }

	    CompraMaterial existente = cmservice.buscar(id);
	    if (existente == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("No se encontró ninguna compra con ID: " + id);
	    }

	    if (dto.getProveedorId() == null || dto.getMaterialId() == null) {
	        return ResponseEntity.badRequest().body("Proveedor y material son obligatorios");
	    }

	    Proveedor proveedor = pservice.buscar(dto.getProveedorId());
	    if (proveedor == null) {
	        return ResponseEntity.badRequest().body("Proveedor no encontrado con ID: " + dto.getProveedorId());
	    }

	    Material material = mservice.buscar(dto.getMaterialId());
	    if (material == null) {
	        return ResponseEntity.badRequest().body("Material no encontrado con ID: " + dto.getMaterialId());
	    }

	    // 1. Ajustar stock (diferencia entre cantidades)
	    int cantidadOriginal = existente.getCantidad();
	    int nuevaCantidad = dto.getCantidad();
	    int diferencia = nuevaCantidad - cantidadOriginal;

	    if (material.getCantidad() + diferencia < 0) {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body("No se puede modificar la compra porque se ha consumido parte del material.");
	    }

	    material.setCantidad(material.getCantidad() + diferencia);
	    float nuevoPrecioUnitario = (float) dto.getPrecioTotal() / dto.getCantidad();
	    material.setPrecioActual(nuevoPrecioUnitario);
	    mservice.modificar(material);

	    // 2. Modificar compra
	    CompraMaterial compra = mapper.map(dto, CompraMaterial.class);
	    compra.setProveedor(proveedor);
	    compra.setMaterial(material);
	    compra.setPrecioUnitario(nuevoPrecioUnitario);
	    CompraMaterial actualizada = cmservice.modificar(compra);

	    // 3. Modificar movimiento asociado
	    MovimientoMaterial movimiento = mmservice.findByCompraMaterialId(id);
	    movimiento.setCantidad(nuevaCantidad);
	    movimiento.setDescripcion(dto.getDescripcion());
	    movimiento.setFecha(dto.getFecha());
	    movimiento.setMaterial(material);
	    movimiento.setTipo(TipoMovimientoMaterial.ENTRADA);
	    movimiento.setStockResultante(material.getCantidad());
	    movimiento.setCompraMaterial(actualizada);
	    mmservice.modificar(movimiento);

	    CompraMaterialDto respuesta = mapper.map(actualizada, CompraMaterialDto.class);
	    return ResponseEntity.ok(respuesta);
	}

    @Transactional
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
        CompraMaterial existente = cmservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la compra");
        }
        
        Material material = existente.getMaterial();
        MovimientoMaterial movimiento = mmservice.findByCompraMaterialId(id);

        int cantidadCompra = existente.getCantidad();
        int stockActual = material.getCantidad();

        if (stockActual < cantidadCompra) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("No se puede borrar la compra porque ya se ha consumido parte del material.");
        }
        

     

        // Restar cantidad
        material.setCantidad(stockActual - cantidadCompra);
        mservice.modificar(material); // Asegúrate de guardar el cambio en el stock

        // Borrar movimiento y compra
        mmservice.borrar(movimiento.getId());
        cmservice.borrar(id);
        
     // Después de borrar la compra:
        List<CompraMaterial> compras = cmservice.findByMaterialIdOrderByFechaDesc(material.getId());

        if (!compras.isEmpty()) {
            double nuevoPrecio = compras.get(0).getPrecioUnitario();
            material.setPrecioActual((float)nuevoPrecio);
        } else {
            // No hay compras → precio a 0 o nulo
            material.setPrecioActual(0);
        }

        mservice.modificar(material);
        

        return ResponseEntity.noContent().build();
    }
}