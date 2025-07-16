package cava.model.service;

import cava.model.entity.Log;
import cava.model.repository.LogRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements LogService{
	
	@Autowired
	private LogRepository lrepo;

	@Override
	public Log buscar(Long clave) {
		return lrepo.findById(clave).orElse(null);

	}

	@Override
	public List<Log> buscarTodos() {
		return lrepo.findAll();
	}

	@Override
	public Log insertar(Log entidad) {
	    try {
	        return lrepo.save(entidad);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	@Override
	public Log modificar(Log entidad) {
	    if(lrepo.existsById(entidad.getId())) {
	        return lrepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public void borrar(Long clave) {
	    if (lrepo.existsById(clave)) {
	        lrepo.deleteById(clave);
	    }
	}

	public void registrar(String evento) {
		Log log = new Log();
		log.setHora(java.time.LocalDateTime.now());
		log.setEvento(evento);
		insertar(log);
	}

}
