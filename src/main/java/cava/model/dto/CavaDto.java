package cava.model.dto;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CavaDto {
    private String id;
    private String nombre;
    private String familiaId;
    private String familiaNombre;
    private String calificacion;
    private String tipo;
    private boolean ecologico;
    private PartidaDto partidaActual;
    private List<CavaPartidaDto> partidasRelacionadas;
    private List<MaterialCavaDto> materialesRelacionados;
}
