package cava.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import cava.model.entity.Categoria;
import cava.model.entity.Cava;
import cava.model.entity.CavaPartida;
import cava.model.entity.CompraMaterial;
import cava.model.entity.Familia;
import cava.model.entity.Material;
import cava.model.entity.MaterialCava;
import cava.model.entity.MovimientoMaterial;
import cava.model.entity.Partida;
import cava.model.entity.Proveedor;
import cava.model.entity.TipoMovimientoMaterial;
import cava.model.repository.CategoriaRepository;
import cava.model.repository.CavaPartidaRepository;
import cava.model.repository.CavaRepository;
import cava.model.repository.CompraMaterialRepository;
import cava.model.repository.DeguelleRepository;
import cava.model.repository.FamiliaRepository;
import cava.model.repository.IncidenciaRepository;
import cava.model.repository.MaterialCavaRepository;
import cava.model.repository.MaterialRepository;
import cava.model.repository.MovimientoMaterialRepository;
import cava.model.repository.PartidaRepository;
import cava.model.repository.ProveedorRepository;
import cava.model.repository.VentaRepository;
import jakarta.transaction.Transactional;

@Service
@Profile("dev")
public class ResetService {

    @Autowired
    private CavaRepository cavaRepo;

    @Autowired
    private PartidaRepository partidaRepo;

    @Autowired
    private CavaPartidaRepository cavaPartidaRepo;

    @Autowired
    private MaterialCavaRepository mcRepo;

    @Autowired
    private DeguelleRepository dRepo;
    
    @Autowired
    private MaterialRepository matRepo;
    
    @Autowired
    private MovimientoMaterialRepository mmRepo;
    
    @Autowired
    private FamiliaRepository fRepo;
    
    @Autowired
    private CategoriaRepository catRepo;
    
    @Autowired
    private VentaRepository vRepo;
    
    @Autowired
    private ProveedorRepository prepo;
    
    @Autowired
    private CompraMaterialRepository cmRepo;
    
    @Autowired
    private IncidenciaRepository rRepo;

