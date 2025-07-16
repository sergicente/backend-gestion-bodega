package cava.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cava.model.entity.Partida;
import java.time.LocalDate;
import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, String>{
    List<Partida> findByFechaEmbotelladoBetween (LocalDate desde, LocalDate hasta);
}
