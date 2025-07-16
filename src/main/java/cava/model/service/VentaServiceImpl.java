package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cava.model.entity.Venta;
import cava.model.repository.VentaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class VentaServiceImpl implements VentaService{
	
	@Autowired
	private VentaRepository vrepo;

	@Override
	public Venta buscar(String clave) {
		return vrepo.findById(clave).orElse(null);

	}

	@Override
	public List<Venta> buscarTodos() {
		return vrepo.findAll();
	}

	@Override
	public Venta insertar(Venta entidad) {
	    return vrepo.save(entidad); // Let it insert directly
	}

	@Override
	public Venta modificar(Venta entidad) {
	    if(vrepo.existsById(entidad.getId())) {
	        return vrepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public void borrar(String clave) {
	    if (vrepo.existsById(clave)) {
	    	vrepo.deleteById(clave);
	    }
	}


}
