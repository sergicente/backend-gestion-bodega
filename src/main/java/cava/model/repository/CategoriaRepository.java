package cava.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
	

}
