package cava.model.restcontroller;

import cava.model.dto.ArchivoDto;
import org.springframework.core.io.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {
	
	@Autowired
	private ModelMapper mapper;
    @Value("${ruta-archivos}")
    private String rutaBaseArchivos;


    @GetMapping("/{tipo}/{id}")
    public ResponseEntity<List<ArchivoDto>> listarArchivos(@PathVariable String tipo, @PathVariable String id) {
        Path carpeta = Paths.get(rutaBaseArchivos, tipo, id);
        if (!Files.exists(carpeta)) {
            return ResponseEntity.ok(Collections.emptyList());
        }
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
                LocalDateTime fecha = LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
                dto.setFecha(fecha);
                archivos.add(dto);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(archivos);
    }

    @GetMapping("/{tipo}/{id}/{nombreArchivo}")
    public ResponseEntity<Resource> descargarArchivo( @PathVariable String tipo,
            @PathVariable String id,
            @PathVariable String nombreArchivo) {

        try {
            Path archivoPath = Paths.get(rutaBaseArchivos, tipo, id, nombreArchivo).normalize();

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

    @PostMapping("/{tipo}/{id}")
    public ResponseEntity<Map<String, String>> subirArchivos(@PathVariable String tipo,
            @PathVariable String id,
            @RequestParam("archivos") List<MultipartFile> archivos) {

        Map<String, String> respuesta = new HashMap<>();

        try {
            Path carpeta = Paths.get(rutaBaseArchivos, tipo, id);
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
