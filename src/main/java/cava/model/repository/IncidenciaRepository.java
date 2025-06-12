package cava.model.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.CompraMaterial;
import cava.model.entity.Incidencia;
import cava.model.entity.Proveedor;


public interface IncidenciaRepository extends JpaRepository<Incidencia, Long>{


}
