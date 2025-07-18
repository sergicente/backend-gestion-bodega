package cava.model.restcontroller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cava.model.entity.*;
import cava.model.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cava.model.dto.MaterialDto;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/material")
public class MaterialController {
	
	@Autowired
	private MaterialService mservice;
	@Autowired
	private FamiliaService fservice;
	@Autowired
	private CategoriaService catservice;
	@Autowired
	private ModelMapper mapper;
    @Autowired
    private CompraMaterialService cmservice;
    @Autowired
    private ProveedorService pservice;
    @Autowired
    private MovimientoMaterialService mmservice;
    // Obtener todas las partidas
	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
	    List<Material> materiales = mservice.buscarTodos();
	    List<MaterialDto> dtos = new ArrayList<>();

	    for (Material m : materiales) {
	        MaterialDto dto = mapper.map(m, MaterialDto.class);
	        dtos.add(dto);
	    }

	    return ResponseEntity.ok(dtos);
	}

    @GetMapping("/proveedor/{id}")
    public ResponseEntity<List<MaterialDto>> obtenerMaterialesPorProveedor(@PathVariable Long id) {
        List<Material> materiales = cmservice.obtenerMaterialesPorProveedor(id);
        List<MaterialDto> dtos = materiales.stream()
                .map(material -> mapper.map(material, MaterialDto.class))
                .toList();
        return ResponseEntity.ok(dtos);
    }
    
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerUno(@PathVariable long id) {
	    Material material = mservice.buscar(id);
	    
	    if (material == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el material");
	    } else {
	        MaterialDto dto = mapper.map(material, MaterialDto.class);
	        return ResponseEntity.ok(dto);
	    }
	}
    
    
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody MaterialDto materialDto) {

        // Buscar categoria
        Categoria categoria = catservice.buscar(materialDto.getCategoriaId());
        if (categoria == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria no encontrada");
        }
    	
        // Buscar familia
        Familia familia = fservice.buscar(materialDto.getFamiliaId());
        if (familia == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Familia no encontrada");
        }

        
        
        // Crear material
        Material material = mapper.map(materialDto, Material.class);

        // Guardar
        Material nuevoMaterial = mservice.insertar(material);

        // Convertir a DTO
        MaterialDto nuevoDto = mapper.map(nuevoMaterial, MaterialDto.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }
    
    
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody MaterialDto materialDto) {
        try {
            if (!id.equals(materialDto.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID en la URL y en el body no coinciden");
            }

            Material existente = mservice.buscar(id);
            if (existente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Material no encontrado con ID " + id);
            }

            Familia familia = fservice.buscar(materialDto.getFamiliaId());
            if (familia == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Familia no encontrada");
            }
            
            // Buscar familia
            Categoria categoria = catservice.buscar(materialDto.getCategoriaId());
            if (categoria == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria no encontrada");
            }

            // Modificar directamente
            existente.setNombre(materialDto.getNombre());
            existente.setCategoria(categoria);
            existente.setFamilia(familia);
            existente.setObservaciones(materialDto.getObservaciones());
            existente.setCantidad(materialDto.getCantidad());
            existente.setCantidadMinima(materialDto.getCantidadMinima());
            existente.setCantidadGastada(materialDto.getCantidadGastada());

            Material actualizado = mservice.modificar(existente);
            MaterialDto actualizadoDto = mapper.map(actualizado, MaterialDto.class);
            return ResponseEntity.ok(actualizadoDto);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }
	
    
    
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
        Material existente = mservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el material con ID " + id);
        }
        mservice.borrar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<MaterialDto>> materialesBajoMinimo() {
        List<Material> todos = mservice.buscarTodos();
        List<MaterialDto> alertas = new ArrayList<>();
        for (Material m : todos) {
            if (m.getCantidad() <= m.getCantidadMinima()*2) {
                MaterialDto dto = mapper.map(m, MaterialDto.class);
                alertas.add(dto);
            }
        }
        System.out.println("Entrando en alertas, total materiales: " + todos.size());
        return ResponseEntity.ok(alertas);
    }



    @PostMapping("/insertar-completo")
    public ResponseEntity<?> insertarMaterialConStockYPrecio(@RequestBody MaterialDto dto) {
        // 1. Buscar categoría y familia
        Categoria categoria = catservice.buscar(dto.getCategoriaId());
        Familia familia = fservice.buscar(dto.getFamiliaId());
        if (categoria == null || familia == null) {
            return ResponseEntity.badRequest().body("Familia o Categoría no encontrada");
        }
        Proveedor proveedor = null;
        if (dto.getProveedorId() != null) {
            proveedor = pservice.buscar(dto.getProveedorId());
            if (proveedor == null) {
                return ResponseEntity.badRequest().body("Proveedor no encontrado con ID " + dto.getProveedorId());
            }
        }
        // 2. Crear Material
        Material material = mapper.map(dto, Material.class);
        material.setCategoria(categoria);
        material.setFamilia(familia);
        Material nuevo = mservice.insertar(material);

        // 3. Crear CompraMaterial de 1 unidad al precio indicado
        CompraMaterial compra = new CompraMaterial();
        compra.setMaterial(nuevo);
        compra.setFecha(LocalDateTime.of(2025, 7, 1, 0, 0));
        compra.setProveedor(proveedor);
        compra.setDescripcion("Inventari inicial juliol 2025");
        compra.setPrecioUnitario(dto.getPrecioActual());
        compra.setPrecioTotal(dto.getPrecioActual()*dto.getCantidad());
        compra.setCantidad(dto.getCantidad());
        CompraMaterial guardada = cmservice.insertar(compra);

        // 4. Crear MovimientoMaterial con stock real
        MovimientoMaterial mov_compra = new MovimientoMaterial();
        mov_compra.setMaterial(nuevo);
        mov_compra.setCantidad(dto.getCantidad());
        mov_compra.setDescripcion("Inventari inicial juliol 2025");
        mov_compra.setTipo(TipoMovimientoMaterial.ENTRADA);
        mov_compra.setFecha(LocalDateTime.of(2025, 7, 1, 0, 0));
        mov_compra.setCompraMaterial(guardada);
        mmservice.insertar(mov_compra);




        // 5. Devolver material DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(nuevo, MaterialDto.class));
    }
	
}
