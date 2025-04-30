package cava.model.service;

import java.util.List;

import cava.model.entity.MaterialCava;

public interface MaterialCavaService extends InterfaceGenericoCrud<MaterialCava, Integer>{
	List<MaterialCava>findByCavaId(String cavaId);
}
