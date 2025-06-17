package cava.model.service;


import cava.model.entity.CosteCrianza;
import cava.model.entity.CosteFijoBotella;

public interface CosteCrianzaService extends InterfaceGenericoCrud<CosteCrianza, Long>{

	CosteCrianza obtenerCosteFijoActual();

	CosteCrianza actualizarCosteFijo(double nuevoCoste);
}
