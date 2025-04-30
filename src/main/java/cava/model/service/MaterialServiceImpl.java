package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.Jaula;
import cava.model.entity.Material;
import cava.model.entity.Partida;
import cava.model.repository.JaulaRepository;
import cava.model.repository.MaterialRepository;
import cava.model.repository.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MaterialServiceImpl implements MaterialService{
	
	@Autowired
	private MaterialRepository mrepo;

	@Override
	public Material buscar(Integer clave) {
		return mrepo.findById(clave).orElse(null);

	}

	@Override
	public List<Material> buscarTodos() {
		// TODO Auto-generated method stub
		return mrepo.findAll();
	}

	@Override
	public Material insertar(Material material) {
	    return mrepo.save(material);
	}

	@Override
	public Material modificar(Material entidad) {
	    if(mrepo.existsById(entidad.getId())) {
	        return mrepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public int borrar(Integer clave) {
		try {
			if(mrepo.existsById(clave)) {
				mrepo.deleteById(clave);
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
