package cava.model.restcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

import cava.model.dto.CategoriaDto;
import cava.model.dto.MaterialDto;
import cava.model.entity.Categoria;
import cava.model.entity.Material;
import cava.model.service.CategoriaService;
import cava.model.service.MaterialService;

@RestController
@RequestMapping("/api/categoria")
public class CategoriaController {
	
	@Autowired
	private CategoriaService catservice;
	@Autowired
	private MaterialService mservice;
	@Autowired
	private ModelMapper mapper;
	
	
    // Obtener todas las partidas
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
    	List<Categoria> categorias = catservice.buscarTodos();
    	List<CategoriaDto> listaDto = new ArrayList<>();
    	for(Categoria c : categorias) {
    		CategoriaDto dto = mapper.map(c, CategoriaDto.class);
    		listaDto.add(dto);
    	}
        return ResponseEntity.ok(listaDto);
    }
    
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable long id) {
    	Categoria categoria = catservice.buscar(id);
    	if(categoria == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la categoria");
    	}else {
    		return ResponseEntity.ok(categoria);
    	}
    }
    
    
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody CategoriaDto dto) {
    	


        // Convertir DTO a entidad
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());

        // Guardar
        Categoria guardada = catservice.insertar(categoria);

        // Convertir a DTO de respuesta
        CategoriaDto nuevoDto = new CategoriaDto(
            guardada.getId(),
            guardada.getNombre()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }
    
    
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<CategoriaDto> modificar(@PathVariable Long id, @RequestBody CategoriaDto dto) {
        
        // Buscar la familia existente
    	Categoria existente = catservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizar campos
        existente.setNombre(dto.getNombre());

        // Guardar cambios
        Categoria actualizada = catservice.insertar(existente);

        // Devolver DTO actualizado
        CategoriaDto respuesta = new CategoriaDto(
            actualizada.getId(),
            actualizada.getNombre()
        );

        return ResponseEntity.ok(respuesta);
    }
	
    
    
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        
    	Categoria existente = catservice.buscar(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        try {
        	catservice.borrar(id);
            return ResponseEntity.ok().body(Map.of("mensaje", "Categoria eliminada correctamente"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "No se puede eliminar la categoria porque está en uso."));
        }
    }
    
    @GetMapping("/materiales/{id}")
    public ResponseEntity<?> buscarMateriales(@PathVariable Long id) {
        Categoria categoria = catservice.buscar(id);
        if (categoria == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Familia no encontrada con ID: " + id);
        }
        List<Material> lista = mservice.findByCategoria(categoria);
        List<MaterialDto> listaDto = new ArrayList<>();
        for(Material m : lista){
        	MaterialDto dto = mapper.map(m, MaterialDto.class);
        	listaDto.add(dto);
        }
        return ResponseEntity.ok(listaDto);
    }

	
}
