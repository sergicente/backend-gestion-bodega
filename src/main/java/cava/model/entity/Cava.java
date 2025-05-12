package cava.model.entity;

import jakarta.persistence.Entity;
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
public class Cava {
	
    @Id
    private String id;
    
    private String nombre;
    
    private int cantidad;
    
    @ManyToOne
    @JoinColumn(name = "familia_id", nullable = false)
    private Familia familia;
    
    @ManyToOne
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;
    

}
