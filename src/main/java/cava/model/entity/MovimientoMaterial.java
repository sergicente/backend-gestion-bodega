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
public class MovimientoMaterial {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDate fecha;
	
    @Enumerated(EnumType.STRING)
	private TipoMovimientoMaterial tipo;
	private String descripcion;
	private int cantidad;

    @ManyToOne
    @JoinColumn(name = "material_id")
	private Material material;
    
    @ManyToOne
    @JoinColumn(name = "deguelle_id")
    private Deguelle deguelle;
	
    private int stockResultante;

    @ManyToOne
    @JoinColumn(name = "compra_material_id", nullable = true)
    private CompraMaterial compraMaterial;
}
