package cava.model.service;

import cava.model.entity.Pedido;
import cava.model.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService{
	
	@Autowired
	private PedidoRepository prepo;

	@Override
	public Pedido buscar(Long clave) {
		return prepo.findById(clave).orElse(null);

	}

	@Override
	public List<Pedido> buscarTodos() {
		// TODO Auto-generated method stub
		return prepo.findAll();
	}

	@Override
	public Pedido insertar(Pedido entidad) {
	    try {
	        return prepo.save(entidad);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	@Override
	public Pedido modificar(Pedido entidad) {
	    if(prepo.existsById(entidad.getId())) {
	        return prepo.save(entidad);
	    } else {
	        throw new EntityNotFoundException("No se encontró la entidad con ID: " + entidad.getId());
	    }
	}

	@Override
	public void borrar(Long clave) {
	    if (prepo.existsById(clave)) {
	        prepo.deleteById(clave);
	    }
	}



}
