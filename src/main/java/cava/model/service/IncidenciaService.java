package cava.model.service;

import cava.model.entity.Incidencia;

import java.util.List;

public interface IncidenciaService extends InterfaceGenericoCrud<Incidencia, Long>{
    List<Incidencia> findByPartidaId(String id);
    List<Incidencia> findByCavaId(String id);

}
