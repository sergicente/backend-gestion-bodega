package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.CompraMaterial;
import cava.model.entity.Familia;
import cava.model.entity.Proveedor;
import cava.model.repository.CompraMaterialRepository;
import cava.model.repository.FamiliaRepository;
import cava.model.repository.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ProveedorServiceImpl implements ProveedorService{
	
	@Autowired
	private ProveedorRepository prepo;

	@Override
	public Proveedor buscar(Long clave) {
		return prepo.findById(clave).orElse(null);

	}

	@Override
	public List<Proveedor> buscarTodos() {
		return prepo.findAll();
	}

	@Override
	@Transactional
	public Proveedor insertar(Proveedor entidad) {
	    return prepo.save(entidad);
	}

	@Override
	@Transactional
	public Proveedor modificar(Proveedor entidad) {
	    if(prepo.existsById(entidad.getId())) {
	        return prepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	@Transactional
	public void borrar(Long clave) {
	    if (prepo.existsById(clave)) {
	        prepo.deleteById(clave);
	    }
	}

}
