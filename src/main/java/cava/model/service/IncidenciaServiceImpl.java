package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cava.model.entity.Incidencia;
import cava.model.repository.IncidenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class IncidenciaServiceImpl implements IncidenciaService{
	
	@Autowired
	private IncidenciaRepository irepo;

	@Override
	public Incidencia buscar(Long clave) {
		return irepo.findById(clave).orElse(null);

	}

	@Override
	public List<Incidencia> buscarTodos() {
		return irepo.findAll();
	}

	@Override
	@Transactional
	public Incidencia insertar(Incidencia entidad) {
	    return irepo.save(entidad);
	}

	@Override
	@Transactional
	public Incidencia modificar(Incidencia entidad) {
	    if(irepo.existsById(entidad.getId())) {
	        return irepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la incidencia con ID: " + entidad.getId());
	    }
	}

	@Override
	@Transactional
	public void borrar(Long clave) {
	    if (irepo.existsById(clave)) {
	        irepo.deleteById(clave);
	    }
	}

	@Override
	public List<Incidencia> findByPartidaId(String id) {
		return irepo.findByPartidaId(id);
	}

	@Override
	public List<Incidencia> findByCavaId(String id) {
		return irepo.findByCavaId(id);
	}

	@Override
	public Incidencia findByDeguelleId(Long id) {
		return irepo.findByDeguelleId(id);
	}
}
