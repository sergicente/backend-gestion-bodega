package cava.model.service;


import cava.model.dto.DashboardGraficoCavasDto;
import cava.model.dto.DashboardResumenDto;
import cava.model.dto.ResumenDeguellesDto;

import java.util.List;

public interface DashboardService {

     DashboardResumenDto generarResumen();
    List<DashboardGraficoCavasDto> obtenerDeguelladosPorCavaEsteAnyo();
    ResumenDeguellesDto obtenerResumenMensualDeguellades();
}
