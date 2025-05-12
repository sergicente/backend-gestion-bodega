package cava.model.service;

import java.util.List;

import cava.model.entity.Familia;
import cava.model.entity.Material;

public interface MaterialService extends InterfaceGenericoCrud<Material, Long>{
    List<Material> findByFamilia(Familia familia);

}
