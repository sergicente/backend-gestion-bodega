package cava.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Venta {
	
    @Id
    private String id;
    
    private int ano;
    
    @Enumerated(EnumType.STRING)
    private Trimestre trimestre;
    
    private int cantidad;
    
    @ManyToOne
    private CavaPartida cavaPartida;

}
