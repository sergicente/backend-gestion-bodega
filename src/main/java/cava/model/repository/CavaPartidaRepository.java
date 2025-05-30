package cava.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.CavaPartida;

public interface CavaPartidaRepository extends JpaRepository<CavaPartida, Long>{
	List<CavaPartida> findByCavaId(String cavaId);
	Optional<CavaPartida>findByCavaIdAndPartidaId(String idCava, String idPartida);
}
