package cava.model.service;

import java.util.List;

import cava.model.entity.CompraMaterial;
import cava.model.entity.Material;

public interface CompraMaterialService extends InterfaceGenericoCrud<CompraMaterial, Long>{
	List<CompraMaterial> findByMaterialId(Long id);
	List<CompraMaterial> findByMaterialIdOrderByFechaDesc(Long materialId);
	List<CompraMaterial> findByProveedorId(Long id);
	List<Material> obtenerMaterialesPorProveedor(Long proveedorId);
}
