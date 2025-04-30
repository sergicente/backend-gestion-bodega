package cava.model.dto;

import java.time.LocalDate;

import cava.model.entity.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDto {
    private Long id;
    private LocalDate fecha;
    private TipoMovimiento tipo;
    private String descripcion;
    private int cantidad;

    private String partidaId;
    private Integer materialId;
    
}
