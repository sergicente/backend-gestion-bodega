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
public class Deguelle {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate fecha;
		
	private String lot;
	
	private int cantidad;
	
	private int merma;
	
	private boolean limpieza;
	
	private String observaciones;
	
	private int licor;

	@ManyToOne
	@JoinColumn(name = "partida_id")
	private Partida partida;
	
	@ManyToOne
	@JoinColumn(name = "cava_id")
	private Cava cava;
}