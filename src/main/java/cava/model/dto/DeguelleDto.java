package cava.model.dto;

import java.time.LocalDate;

import cava.model.entity.EstadoBotella;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeguelleDto {
	private Long id;
	private LocalDate fecha;
	private String descripcion;
	private String lot;
	private int cantidad;
	private EstadoBotella estadoAnterior;
	private EstadoBotella estadoNuevo;
	private String partidaId;
	private String cavaId;
	private String cavaNombre;
	private boolean partidaEcologico;
}
