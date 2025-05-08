package cava.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.Movimiento;
import cava.model.repository.MovimientoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MovimientoServiceImpl implements MovimientoService{
	
	@Autowired
	private MovimientoRepository movrepo;

	@Override
	public Movimiento buscar(Long clave) {
		return movrepo.findById(clave).orElse(null);

	}

	@Override
	public List<Movimiento> buscarTodos() {
		return movrepo.findAll();
	}

	@Override
	public Movimiento insertar(Movimiento movimiento) {
	    return movrepo.save(movimiento);
	}

	@Override
	public Movimiento modificar(Movimiento entidad) {
	    if(movrepo.existsById(entidad.getId())) {
	        return movrepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public int borrar(Long clave) {
		try {
			if(movrepo.existsById(clave)) {
				movrepo.deleteById(clave);
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
	public List<Movimiento> findByMaterialId(Long id) {
		return movrepo.findByMaterialId(id);
	}

}
