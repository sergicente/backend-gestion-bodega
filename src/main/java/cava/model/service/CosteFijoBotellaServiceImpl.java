package cava.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cava.model.entity.CavaPartida;
import cava.model.entity.CosteFijoBotella;
import cava.model.entity.Deguelle;
import cava.model.repository.CosteFijoBotellaRepository;
import cava.model.repository.DeguelleRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CosteFijoBotellaServiceImpl implements CosteFijoBotellaService {

    @Autowired
    private CosteFijoBotellaRepository cfbrepo;

    private static final Long ID_UNICO = 1L;

    @Override
    public CosteFijoBotella buscar(Long id) {
        if(id == null) {
            return obtenerCosteFijoActual();
        }
        return cfbrepo.findById(id).orElse(null);
    }

    @Override
    public CosteFijoBotella insertar(CosteFijoBotella entidad) {
        if(cfbrepo.existsById(entidad.getId())) {
            throw new IllegalStateException("El coste fijo ya existe, usa modificar");
        }
        return cfbrepo.save(entidad);
    }

    @Override
    public CosteFijoBotella modificar(CosteFijoBotella entidad) {
        if(!cfbrepo.existsById(entidad.getId())) {
            throw new EntityNotFoundException("No existe el coste fijo con id " + entidad.getId());
        }
        return cfbrepo.save(entidad);
    }

    @Override
    public void borrar(Long id) {
        throw new UnsupportedOperationException("No se permite borrar el coste fijo");
    }

    @Override
    public List<CosteFijoBotella> buscarTodos() {
        return cfbrepo.findAll();
    }

    // Métodos propios

    @Override
    public CosteFijoBotella obtenerCosteFijoActual() {
        return cfbrepo.findById(ID_UNICO).orElseGet(() -> {
            CosteFijoBotella nuevo = new CosteFijoBotella();
            nuevo.setId(ID_UNICO);
            nuevo.setCosteFijoBotella(0);
            return cfbrepo.save(nuevo);
        });
    }

    @Override
    public CosteFijoBotella actualizarCosteFijo(double nuevoCoste) {
        CosteFijoBotella coste = obtenerCosteFijoActual();
        coste.setCosteFijoBotella(nuevoCoste);
        return cfbrepo.save(coste);
    }
}