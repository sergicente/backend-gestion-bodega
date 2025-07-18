package cava.model.service;

import java.util.List;

import cava.model.entity.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.CompraMaterial;
import cava.model.repository.CompraMaterialRepository;
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
		return cmrepo.findByMaterialIdOrderByFechaDesc(materialId);
	}

	@Override
	public List<CompraMaterial> findByProveedorId(Long id) {
		return cmrepo.findByProveedorId(id);
	}

	@Override
	public List<Material> obtenerMaterialesPorProveedor(Long proveedorId) {
		return cmrepo.findMaterialesByProveedorId(proveedorId);
	}

}
