package cava.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.MaterialCava;

public interface MaterialCavaRepository extends JpaRepository<MaterialCava, Integer>{
	List<MaterialCava> findByCavaId(String cavaId);
}
