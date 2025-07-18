package cava.model.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cava.model.entity.Cava;
import cava.model.entity.Familia;

public interface CavaRepository extends JpaRepository<Cava, String>{
    List<Cava> findByFamilia(Familia familia);

}
