package cava.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto {
	private Long id;
	private String cliente;
	private String observacionesGenerales;
	private LocalDate fechaCreacion;
	private LocalDate fechaLimite;
	private String estado;
	private boolean urgente;
	private List<LineaPedidoDto> lineas;
}
