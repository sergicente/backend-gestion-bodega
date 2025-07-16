package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.Categoria;
import cava.model.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoriaServiceImpl implements CategoriaService{
	
	@Autowired
	private CategoriaRepository crepo;

	@Override
	public Categoria buscar(Long clave) {
		return crepo.findById(clave).orElse(null);

	}

	@Override
	public List<Categoria> buscarTodos() {
		return crepo.findAll();
	}

	@Override
	public Categoria insertar(Categoria entidad) {
	    try {
	        return crepo.save(entidad);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	@Override
	public Categoria modificar(Categoria entidad) {
	    if(crepo.existsById(entidad.getId())) {
	        return crepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public void borrar(Long clave) {
	    if (crepo.existsById(clave)) {
	    	crepo.deleteById(clave);
	    }
	}

}