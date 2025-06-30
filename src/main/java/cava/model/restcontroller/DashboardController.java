package cava.model.restcontroller;

import cava.model.dto.DashboardGraficoCavasDto;
import cava.model.dto.DashboardResumenDto;
import cava.model.dto.ResumenDeguellesDto;
import cava.model.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {


    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDto> obtenerResumen() {
        return  ResponseEntity.ok(dashboardService.generarResumen());
    }

    @GetMapping("/deguelles")
    public ResumenDeguellesDto getResumenMensualDeguellades() {
        return dashboardService.obtenerResumenMensualDeguellades();
    }

    @GetMapping("/deguelles-por-cava")
    public List<DashboardGraficoCavasDto> getDeguellesPorCavaEsteAnyo() {
        return dashboardService.obtenerDeguelladosPorCavaEsteAnyo();
    }
}
