package cava.model.service;

import java.util.List;

import cava.model.entity.MovimientoMaterial;

public interface MovimientoMaterialService extends InterfaceGenericoCrud<MovimientoMaterial, Long>{
	List<MovimientoMaterial> findByMaterialId(Long id);

}
