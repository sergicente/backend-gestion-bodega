package cava.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Partida {
	
    @Id
    private String id;

    private LocalDate fechaEmbotellado;

    private int botellasRima;
    private int botellasPunta;
    private int botellasDeguelladas;
    private int botellasStock;
    private int botellasVendidas;
}
