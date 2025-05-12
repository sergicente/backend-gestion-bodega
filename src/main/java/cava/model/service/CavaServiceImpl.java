package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.Cava;
import cava.model.entity.Familia;
import cava.model.entity.Jaula;
import cava.model.entity.Partida;
import cava.model.repository.CavaRepository;
import cava.model.repository.JaulaRepository;
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
		// TODO Auto-generated method stub
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
	public int borrar(String clave) {
		try {
			if(crepo.existsById(clave)) {
				crepo.deleteById(clave);
				return 1;
			}else {
				return 0;
			}
		}catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Cava> findByFamilia(Familia familia) {
		return crepo.findByFamilia(familia);
	}

}
