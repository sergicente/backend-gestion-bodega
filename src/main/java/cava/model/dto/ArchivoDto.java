package cava.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoDto {
    private String nombre;
    private long tamano;
    private String tipo;
    private LocalDateTime fecha;
}
