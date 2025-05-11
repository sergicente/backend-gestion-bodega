package cava.model.dto;

import java.time.LocalDate;

import cava.model.entity.TipoMovimientoMaterial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoMaterialDto {
    private Long id;
    private LocalDate fecha;
    private TipoMovimientoMaterial tipo;
    private String descripcion;
    private int cantidad;
    private Long materialId;
    
}
