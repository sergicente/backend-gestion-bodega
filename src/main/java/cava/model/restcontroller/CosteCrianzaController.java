package cava.model.restcontroller;

import cava.model.entity.CosteCrianza;
import cava.model.entity.CosteFijoBotella;
import cava.model.service.CosteCrianzaService;
import cava.model.service.CosteFijoBotellaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coste-crianza")
public class CosteCrianzaController {

    @Autowired
    private CosteCrianzaService costeService;

    // Obtener el coste fijo unitario actual
   
    @GetMapping
    public ResponseEntity<CosteCrianza> getCosteFijo() {
        CosteCrianza coste = costeService.obtenerCosteFijoActual();
        return ResponseEntity.ok(coste);
    }

    // Actualizar el coste fijo unitario
    @PutMapping
    public ResponseEntity<CosteCrianza> actualizarCosteFijo(@RequestBody CosteCrianza nuevoCoste) {
        CosteCrianza actualizado = costeService.modificar(nuevoCoste);
        return ResponseEntity.ok(actualizado);
    }
}