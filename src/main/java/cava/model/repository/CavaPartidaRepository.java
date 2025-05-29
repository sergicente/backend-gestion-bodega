package cava.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.CavaPartida;
import cava.model.entity.MaterialCava;

public interface CavaPartidaRepository extends JpaRepository<CavaPartida, Long>{
	List<CavaPartida> findByCavaId(String cavaId);
}
