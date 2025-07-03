package cava.model.restcontroller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cava.model.dto.ArchivoDto;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cava.model.dto.PartidaDto;
import cava.model.entity.Partida;
import cava.model.service.PartidaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/partida")
public class PartidaController {
	
	@Autowired
	private PartidaService pservice;
	@Autowired
	private ModelMapper mapper;
    @Value("${ruta-archivos}")
    private String rutaBaseArchivos;
	
    // Obtener todas las partidas
    @GetMapping
    public ResponseEntity<?> obtenerTodas() {
        List<Partida> partidas = pservice.buscarTodos();
        List<PartidaDto> dtos = new ArrayList<>();

        for (Partida p : partidas) {
            PartidaDto dto = mapper.map(p, PartidaDto.class);
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
    }
    
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable String id) {
        Partida partida = pservice.buscar(id);
        if (partida == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
        } else {
            PartidaDto dto = mapper.map(partida, PartidaDto.class);
            return ResponseEntity.ok(dto);
        }
    }
    
    
    
    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUno(@RequestBody Partida cava) {

        // Verifica si ya existe
        Partida partidaExistente = pservice.buscar(cava.getId());
        if (partidaExistente != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: Ya existe una partida con el ID " + cava.getId());
        }

        // Inserta la nueva partida
        Partida nuevaPartida = pservice.insertar(cava);

        // Mapea a DTO manualmente, sin streams
        PartidaDto dto = mapper.map(nuevaPartida, PartidaDto.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    
    
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable String id, @RequestBody Partida cava) {
        try {
            if (!cava.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID en la URL y el body no coinciden");
            }

            Partida actualizado = pservice.modificar(cava);
            PartidaDto dto = mapper.map(actualizado, PartidaDto.class);

            return ResponseEntity.ok(dto);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }
	
	// Borrar una partida
	@DeleteMapping("/borrar/{id}")
	public ResponseEntity<?> borrar(@PathVariable String id) {
		Partida existente = pservice.buscar(id);
		if(existente == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la partida");
		}
		pservice.borrar(id);
		return ResponseEntity.noContent().build();
	}


    @GetMapping("/{id}/archivos")
    public ResponseEntity<List<ArchivoDto>> listarArchivos(@PathVariable String id) {
        Path carpeta = Paths.get(rutaBaseArchivos, "partidas", id);
        List<ArchivoDto> archivos = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(carpeta)) {
            for (Path path : stream) {
                if (!Files.isRegularFile(path)) continue;

                ArchivoDto dto = new ArchivoDto();
                dto.setNombre(path.getFileName().toString());
                dto.setTamano(Files.size(path));
                dto.setTipo(Files.probeContentType(path));
                java.nio.file.attribute.FileTime fileTime = Files.getLastModifiedTime(path);
                java.time.Instant instant = fileTime.toInstant();
                java.time.LocalDateTime fecha = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
                dto.setFecha(fecha);

                archivos.add(dto);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(archivos);
    }

    @GetMapping("/{id}/archivos/{nombreArchivo}")
    public ResponseEntity<Resource> descargarArchivo(
            @PathVariable String id,
            @PathVariable String nombreArchivo) {

        try {
            Path archivoPath = Paths.get(rutaBaseArchivos).resolve(id).resolve(nombreArchivo).normalize();

            if (!archivoPath.startsWith(Paths.get(rutaBaseArchivos))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (!Files.exists(archivoPath) || !Files.isReadable(archivoPath)) {
                return ResponseEntity.notFound().build();
            }

            UrlResource recurso = new UrlResource(archivoPath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                    .body((Resource) recurso);

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/archivos")
    public ResponseEntity<Map<String, String>> subirArchivos(
            @PathVariable String id,
            @RequestParam("archivos") List<MultipartFile> archivos) {

        Map<String, String> respuesta = new HashMap<>();

        try {
            Path carpeta = Paths.get(rutaBaseArchivos, "partidas", id);
            Files.createDirectories(carpeta);

            for (MultipartFile archivo : archivos) {
                if (archivo.isEmpty()) continue;

                String nombreOriginal = archivo.getOriginalFilename();
                if (nombreOriginal == null) continue;

                Path destino = carpeta.resolve(nombreOriginal);

                if (Files.exists(destino)) {
                    respuesta.put("mensaje", "Ya existe un archivo con el nombre: " + nombreOriginal);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
                }

                Files.copy(archivo.getInputStream(), destino);
            }

            respuesta.put("mensaje", "Archivos subidos correctamente");
            return ResponseEntity.ok(respuesta);

        } catch (IOException e) {
            respuesta.put("mensaje", "Error al guardar archivos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }
	
}
