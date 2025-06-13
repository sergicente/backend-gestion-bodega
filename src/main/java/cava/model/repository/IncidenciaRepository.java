package cava.model.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.CompraMaterial;
import cava.model.entity.Incidencia;
import cava.model.entity.Proveedor;

import java.util.List;


public interface IncidenciaRepository extends JpaRepository<Incidencia, Long>{
    List<Incidencia> findByPartidaId(String id);
    List<Incidencia> findByCavaId(String id);


}
