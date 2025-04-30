package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.Jaula;
import cava.model.entity.Partida;
import cava.model.repository.JaulaRepository;
import cava.model.repository.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class JaulaServiceImpl implements JaulaService{
	
	@Autowired
	private JaulaRepository jrepo;

	@Override
	public Jaula buscar(Long clave) {
		return jrepo.findById(clave).orElse(null);

	}

	@Override
	public List<Jaula> buscarTodos() {
		// TODO Auto-generated method stub
		return jrepo.findAll();
	}

	@Override
	public Jaula insertar(Jaula entidad) {
		try {
			if(jrepo.existsById(entidad.getId())) {
				return null;
			}else {
				return jrepo.save(entidad);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Jaula modificar(Jaula entidad) {
	    if(jrepo.existsById(entidad.getId())) {
	        return jrepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public int borrar(Long clave) {
		try {
			if(jrepo.existsById(clave)) {
				jrepo.deleteById(clave);
				return 1;
			}else {
				return 0;
			}
		}catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

}
