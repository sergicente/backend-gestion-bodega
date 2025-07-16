package cava.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pedido {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cliente;
    @Column(name = "observaciones_generales", length = 5000)
    private String observacionesGenerales;
    private LocalDate fechaCreacion;
    private LocalDate fechaLimite;
    private String estado;
    private String nota1;
    private String nota2;
    private boolean gls;
    private boolean urgente;
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineas = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoTarea> tareas = new ArrayList<>();
}
