package cava.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.CavaPartida;
import cava.model.entity.Deguelle;

public interface DeguelleRepository extends JpaRepository<Deguelle, Long>{
	List<Deguelle> findByCavaIdAndPartidaId(String cavaId, String partidaId);
	boolean existsByLotIgnoreCase(String lot);
}
