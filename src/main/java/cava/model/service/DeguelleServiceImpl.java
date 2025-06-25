package cava.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.CavaPartida;
import cava.model.entity.Deguelle;
import cava.model.repository.DeguelleRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class DeguelleServiceImpl implements DeguelleService{
	
	@Autowired
	private DeguelleRepository movbotrepo;

	@Override
	public Deguelle buscar(Long clave) {
		return movbotrepo.findById(clave).orElse(null);

	}

	@Override
	public List<Deguelle> buscarTodos() {
		return movbotrepo.findAll();
	}

	@Override
	public Deguelle insertar(Deguelle movimiento) {
	    return movbotrepo.save(movimiento);
	}

	@Override
	public Deguelle modificar(Deguelle entidad) {
	    if(movbotrepo.existsById(entidad.getId())) {
	        return movbotrepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public void borrar(Long clave) {
	    if (movbotrepo.existsById(clave)) {
	    	movbotrepo.deleteById(clave);
	    }
	}

	@Override
	public List<Deguelle> buscarPorCavaYPartida(String cavaId, String partidaId) {
	    return movbotrepo.findByCavaIdAndPartidaId(cavaId, partidaId);
	}

	@Override
	public boolean existsByLotIgnoreCase(String lot) {
		return movbotrepo.existsByLotIgnoreCase(lot.trim());
	}


}
