package cava.model.restcontroller;

import cava.model.dto.*;
import cava.model.entity.*;
import cava.model.service.LogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/log")
public class LogController {
	
	@Autowired
	private LogService lservice;

	@Autowired
	private ModelMapper mapper;
	
    // Obtener todas las partidas
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        List<Log> logs = lservice.buscarTodos();
        List<LogDto> logsDto = new ArrayList<>();

        for (Log l : logs) {
            logsDto.add(mapper.map(l, LogDto.class));
        }

        return ResponseEntity.ok(logsDto);
    }
    

    @PostMapping
    public ResponseEntity<?> insertarUno(@RequestBody LogDto dto) {

        if (dto.getEvento() == null || dto.getEvento().isBlank()) {
            return ResponseEntity.badRequest().body("El evento no puede estar vacío");
        }

        // Convertir DTO a entidad
        Log log = new Log(dto.getEvento(), dto.getUsuario());
        // Guardar
        Log guardado = lservice.insertar(log);

        if (guardado == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se ha podido guardar el log");
        }
        // Convertir a DTO de respuesta
        LogDto nuevoDto = mapper.map(guardado, LogDto.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDto);
    }
    
    

	
}
