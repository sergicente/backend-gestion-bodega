package cava.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import cava.model.entity.*;
import cava.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

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

    @Autowired
    private LogRepository lRepo;

    @Autowired
    private UsuarioRepository uRepo;

    @Autowired
    private CosteCrianzaRepository ccRepo;

    @Autowired
    private CosteFijoBotellaRepository cfRepo;

    @Transactional
    public void reiniciarBaseDeDatos() {

        // Coste Fijo
//        cfRepo.deleteAll();
//        // Coste Crianza
//        ccRepo.deleteAll();

        //  Log
        lRepo.deleteAll();
    	
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



        cfRepo.findById(1L).ifPresentOrElse(
                existente -> {
                    existente.setCosteFijoBotella(2.22);
                    cfRepo.save(existente);
                },
                () -> {
                    CosteFijoBotella nuevo = new CosteFijoBotella(1L, 2.22);
                    cfRepo.save(nuevo);
                }
        );

        ccRepo.findById(1L).ifPresentOrElse(
                existente -> {
                    existente.setCosteCrianzaBotella(0.1);
                    ccRepo.save(existente);
                },
                () -> {
                    CosteCrianza nuevo = new CosteCrianza(1L, 0.1);
                    ccRepo.save(nuevo);
                }
        );

        Usuario admin = new Usuario(1L, "Admin", "admin", "admin", Rol.ADMIN);
        uRepo.save(admin);
        
        Familia f1 = new Familia(1L, "Montsant");
        Familia f2 = new Familia(2L, "Mas Xarot");
        Familia f3 = new Familia(3L, "Roca Gibert");
        fRepo.saveAll(List.of(f1, f2, f3));
        
        
        Partida p1 = new Partida("18PINSURO", LocalDate.of(2019, 2, 10), 1000, 0, 0, 0, true, "Verda", "Suro", "Celler Piñol", "50% Xarel·lo", "25% Macabeu", "25% Parellada", null, 2.44);
        Partida p2 = new Partida("20PIN", LocalDate.of(2021, 1, 5), 1000, 0, 0, 0, true, "Verda", "Corona", "Celler Piñol", "40% Xarel·lo", " 30% Macabeu", "30% Parellada", null, 2.01);
        Partida p3 = new Partida("21PIN", LocalDate.of(2022, 2, 18), 1000, 0, 0, 0, true, "Verda", "Corona", "Celler Piñol", "55% Xarel·lo", "25% Macabeu", "20% Parellada", null, 2.01);
        Partida p4 = new Partida("22SJ", LocalDate.of(2023, 1, 15), 1000, 0, 0, 0, false, "Verda", "Corona", "Cellers Domenys", "35% Xarel·lo", "35% Macabeu", "30% Parellada", null, 1.92);
        Partida p5 = new Partida("23ROSAT", LocalDate.of(2024, 2, 5), 1000, 0, 0, 0, true, "Verda", "Corona", "Celler Piñol", "100% Pinot Noir", null, null, null, 2.10);
        Partida p6 = new Partida("21ROSATGR", LocalDate.of(2022, 2, 14), 1000, 0, 0, 0, true, "Verda", "Corona", "Celler Piñol", "100% Pinot Noir", null, null, null, 2.20);

        partidaRepo.saveAll(List.of(p1, p2, p3, p4, p5, p6));
     
    
        Cava cava1 = new Cava("21", "Mas Xarot Brut", true, f2, null, null);
        Cava cava2 = new Cava("23", "Mas Xarot Brut Nature", true, f2, null, null);
        Cava cava3 = new Cava("25", "Mas Xarot Enoteca", true, f2, null, null);
        Cava cava4 = new Cava("22", "Mas Xarot Barcelona", false, f2, null, null);
       Cava cava5 = new Cava("24", "Mas Xarot Pinot Noir", true, f2, null, null);
        cavaRepo.saveAll(List.of(cava1, cava2, cava3, cava4, cava5));
        
        CavaPartida r3 = new CavaPartida(null, cava1, p3, 0, 0, true, LocalDateTime.now());
        CavaPartida r4 = new CavaPartida(null, cava2, p2, 0, 0, true, LocalDateTime.now());
        CavaPartida r6 = new CavaPartida(null, cava3, p1, 0, 0, true, LocalDateTime.now());
        CavaPartida r7 = new CavaPartida(null, cava4, p4, 0, 0, true, LocalDateTime.now());
        CavaPartida r8 = new CavaPartida(null, cava5, p5, 0, 0, true, LocalDateTime.now());
        cavaPartidaRepo.saveAll(List.of(r3, r4, r6, r7, r8));

        
        Categoria c1 = new Categoria(null, "Càpsules");
        Categoria c2 = new Categoria(null, "Etiquetes");
        Categoria c3 = new Categoria(null, "Caixes");
        Categoria c4 = new Categoria(null, "Collarins");
        Categoria c5 = new Categoria(null, "Contraetiquetes");
        Categoria c6 = new Categoria(null, "Taps");
        Categoria c7 = new Categoria(null,"Morrions");
        catRepo.saveAll(List.of(c1, c2, c3, c4, c5,c6, c7));
        
        Proveedor enoplastic = new Proveedor(null, "Enoplastic");
        Proveedor vidal = new Proveedor(null, "Vidal & Armadans");
        Proveedor cartonajes = new Proveedor(null, "Cartonajes Font");
        Proveedor amorin = new Proveedor(null, "Amorin");
        Proveedor sabat = new Proveedor(null, "Sabat");
        prepo.saveAll(List.of(enoplastic, vidal, cartonajes, amorin, sabat));
        
        
        
        Material m1 = new Material(null, "Càpsula", (float)(1605.0 / 30000), c1, f2, null, 30000, 1000, 1f);
        Material m2 = new Material(null, "Etiqueta", (float)(1520.0 / 10000), c2, f2, null, 10000, 1000, 1f);
        Material m3 = new Material(null, "Caixa", (float)(830.0 / 1000), c3, f2, null, 1000, 200, 0.17f);
        Material m4 = new Material(null, "Caixa Montsant", (float)(810.0 / 1000), c3, f1, null, 1000, 200, 0.17f);
        Material m5 = new Material(null, "Collarí", (float)(105.0 / 1000), c4, f2, null, 10000, 1000, 1f);
        Material m6 = new Material(null, "Contraetiqueta", (float)(75.0 / 1000), c5, f2, null, 10000, 1000, 1f);
        Material m7 = new Material(null, "Tap Suro", (float)(450.0 / 3000), c6, f2, null, 3000, 500, 1f);
        Material m8 = new Material(null, "Morrió", (float)(848.04 / 9000), c7, f2, null, 9000, 1000, 1f);

        matRepo.saveAll(List.of(m1, m2, m3, m4, m5, m6, m7, m8));

        
        CompraMaterial compra1 = new CompraMaterial(null, 30000, 1605.0,0.05, "Albarà 1", enoplastic, LocalDateTime.of(2023, 3, 5,10,0), m1);
        CompraMaterial compra2 = new CompraMaterial(null, 10000, 1520.0,0.15, "Albarà 2", vidal, LocalDateTime.of(2024, 5, 11,10,0), m2);
        CompraMaterial compra3 = new CompraMaterial(null, 1000, 830.0,0.83, "Albarà 3", cartonajes, LocalDateTime.of(2025, 4, 3,10,0), m3);
        CompraMaterial compra4 = new CompraMaterial(null, 1000, 1520.0,0.81, "Albarà 4", cartonajes, LocalDateTime.of(2025, 7, 6,10,0), m4);
        CompraMaterial compra5 = new CompraMaterial(null, 10000, 1050.0,0.105, "Albarà 5", vidal, LocalDateTime.of(2024, 5, 11,10,0), m5);
        CompraMaterial compra6 = new CompraMaterial(null, 10000, 750.0,0.075, "Albarà 6", vidal, LocalDateTime.of(2024, 5, 11,10,0), m6);
        CompraMaterial compra7 = new CompraMaterial(null, 3000, 450.0,0.15, "Albarà 7", amorin, LocalDateTime.of(2024, 5, 11,10,0), m7);
        CompraMaterial compra8 = new CompraMaterial(null, 9000, 848.04,0.094, "Albarà 8", sabat, LocalDateTime.of(2024, 5, 11,10,0), m8);

        cmRepo.saveAll(List.of(compra1, compra2, compra3, compra4, compra5, compra6, compra7, compra8));
        
        
        MovimientoMaterial mm1 = new MovimientoMaterial(null, LocalDateTime.of(2023, 3, 5,10,0), TipoMovimientoMaterial.ENTRADA, "Albarán 1", 30000, m1, null, 30000, compra1);
        MovimientoMaterial mm2 = new MovimientoMaterial(null, LocalDateTime.of(2024, 5, 11,10,0), TipoMovimientoMaterial.ENTRADA, "Albarán 2", 10000, m2, null, 10000, compra2);
        MovimientoMaterial mm3 = new MovimientoMaterial(null, LocalDateTime.of(2025, 4, 3,10,0), TipoMovimientoMaterial.ENTRADA, "Albarán 3", 1000, m3, null, 1000, compra3);
        MovimientoMaterial mm4 = new MovimientoMaterial(null, LocalDateTime.of(2025, 7, 6,10,0), TipoMovimientoMaterial.ENTRADA, "Albarán 4", 1000, m4, null, 1000, compra4);
        MovimientoMaterial mm5 = new MovimientoMaterial(null, LocalDateTime.of(2024, 5, 11,10,0), TipoMovimientoMaterial.ENTRADA, "Albarán 5", 10000, m5, null, 10000, compra5);
        MovimientoMaterial mm6 = new MovimientoMaterial(null, LocalDateTime.of(2024, 5, 11,10,0), TipoMovimientoMaterial.ENTRADA, "Albarán 6", 10000, m6, null, 10000, compra6);
        MovimientoMaterial mm7 = new MovimientoMaterial(null, LocalDateTime.of(2024, 5, 11,10,0), TipoMovimientoMaterial.ENTRADA, "Albarán 7", 3000, m7, null, 3000, compra7);
        MovimientoMaterial mm8 = new MovimientoMaterial(null, LocalDateTime.of(2024, 5, 11,10,0), TipoMovimientoMaterial.ENTRADA, "Albarán 8", 9000, m8, null, 9000, compra8);

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
        MaterialCava mc28 = new MaterialCava(null, cava5, m1);
        MaterialCava mc29 = new MaterialCava(null, cava5, m2);
        MaterialCava mc30 = new MaterialCava(null, cava5, m3);
        MaterialCava mc31 = new MaterialCava(null, cava5, m5);
        MaterialCava mc32 = new MaterialCava(null, cava5, m6);
        MaterialCava mc33 = new MaterialCava(null, cava5, m7);
        MaterialCava mc34 = new MaterialCava(null, cava5, m8);

        mcRepo.saveAll(List.of(mc1, mc2, mc3, mc4, mc5, mc6, mc7, mc8, mc9, mc11, mc12, mc13, mc14, mc15, mc16, mc17, mc18, mc19, mc20, mc21, mc22, mc23, mc24, mc25, mc26, mc27, mc28, mc29, mc30, mc31, mc32, mc33, mc34));

    }
}