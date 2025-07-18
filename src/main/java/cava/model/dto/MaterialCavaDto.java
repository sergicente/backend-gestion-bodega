package cava.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCavaDto {
    private int id; 
    private Long materialId;
    private String materialNombre;
    private float materialPrecioActual;
    private int cantidad;
    private int materialCantidadMinima;
    private String categoriaNombre;
    private int cantidadGastada;
    private String cavaNombre;
}

