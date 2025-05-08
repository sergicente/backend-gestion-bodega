package cava.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long>{
	List<Movimiento> findByMaterialId(Long id);
}
