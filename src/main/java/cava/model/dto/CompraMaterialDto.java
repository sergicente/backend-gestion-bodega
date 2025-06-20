package cava.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import cava.model.entity.Proveedor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraMaterialDto {
    private Long id;

    private int cantidad;
    private double precioTotal;
    private String descripcion;
    private Long proveedorId;
    private String proveedorNombre;
    private LocalDateTime fecha;
    private Long materialId;
    private String materialNombre;
    private String materialCategoriaNombre;

    // precioUnitario calculado en el DTO
    public double getPrecioUnitario() {
        return cantidad > 0 ? precioTotal / cantidad : 0.0;
    }
}
