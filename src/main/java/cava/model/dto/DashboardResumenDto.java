package cava.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumenDto {

    private int totalBotellasEnRima;
    private int salidas;
    private int salidasP;
    private int embotellada;
    private int embotelladaP;
    private double crianzaMedia;
}