    @Transactional
    public void reiniciarBaseDeDatos() {
    	
    	// Roturas
    	rRepo.deleteAll();
    	
        // Ventas (no dependen de nadie, pero sí de CavaPartida)
        vRepo.deleteAll();

        // Movimientos (referencian Material y CompraMaterial)
        mmRepo.deleteAll();

        // Relación material-cava (intermedia)
        mcRepo.deleteAll();

        // Compras (referencian Material y Proveedor)
        cmRepo.deleteAll();

        // Degüelles (referenciados en MovimientoMaterial)
        dRepo.deleteAll();

        // Relación cava-partida
        cavaPartidaRepo.deleteAll();

        // Partidas
        partidaRepo.deleteAll();

        // Cavas (dependen de Familia)
        cavaRepo.deleteAll();

        // Materiales (dependen de Categoría y Familia)
        matRepo.deleteAll();

        // Proveedores
        prepo.deleteAll();

        // Categorías
        catRepo.deleteAll();

        // Familias
        fRepo.deleteAll();
        
        
        Familia f1 = new Familia(1L, "Montsant");
        Familia f2 = new Familia(2L, "Mas Xarot");
        Familia f3 = new Familia(3L, "Roca Gibert");
        fRepo.saveAll(List.of(f1, f2, f3));
        
        
        Partida p1 = new Partida("18PINSURO", LocalDate.of(2019, 2, 10), 1000, 0, 0, 0, false, "Verda", "Suro", "Celler Piñol", "50% Xarel·lo", "25% Macabeu", "25% Parellada", null, 2.44);
        Partida p2 = new Partida("20PIN", LocalDate.of(2021, 1, 5), 1000, 0, 0, 0, true, "Verda", "Corona", "Celler Piñol", "40% Xarel·lo", " 30% Macabeu", "30% Parellada", null, 2.01);
        Partida p3 = new Partida("21PIN", LocalDate.of(2022, 2, 18), 1000, 0, 0, 0, true, "Verda", "Corona", "Celler Piñol", "55% Xarel·lo", "25% Macabeu", "20% Parellada", null, 2.01);
        Partida p4 = new Partida("22SJ", LocalDate.of(2023, 1, 15), 1000, 0, 0, 0, true, "Verda", "Corona", "Cellers Domenys", "35% Xarel·lo", "35% Macabeu", "30% Parellada", null, 1.92);
        Partida p5 = new Partida("23ROSAT", LocalDate.of(2024, 2, 5), 1000, 0, 0, 0, true, "Verda", "Corona", "Celler Piñol", "100% Pinot Noir", "", "2", null, 2.10);
        Partida p6 = new Partida("21ROSATGR", LocalDate.of(2022, 2, 14), 1000, 0, 0, 0, true, "Verda", "Corona", "Celler Piñol", "100% Pinot Noir", "", "", null, 2.20);

        partidaRepo.saveAll(List.of(p1, p2, p3, p4, p5, p6));
     
    
        Cava cava1 = new Cava("21", "Mas Xarot Brut", true, f2, null, null);
        Cava cava2 = new Cava("24", "Mas Xarot Brut Nature", true, f2, null, null);
        Cava cava3 = new Cava("25", "Mas Xarot Enoteca", false, f2, null, null);
        Cava cava4 = new Cava("22", "Mas Xarot Barcelona", false, f2, null, null);
        cavaRepo.saveAll(List.of(cava1, cava2, cava3, cava4));
        
        CavaPartida r1 = new CavaPartida(null, cava1, p1, 0, 0, false, LocalDateTime.now());
        CavaPartida r2 = new CavaPartida(null, cava1, p2, 0, 0, false, LocalDateTime.now());
        CavaPartida r3 = new CavaPartida(null, cava1, p3, 0, 0, true, LocalDateTime.now());
        CavaPartida r4 = new CavaPartida(null, cava2, p2, 0, 0, true, LocalDateTime.now());
        CavaPartida r6 = new CavaPartida(null, cava3, p1, 0, 0, true, LocalDateTime.now());
        CavaPartida r7 = new CavaPartida(null, cava4, p4, 0, 0, true, LocalDateTime.now());
        cavaPartidaRepo.saveAll(List.of(r1, r2, r3, r4, r6, r7));

        
        Categoria c1 = new Categoria(null, "Cápsulas");
        Categoria c2 = new Categoria(null, "Etiquetas");
        Categoria c3 = new Categoria(null, "Cajas");
        Categoria c4 = new Categoria(null, "Collarínes");
        Categoria c5 = new Categoria(null, "Contraetiquetas");
        Categoria c6 = new Categoria(null, "Taps");
        Categoria c7 = new Categoria(null,"Morrions");
        catRepo.saveAll(List.of(c1, c2, c3, c4, c5,c6, c7));
        
        Proveedor enoplastic = new Proveedor(null, "Enoplastic");
        Proveedor vidal = new Proveedor(null, "Vidal & Armadans");
        Proveedor cartonajes = new Proveedor(null, "Cartonajes Font");
        Proveedor amorin = new Proveedor(null, "Amorin");
        Proveedor sabat = new Proveedor(null, "Sabat");
        prepo.saveAll(List.of(enoplastic, vidal, cartonajes, amorin, sabat));
        
        
        
        Material m1 = new Material(null, "Cápsula Mas Xarot", (float)(1605.0 / 30000), c1, f2, null, 30000, 1000, 1f);
        Material m2 = new Material(null, "Etiqueta Mas Xarot", (float)(1520.0 / 10000), c2, f2, null, 10000, 1000, 1f);
        Material m3 = new Material(null, "Caja Mas Xarot", (float)(830.0 / 1000), c3, f2, null, 1000, 200, 0.17f);
        Material m4 = new Material(null, "Caja Montsant", (float)(810.0 / 1000), c3, f1, null, 1000, 200, 0.17f);
        Material m5 = new Material(null, "Collarín Mas Xarot", (float)(105.0 / 1000), c4, f2, null, 10000, 1000, 1f);
        Material m6 = new Material(null, "Contraetiqueta Mas Xarot", (float)(75.0 / 1000), c5, f2, null, 10000, 1000, 1f);
        Material m7 = new Material(null, "Tap Suro", (float)(450.0 / 3000), c6, f2, null, 3000, 500, 1f);
        Material m8 = new Material(null, "Morrió Mas Xarot", (float)(848.04 / 9000), c7, f2, null, 9000, 1000, 1f);

        matRepo.saveAll(List.of(m1, m2, m3, m4, m5, m6, m7, m8));

        
        CompraMaterial compra1 = new CompraMaterial(null, 30000, 1605.0,0.05, "Albarán 1", enoplastic, LocalDate.of(2023, 3, 5), m1);
        CompraMaterial compra2 = new CompraMaterial(null, 10000, 1520.0,0.15, "Albarán 2", vidal, LocalDate.of(2024, 5, 11), m2);
        CompraMaterial compra3 = new CompraMaterial(null, 1000, 830.0,0.83, "Albarán 3", cartonajes, LocalDate.of(2025, 4, 3), m3);
        CompraMaterial compra4 = new CompraMaterial(null, 1000, 1520.0,0.81, "Albarán 4", cartonajes, LocalDate.of(2025, 7, 6), m4);
        CompraMaterial compra5 = new CompraMaterial(null, 10000, 1050.0,0.105, "Albarán 5", vidal, LocalDate.of(2024, 5, 11), m5);
        CompraMaterial compra6 = new CompraMaterial(null, 10000, 750.0,0.075, "Albarán 6", vidal, LocalDate.of(2024, 5, 11), m6);
        CompraMaterial compra7 = new CompraMaterial(null, 3000, 450.0,0.15, "Albarán 7", amorin, LocalDate.of(2024, 5, 11), m7);
        CompraMaterial compra8 = new CompraMaterial(null, 9000, 848.04,0.094, "Albarán 8", amorin, LocalDate.of(2024, 5, 11), m8);

        cmRepo.saveAll(List.of(compra1, compra2, compra3, compra4, compra5, compra6, compra7, compra8));
        
        
        MovimientoMaterial mm1 = new MovimientoMaterial(null, LocalDate.of(2023, 3, 5), TipoMovimientoMaterial.ENTRADA, "Albarán 1", 30000, m1, null, 30000, compra1);
        MovimientoMaterial mm2 = new MovimientoMaterial(null, LocalDate.of(2024, 5, 11), TipoMovimientoMaterial.ENTRADA, "Albarán 2", 10000, m2, null, 10000, compra2);
        MovimientoMaterial mm3 = new MovimientoMaterial(null, LocalDate.of(2025, 4, 3), TipoMovimientoMaterial.ENTRADA, "Albarán 3", 1000, m3, null, 1000, compra3);
        MovimientoMaterial mm4 = new MovimientoMaterial(null, LocalDate.of(2025, 7, 6), TipoMovimientoMaterial.ENTRADA, "Albarán 4", 1000, m4, null, 1000, compra4);
        MovimientoMaterial mm5 = new MovimientoMaterial(null, LocalDate.of(2024, 5, 11), TipoMovimientoMaterial.ENTRADA, "Albarán 5", 10000, m5, null, 10000, compra5);
        MovimientoMaterial mm6 = new MovimientoMaterial(null, LocalDate.of(2024, 5, 11), TipoMovimientoMaterial.ENTRADA, "Albarán 6", 10000, m6, null, 10000, compra6);
        MovimientoMaterial mm7 = new MovimientoMaterial(null, LocalDate.of(2024, 5, 11), TipoMovimientoMaterial.ENTRADA, "Albarán 7", 3000, m7, null, 3000, compra7);
        MovimientoMaterial mm8 = new MovimientoMaterial(null, LocalDate.of(2024, 5, 11), TipoMovimientoMaterial.ENTRADA, "Albarán 8", 9000, m8, null, 9000, compra7);

        mmRepo.saveAll(List.of(mm1, mm2, mm3, mm4, mm5, mm6, mm7, mm8));


        
        MaterialCava mc1 = new MaterialCava(null, cava1, m1);
        MaterialCava mc2 = new MaterialCava(null, cava1, m2);
        MaterialCava mc3 = new MaterialCava(null, cava1, m3);
        MaterialCava mc4 = new MaterialCava(null, cava2, m1);
        MaterialCava mc5 = new MaterialCava(null, cava2, m2);
        MaterialCava mc6 = new MaterialCava(null, cava2, m3);
        MaterialCava mc7 = new MaterialCava(null, cava3, m1);
        MaterialCava mc8 = new MaterialCava(null, cava3, m2);
        MaterialCava mc9 = new MaterialCava(null, cava3, m3);
        MaterialCava mc11 = new MaterialCava(null, cava1, m5);
        MaterialCava mc12 = new MaterialCava(null, cava2, m5);
        MaterialCava mc13 = new MaterialCava(null, cava3, m5);
        MaterialCava mc14 = new MaterialCava(null, cava1, m6);
        MaterialCava mc15 = new MaterialCava(null, cava2, m6);
        MaterialCava mc16 = new MaterialCava(null, cava3, m6);
        MaterialCava mc17 = new MaterialCava(null, cava1, m7);
        MaterialCava mc18 = new MaterialCava(null, cava2, m7);
        MaterialCava mc19 = new MaterialCava(null, cava3, m7);
        MaterialCava mc20 = new MaterialCava(null, cava1, m8);
        MaterialCava mc21 = new MaterialCava(null, cava2, m8);
        MaterialCava mc22 = new MaterialCava(null, cava3, m8);
        MaterialCava mc23 = new MaterialCava(null, cava4, m1);
        MaterialCava mc24 = new MaterialCava(null, cava4, m2);
        MaterialCava mc25 = new MaterialCava(null, cava4, m3);
        MaterialCava mc26 = new MaterialCava(null, cava4, m7);
        MaterialCava mc27 = new MaterialCava(null, cava4, m8);

        mcRepo.saveAll(List.of(mc1, mc2, mc3, mc4, mc5, mc6, mc7, mc8, mc9, mc11, mc12, mc13, mc14, mc15, mc16, mc17, mc18, mc19, mc20, mc21, mc22, mc23, mc24, mc25, mc26, mc27));

    }
}