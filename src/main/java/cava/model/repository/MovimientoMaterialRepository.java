package cava.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.MovimientoMaterial;

public interface MovimientoMaterialRepository extends JpaRepository<MovimientoMaterial, Long>{
	List<MovimientoMaterial> findByMaterialId(Long id);
}
