package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.Cava;
import cava.model.entity.Familia;
import cava.model.entity.Partida;
import cava.model.repository.CavaRepository;
import cava.model.repository.FamiliaRepository;
import cava.model.repository.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FamiliaServiceImpl implements FamiliaService{
	
	@Autowired
	private FamiliaRepository frepo;

	@Override
	public Familia buscar(Long clave) {
		return frepo.findById(clave).orElse(null);

	}

	@Override
	public List<Familia> buscarTodos() {
		// TODO Auto-generated method stub
		return frepo.findAll();
	}

	@Override
	public Familia insertar(Familia entidad) {
	    try {
	        return frepo.save(entidad);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	@Override
	public Familia modificar(Familia entidad) {
	    if(frepo.existsById(entidad.getId())) {
	        return frepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public void borrar(Long clave) {
	    if (frepo.existsById(clave)) {
	        frepo.deleteById(clave);
	    }
	}

}
