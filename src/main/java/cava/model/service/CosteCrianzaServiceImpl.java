package cava.model.service;

import cava.model.entity.CosteCrianza;
import cava.model.repository.CosteCrianzaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosteCrianzaServiceImpl implements CosteCrianzaService {

    @Autowired
    private CosteCrianzaRepository ccbrepo;

    private static final Long ID_UNICO = 1L;

    @Override
    public CosteCrianza buscar(Long id) {
        if(id == null) {
            return obtenerCosteFijoActual();
        }
        return ccbrepo.findById(id).orElse(null);
    }

    @Override
    public CosteCrianza insertar(CosteCrianza entidad) {
        if(ccbrepo.existsById(entidad.getId())) {
            throw new IllegalStateException("El coste fijo ya existe, usa modificar");
        }
        return ccbrepo.save(entidad);
    }

    @Override
    public CosteCrianza modificar(CosteCrianza entidad) {
        if(!ccbrepo.existsById(entidad.getId())) {
            throw new EntityNotFoundException("No existe el coste fijo con id " + entidad.getId());
        }
        return ccbrepo.save(entidad);
    }

    @Override
    public void borrar(Long id) {
        throw new UnsupportedOperationException("No se permite borrar el coste de crianza");
    }

    @Override
    public List<CosteCrianza> buscarTodos() {
        return ccbrepo.findAll();
    }

    // Métodos propios

    @Override
    public CosteCrianza obtenerCosteFijoActual() {
        return ccbrepo.findById(ID_UNICO).orElseGet(() -> {
            CosteCrianza nuevo = new CosteCrianza();
            nuevo.setId(ID_UNICO);
            nuevo.setCosteCrianzaBotella(0);
            return ccbrepo.save(nuevo);
        });
    }

    @Override
    public CosteCrianza actualizarCosteFijo(double nuevoCoste) {
        CosteCrianza coste = obtenerCosteFijoActual();
        coste.setCosteCrianzaBotella(nuevoCoste);
        return ccbrepo.save(coste);
    }
}