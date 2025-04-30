package cava.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.Jaula;
import cava.model.entity.Material;

public interface MaterialRepository extends JpaRepository<Material, Integer>{

}
