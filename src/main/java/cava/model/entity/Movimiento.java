package cava.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Movimiento {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDate fecha;
	
    @Enumerated(EnumType.STRING)
	private TipoMovimiento tipo;
	private String descripcion;
	private int cantidad;
	
    @ManyToOne
    @JoinColumn(name = "partida_id")
	private Partida partida;
    

    @ManyToOne
    @JoinColumn(name = "material_id")
	private Material material;
	
	
}
