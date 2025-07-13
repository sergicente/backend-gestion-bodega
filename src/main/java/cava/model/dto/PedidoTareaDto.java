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
public class PedidoTareaDto {
	private Long id;
	private String texto;
	private boolean completado;
}
