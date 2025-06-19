package cava.model.service;

import cava.model.entity.Log;

public interface LogService extends InterfaceGenericoCrud<Log, Long>{
    void registrar(String evento);
}
