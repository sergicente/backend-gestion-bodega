package cava.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.CavaPartida;
import cava.model.entity.Partida;
import cava.model.repository.CavaPartidaRepository;
import cava.model.repository.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CavaPartidaServiceImpl implements CavaPartidaService{
	
	@Autowired
	private CavaPartidaRepository prepo;

	@Override
	public CavaPartida buscar(Long clave) {
		return prepo.findById(clave).orElse(null);

	}

	@Override
	public List<CavaPartida> buscarTodos() {
		// TODO Auto-generated method stub
		return prepo.findAll();
	}

	@Override
	public CavaPartida insertar(CavaPartida entidad) {
		try {
			return prepo.save(entidad);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public CavaPartida modificar(CavaPartida entidad) {
	    if(prepo.existsById(entidad.getId())) {
	        return prepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public void borrar(Long clave) {
	    if (prepo.existsById(clave)) {
	    	prepo.deleteById(clave);
	    }
	}

	@Override
	public List<CavaPartida> findByCavaId(String cavaId) {
		return prepo.findByCavaId(cavaId);
	}

	@Override
	public Optional<CavaPartida> buscarPorCavaYPartida(String idCava, String idPartida) {
	    return prepo.findByCavaIdAndPartidaId(idCava, idPartida);
	}




}
