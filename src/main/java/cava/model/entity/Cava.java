package cava.model.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    
    private boolean ecologico;
    
    private int cantidad;
    
    @ManyToOne
    @JoinColumn(name = "familia_id", nullable = false)
    private Familia familia;

}
