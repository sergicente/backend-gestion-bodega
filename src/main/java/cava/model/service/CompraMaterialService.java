package cava.model.service;

import java.util.List;

import cava.model.entity.CompraMaterial;
import cava.model.entity.MovimientoMaterial;
import cava.model.entity.Venta;

public interface CompraMaterialService extends InterfaceGenericoCrud<CompraMaterial, Long>{
	List<CompraMaterial> findByMaterialId(Long id);

}
