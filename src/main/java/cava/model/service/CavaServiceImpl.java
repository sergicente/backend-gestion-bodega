package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.Cava;
import cava.model.entity.Familia;
import cava.model.entity.Partida;
import cava.model.repository.CavaRepository;
import cava.model.repository.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CavaServiceImpl implements CavaService{
	
	@Autowired
	private CavaRepository crepo;

	@Override
	public Cava buscar(String clave) {
		return crepo.findById(clave).orElse(null);

	}

	@Override
	public List<Cava> buscarTodos() {
		return crepo.findAll();
	}

	@Override
	public Cava insertar(Cava entidad) {
		try {
			if(crepo.existsById(entidad.getId())) {
				return null;
			}else {
				return crepo.save(entidad);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Cava modificar(Cava entidad) {
	    if(crepo.existsById(entidad.getId())) {
	        return crepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public void borrar(String clave) {
	    if (crepo.existsById(clave)) {
	    	crepo.deleteById(clave);
	    }
	}

	@Override
	public List<Cava> findByFamilia(Familia familia) {
		return crepo.findByFamilia(familia);
	}



}
