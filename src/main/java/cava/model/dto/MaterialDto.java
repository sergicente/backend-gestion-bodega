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
    private float precioActual;
    private Long categoriaId;
    private String categoriaNombre;
    private Long familiaId;
    private String familiaNombre;
    private String observaciones;
    private int cantidad;
    private int cantidadMinima;
    private float cantidadGastada;
}
