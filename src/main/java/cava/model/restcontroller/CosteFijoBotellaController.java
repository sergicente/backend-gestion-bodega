package cava.model.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cava.model.entity.CosteFijoBotella;
import cava.model.service.CosteFijoBotellaService;

@RestController
@RequestMapping("/api/coste-fijo")
public class CosteFijoBotellaController {

    @Autowired
    private CosteFijoBotellaService costeService;

    // Obtener el coste fijo unitario actual
   
    @GetMapping
    public ResponseEntity<CosteFijoBotella> getCosteFijo() {
        CosteFijoBotella coste = costeService.obtenerCosteFijoActual();
        return ResponseEntity.ok(coste);
    }

    // Actualizar el coste fijo unitario
    @PutMapping
    public ResponseEntity<CosteFijoBotella> actualizarCosteFijo(@RequestBody CosteFijoBotella nuevoCoste) {
        CosteFijoBotella actualizado = costeService.modificar(nuevoCoste);
        return ResponseEntity.ok(actualizado);
    }
}