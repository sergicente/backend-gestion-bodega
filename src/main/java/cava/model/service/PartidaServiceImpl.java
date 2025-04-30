package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.Partida;
import cava.model.repository.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PartidaServiceImpl implements PartidaService{
	
	@Autowired
	private PartidaRepository prepo;

	@Override
	public Partida buscar(String clave) {
		return prepo.findById(clave).orElse(null);

	}

	@Override
	public List<Partida> buscarTodos() {
		// TODO Auto-generated method stub
		return prepo.findAll();
	}

	@Override
	public Partida insertar(Partida entidad) {
		try {
			if(prepo.existsById(entidad.getId())) {
				return null;
			}else {
				return prepo.save(entidad);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Partida modificar(Partida entidad) {
	    if(prepo.existsById(entidad.getId())) {
	        return prepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public int borrar(String clave) {
		try {
			if(prepo.existsById(clave)) {
				prepo.deleteById(clave);
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
