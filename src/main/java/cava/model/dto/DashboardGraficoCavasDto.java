package cava.model.dto;

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
