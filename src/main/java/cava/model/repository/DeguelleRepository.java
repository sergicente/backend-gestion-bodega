package cava.model.repository;

import java.time.LocalDateTime;
import java.util.List;
import cava.model.dto.DashboardGraficoCavasDto;
import org.springframework.data.jpa.repository.JpaRepository;
import cava.model.entity.Deguelle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeguelleRepository extends JpaRepository<Deguelle, Long>{
	List<Deguelle> findByCavaIdAndPartidaId(String cavaId, String partidaId);
	boolean existsByLotIgnoreCase(String lot);
	Deguelle findByLotIgnoreCase(String lot);
	List<Deguelle> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

	@Query("""
    SELECT new cava.model.dto.DashboardGraficoCavasDto(d.cava.nombre, SUM(d.cantidad))
    FROM Deguelle d
    WHERE YEAR(d.fecha) = :anyoActual
    GROUP BY d.cava.nombre
    ORDER BY SUM(d.cantidad) DESC
""")
	List<DashboardGraficoCavasDto> obtenerDeguelladosPorCavaEsteAnyo(@Param("anyoActual") int anyoActual);
}
