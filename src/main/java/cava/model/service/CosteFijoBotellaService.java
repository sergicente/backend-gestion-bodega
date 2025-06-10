package cava.model.service;


import cava.model.entity.CosteFijoBotella;

public interface CosteFijoBotellaService extends InterfaceGenericoCrud<CosteFijoBotella, Long>{

	CosteFijoBotella obtenerCosteFijoActual();

	CosteFijoBotella actualizarCosteFijo(double nuevoCoste);
}
