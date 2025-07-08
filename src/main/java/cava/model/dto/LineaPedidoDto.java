package cava.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineaPedidoDto {
	private Long id;
	private String cava;
	private int botellas;
	private String observaciones;
	private String lote;
	private int numeroPalet;
}
