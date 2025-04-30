package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.Jaula;
import cava.model.entity.Material;
import cava.model.entity.MaterialCava;
import cava.model.entity.Partida;
import cava.model.repository.JaulaRepository;
import cava.model.repository.MaterialCavaRepository;
import cava.model.repository.MaterialRepository;
import cava.model.repository.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MaterialCavaServiceImpl implements MaterialCavaService{
	
	@Autowired
	private MaterialCavaRepository mcrepo;

	@Override
	public MaterialCava buscar(Integer clave) {
		return mcrepo.findById(clave).orElse(null);

	}

	@Override
	public List<MaterialCava> buscarTodos() {
		// TODO Auto-generated method stub
		return mcrepo.findAll();
	}

	@Override
	public MaterialCava insertar(MaterialCava material) {
	    return mcrepo.save(material);
	}

	@Override
	public MaterialCava modificar(MaterialCava entidad) {
	    if(mcrepo.existsById(entidad.getId())) {
	        return mcrepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public int borrar(Integer clave) {
		try {
			if(mcrepo.existsById(clave)) {
				mcrepo.deleteById(clave);
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
	public List<MaterialCava> findByCavaId(String cavaId) {
		// TODO Auto-generated method stub
		return mcrepo.findByCavaId(cavaId);
	}



}
