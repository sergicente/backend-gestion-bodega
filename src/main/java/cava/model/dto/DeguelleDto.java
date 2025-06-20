package cava.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeguelleDto {
	private Long id;
	private LocalDateTime fecha;
	private String lot;
	private String lotTap;
	private int cantidad;
	private int merma;
	private String partidaId;
	private String cavaId;
	private String cavaNombre;
	private boolean partidaEcologico;
	private boolean cavaEcologico;
	private boolean limpieza;
	private String observaciones;
	private int licor;

}
