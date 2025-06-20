package cava.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import cava.model.entity.TipoIncidencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidenciaDto {
    private Long id;
    private LocalDateTime fecha;
    private TipoIncidencia tipo;
    private int cantidad;
    private String partidaId;
    private String cavaId;
    private String cavaNombre;
	private String detalles;
    private Long deguelleId;
}
