package cava.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cava.model.entity.Cava;

public interface CavaRepository extends JpaRepository<Cava, String>{

}
