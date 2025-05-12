package cava.model.service;

import java.util.List;

import cava.model.entity.Cava;
import cava.model.entity.Familia;

public interface CavaService extends InterfaceGenericoCrud<Cava, String>{
    List<Cava> findByFamilia(Familia familia);

}
