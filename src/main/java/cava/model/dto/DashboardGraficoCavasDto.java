package cava.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class DashboardGraficoCavasDto {
    private String cavaNombre;
    private Long cantidad;

    public DashboardGraficoCavasDto(String cavaNombre, Long cantidad) {
        this.cavaNombre = cavaNombre;
        this.cantidad = cantidad;
    }

    public String getCavaNombre() {
        return cavaNombre;
    }

    public Long getCantidad() {
        return cantidad;
    }
}
