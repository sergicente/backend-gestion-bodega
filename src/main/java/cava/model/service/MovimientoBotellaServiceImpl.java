package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.MovimientoBotella;
import cava.model.repository.MovimientoBotellaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MovimientoBotellaServiceImpl implements MovimientoBotellaService{
	
	@Autowired
	private MovimientoBotellaRepository movbotrepo;

	@Override
	public MovimientoBotella buscar(Long clave) {
		return movbotrepo.findById(clave).orElse(null);

	}

	@Override
	public List<MovimientoBotella> buscarTodos() {
		return movbotrepo.findAll();
	}

	@Override
	public MovimientoBotella insertar(MovimientoBotella movimiento) {
	    return movbotrepo.save(movimiento);
	}

	@Override
	public MovimientoBotella modificar(MovimientoBotella entidad) {
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



}
