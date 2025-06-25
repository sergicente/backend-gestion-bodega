package cava.model.service;

import java.util.List;
import java.util.Optional;

import cava.model.entity.CavaPartida;
import cava.model.entity.Deguelle;

public interface DeguelleService extends InterfaceGenericoCrud<Deguelle, Long>{
	List<Deguelle> buscarPorCavaYPartida(String cavaId, String partidaId);
	boolean existsByLotIgnoreCase(String lot);
	Deguelle findByLotIgnoreCase(String lot);
}
