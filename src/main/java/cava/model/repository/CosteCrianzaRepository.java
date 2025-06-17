package cava.model.repository;


import cava.model.entity.CosteCrianza;
import cava.model.entity.CosteFijoBotella;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CosteCrianzaRepository extends JpaRepository<CosteCrianza, Long>{
}
