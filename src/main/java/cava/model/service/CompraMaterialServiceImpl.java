package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.CompraMaterial;
import cava.model.entity.Familia;
import cava.model.entity.MovimientoMaterial;
import cava.model.repository.CompraMaterialRepository;
import cava.model.repository.FamiliaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CompraMaterialServiceImpl implements CompraMaterialService{
	
	@Autowired
	private CompraMaterialRepository cmrepo;

	@Override
	public CompraMaterial buscar(Long clave) {
		return cmrepo.findById(clave).orElse(null);

	}

	@Override
	public List<CompraMaterial> buscarTodos() {
		return cmrepo.findAll();
	}

	@Override
	@Transactional
	public CompraMaterial insertar(CompraMaterial entidad) {
	    return cmrepo.save(entidad);
	}

	@Override
	@Transactional
	public CompraMaterial modificar(CompraMaterial entidad) {
	    if(cmrepo.existsById(entidad.getId())) {
	        return cmrepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	@Transactional
	public void borrar(Long clave) {
	    if (cmrepo.existsById(clave)) {
	        cmrepo.deleteById(clave);
	    }
	}
	
	@Override
	public List<CompraMaterial> findByMaterialId(Long id) {
		return cmrepo.findByMaterialId(id);
	}

	@Override
	public List<CompraMaterial> findByMaterialIdOrderByFechaDesc(Long materialId) {
		// TODO Auto-generated method stub
		return cmrepo.findByMaterialIdOrderByFechaDesc(materialId);
	}

}
