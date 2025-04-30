package cava.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDto {

    private int id;
    private String nombre;
    private String tipo;
    private String observaciones;
    private int cantidad;
}
