package cava.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long>{

}
