package cava.model.dto;

import cava.model.entity.PedidoTarea;
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
	private String nota1;
	private String nota2;
	private boolean urgente;
	private boolean gls;
	private List<LineaPedidoDto> lineas;
	private List<PedidoTareaDto> tareas;
}
