package cava.model.repository;


import java.util.List;

import cava.model.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.CompraMaterial;
import cava.model.entity.MovimientoMaterial;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CompraMaterialRepository extends JpaRepository<CompraMaterial, Long>{
	List<CompraMaterial> findByMaterialId(Long id);
	List<CompraMaterial> findByMaterialIdOrderByFechaDesc(Long materialId);
	List<CompraMaterial> findByProveedorId(Long id);
	@Query("SELECT DISTINCT cm.material FROM CompraMaterial cm WHERE cm.proveedor.id = :proveedorId")
	List<Material> findMaterialesByProveedorId(@Param("proveedorId") Long proveedorId);

}
