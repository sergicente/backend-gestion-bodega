package cava.model.service;

import java.util.List;
import java.util.Optional;

import cava.model.entity.CavaPartida;
import cava.model.entity.MaterialCava;

public interface CavaPartidaService extends InterfaceGenericoCrud<CavaPartida, Long>{
	List<CavaPartida>findByCavaId(String cavaId);

	Optional<CavaPartida> buscarPorCavaYPartida(String idCava, String idPartida);
}
