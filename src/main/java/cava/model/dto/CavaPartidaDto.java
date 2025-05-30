package cava.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CavaPartidaDto {
	private Long id;
    private String cavaId;
    private String cavaNombre;
    private String partidaId;
    private boolean actual;
    private boolean partidaEcologico;
    private int cantidad;
    private int vendido;
    private int partidaBotellasRima;
}
