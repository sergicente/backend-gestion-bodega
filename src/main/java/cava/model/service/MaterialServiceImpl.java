package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.Categoria;
import cava.model.entity.Familia;
import cava.model.entity.Material;
import cava.model.repository.MaterialRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MaterialServiceImpl implements MaterialService{
	
	@Autowired
	private MaterialRepository mrepo;

	@Override
	public Material buscar(Long clave) {
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
	public void borrar(Long clave) {
	    if (mrepo.existsById(clave)) {
	    	mrepo.deleteById(clave);
	    }
	}

	@Override
	public List<Material> findByFamilia(Familia familia) {
		return mrepo.findByFamilia(familia);
	}

	@Override
	public List<Material> findByCategoria(Categoria categoria) {
		return mrepo.findByCategoria(categoria);
	}



}
