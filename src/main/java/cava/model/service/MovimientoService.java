package cava.model.service;

import java.util.List;

import cava.model.entity.Movimiento;

public interface MovimientoService extends InterfaceGenericoCrud<Movimiento, Long>{
	List<Movimiento> findByMaterialId(Long id);

}
