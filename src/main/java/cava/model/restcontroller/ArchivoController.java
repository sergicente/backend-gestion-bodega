package cava.model.restcontroller;

import cava.model.dto.ArchivoDto;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {
	

    @Value("${ruta-archivos}")
    private String rutaBaseArchivos;


    @GetMapping("/{tipo}/{id}")
    public ResponseEntity<List<ArchivoDto>> listarArchivos(@PathVariable String tipo, @PathVariable String id) {
        Path carpeta = Path.of(rutaBaseArchivos, tipo, id);
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
            Path archivoPath = Path.of(rutaBaseArchivos, tipo, id, nombreArchivo).normalize();

            if (!archivoPath.startsWith(Path.of(rutaBaseArchivos))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (!Files.exists(archivoPath) || !Files.isReadable(archivoPath)) {
                return ResponseEntity.notFound().build();
            }

            UrlResource recurso = new UrlResource(archivoPath.toUri());
            String contentType = Files.probeContentType(archivoPath); // Detecta tipo MIME
            if (contentType == null) {
                contentType = "application/octet-stream"; // por si no lo detecta
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                    .body((Resource) recurso);

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{tipo}/{id}/{nombreArchivo}")
    public ResponseEntity<Void> eliminarArchivo(@PathVariable String tipo,
                                                @PathVariable String id,
                                                @PathVariable String nombreArchivo) {
        try {
            Path archivoPath = Path.of(rutaBaseArchivos, tipo, id, nombreArchivo).normalize();

            if (!archivoPath.startsWith(Path.of(rutaBaseArchivos))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (!Files.exists(archivoPath) || !Files.isReadable(archivoPath)) {
                return ResponseEntity.notFound().build();
            }

            Files.delete(archivoPath);

            return ResponseEntity.noContent().build();

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{tipo}/{id}")
    public ResponseEntity<Map<String, String>> subirArchivos(@PathVariable String tipo,
            @PathVariable String id,
            @RequestParam List<MultipartFile> archivos) {

        Map<String, String> respuesta = new HashMap<>();

        try {
            Path carpeta = Path.of(rutaBaseArchivos, tipo, id);
            Files.createDirectories(carpeta);

            for (MultipartFile archivo : archivos) {
                if (archivo.isEmpty()) continue;

                String nombreOriginal = archivo.getOriginalFilename();
                if (nombreOriginal == null) continue;

                String nombreSinExtension = nombreOriginal;
                String extension = "";

                int punto = nombreOriginal.lastIndexOf('.');
                if (punto != -1) {
                    nombreSinExtension = nombreOriginal.substring(0, punto);
                    extension = nombreOriginal.substring(punto);
                }

                Path destino = carpeta.resolve(nombreOriginal);
                int contador = 2;

                while (Files.exists(destino)) {
                    String nuevoNombre = nombreSinExtension + "-" + contador + extension;
                    destino = carpeta.resolve(nuevoNombre);
                    contador++;
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
