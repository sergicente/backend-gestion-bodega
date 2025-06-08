package cava.model.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.CompraMaterial;
import cava.model.entity.MovimientoMaterial;


public interface CompraMaterialRepository extends JpaRepository<CompraMaterial, Long>{
	List<CompraMaterial> findByMaterialId(Long id);
	List<CompraMaterial> findByMaterialIdOrderByFechaDesc(Long materialId);

}
