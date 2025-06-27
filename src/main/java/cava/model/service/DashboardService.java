package cava.model.service;


import cava.model.dto.DashboardResumenDto;
import cava.model.dto.ResumenDeguellesDto;
import org.springframework.beans.factory.annotation.Autowired;

public interface DashboardService {

     DashboardResumenDto generarResumen();

    ResumenDeguellesDto obtenerResumenMensualDeguellades();
}
