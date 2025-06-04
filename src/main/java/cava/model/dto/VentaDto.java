package cava.model.dto;

import cava.model.entity.Trimestre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDto {
    private String id;
    private int ano;
    private Trimestre trimestre;
    private int cantidad;
    private Long cavaPartidaId;
}
