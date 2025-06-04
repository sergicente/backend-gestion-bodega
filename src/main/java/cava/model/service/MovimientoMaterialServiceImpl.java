package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.MovimientoMaterial;
import cava.model.repository.MovimientoMaterialRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MovimientoMaterialServiceImpl implements MovimientoMaterialService{
	
	@Autowired
	private MovimientoMaterialRepository movmatrepo;

	@Override
	public MovimientoMaterial buscar(Long clave) {
		return movmatrepo.findById(clave).orElse(null);

	}

	@Override
	public List<MovimientoMaterial> buscarTodos() {
		return movmatrepo.findAll();
	}

	@Override
	public MovimientoMaterial insertar(MovimientoMaterial movimiento) {
	    return movmatrepo.save(movimiento);
	}

	@Override
	public MovimientoMaterial modificar(MovimientoMaterial entidad) {
	    if(movmatrepo.existsById(entidad.getId())) {
	        return movmatrepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public void borrar(Long clave) {
	    if (movmatrepo.existsById(clave)) {
	    	movmatrepo.deleteById(clave);
	    }
	}

	@Override
	public List<MovimientoMaterial> findByMaterialId(Long id) {
		return movmatrepo.findByMaterialId(id);
	}

	@Override
	public List<MovimientoMaterial> findByDeguelleId(Long id) {
		return movmatrepo.findByDeguelleId(id);
	}

}
