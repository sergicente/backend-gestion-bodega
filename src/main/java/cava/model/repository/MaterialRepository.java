package cava.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import cava.model.entity.Familia;
import cava.model.entity.Material;

public interface MaterialRepository extends JpaRepository<Material, Long>{
    List<Material> findByFamilia(Familia familia);

}
