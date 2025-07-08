package cava.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cava;
    private int botellas;
    private String observaciones;
    private String lote;
    private int numeroPalet;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
	
}
