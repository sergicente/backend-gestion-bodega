package cava.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDto {
    private Long id;
    private String nombre;
    private Long categoriaId;
    private Long familiaId;
    private String observaciones;
    private int cantidad;
}
