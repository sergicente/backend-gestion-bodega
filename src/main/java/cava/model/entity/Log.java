package cava.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@Entity
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime hora;
    @Column(columnDefinition = "TEXT")
    private String evento;
    private String usuario;
    public Log(String evento, String usuario) {
        this.hora = LocalDateTime.now();
        this.evento = evento;
        this.usuario = usuario;
    }
}
