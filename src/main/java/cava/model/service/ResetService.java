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
import cava.model.entity.Familia;
import cava.model.entity.Material;
import cava.model.entity.MaterialCava;
import cava.model.entity.Partida;
import cava.model.repository.CategoriaRepository;
import cava.model.repository.CavaPartidaRepository;
import cava.model.repository.CavaRepository;
import cava.model.repository.DeguelleRepository;
import cava.model.repository.FamiliaRepository;
import cava.model.repository.MaterialCavaRepository;
import cava.model.repository.MaterialRepository;
import cava.model.repository.MovimientoMaterialRepository;
import cava.model.repository.PartidaRepository;

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

    @Transactional
    public void reiniciarBaseDeDatos() {
        // Borrar en el orden correcto por dependencias
        mcRepo.deleteAll(); 
        mmRepo.deleteAll(); 
        matRepo.deleteAll();
        dRepo.deleteAll();
        cavaPartidaRepo.deleteAll();
        cavaRepo.deleteAll();
        partidaRepo.deleteAll();
        fRepo.deleteAll();
        catRepo.deleteAll();
        
        
        // Insertar partidas de ejemplo
        Partida p1 = new Partida("18PINSURO", LocalDate.of(2019, 2, 10), 1000, 0, 0, false, "Verde", "Suro", "Celler Piñol", "50% Xarel·lo", "25% Macabeu", " 25% Parellada", null);
        Partida p2 = new Partida("20PIN", LocalDate.of(2021, 1, 5), 1000, 0, 0, true, "Verde", "Corona", "Celler Piñol", "40% Xarel·lo", " 30% Macabeu", "30% Parellada", null);
        Partida p3 = new Partida("21PIN", LocalDate.of(2022, 2, 18), 1000, 0, 0, true, "Verde", "Corona", "Celler Piñol", "55% Xarel·lo", "25% Macabeu", "20% Parellada", null);
        Partida p4 = new Partida("22SJ", LocalDate.of(2023, 1, 15), 1000, 0, 0, true, "Verde", "Corona", "Cellers Domenys", "35% Xarel·lo", "35% Macabeu", "30% Parellada", null);

        partidaRepo.saveAll(List.of(p1, p2, p3, p4));
        
     // Insertar familias
        Familia f1 = new Familia(1L, "Montsant");
        Familia f2 = new Familia(2L, "Mas Xarot");
        Familia f3 = new Familia(3L, "Roca Gibert");

        fRepo.saveAll(List.of(f1, f2, f3));
        
     // Insertar cavas
        Cava cava1 = new Cava("21", "Mas Xarot Brut", true, f2, null);
        Cava cava2 = new Cava("24", "Mas Xarot Brut Nature", true, f2, null);
        Cava cava3 = new Cava("25", "Mas Xarot Enoteca", false, f2, null);
        Cava cava4 = new Cava("11", "Montsant Artesà", false, f1, null);
        
        cavaRepo.saveAll(List.of(cava1, cava2, cava3, cava4));
        
        
     // Recuperar cavas y partidas existentes
     // Crear relaciones usando las cavas y partidas recién creadas
        CavaPartida r2 = new CavaPartida(null, cava1, p2, 0, 0, false, LocalDateTime.now());
        CavaPartida r3 = new CavaPartida(null, cava1, p3, 0, 0, true, LocalDateTime.now()); // actual
        CavaPartida r4 = new CavaPartida(null, cava2, p2, 0, 0, true, LocalDateTime.now());
        CavaPartida r6 = new CavaPartida(null, cava3, p1, 0, 0, true, LocalDateTime.now());
        CavaPartida r7 = new CavaPartida(null, cava4, p4, 0, 0, true, LocalDateTime.now());
        cavaPartidaRepo.saveAll(List.of(r2, r3, r4, r6, r7));

        Categoria c1 = new Categoria(null, "Cápsulas");
        Categoria c2 = new Categoria(null, "Etiquetas");
        Categoria c3 = new Categoria(null, "Cajas");
        catRepo.saveAll(List.of(c1, c2, c3));
        
        Material m1 = new Material(null, "Cápsula Mas Xarot", c1, f2, null, 10000, 1f);
        Material m2 = new Material(null, "Etiqueta Mas Xarot", c2, f2, null, 10000, 1f);
        Material m3 = new Material(null, "Caja Mas Xarot", c3, f2, null, 1000, 0.17f);
        Material m4 = new Material(null, "Caja Montsant", c3, f1, null, 1000, 0.17f);


        matRepo.saveAll(List.of(m1, m2, m3, m4));
        
     // Mas Xarot Brut
        MaterialCava mc1 = new MaterialCava(null, cava1, m1);        // 1 cápsula por botella
        MaterialCava mc2 = new MaterialCava(null, cava1, m2);        // 1 etiqueta por botella
        MaterialCava mc3 = new MaterialCava(null, cava1, m3);   // 1 caja cada 6 botellas

        // Mas Xarot Brut Nature
        MaterialCava mc4 = new MaterialCava(null, cava2, m1);
        MaterialCava mc5 = new MaterialCava(null, cava2, m2);
        MaterialCava mc6 = new MaterialCava(null, cava2, m3);

        // Mas Xarot Enoteca
        MaterialCava mc7 = new MaterialCava(null, cava3, m1);
        MaterialCava mc8 = new MaterialCava(null, cava3, m2);
        MaterialCava mc9 = new MaterialCava(null, cava3, m3);

        // Montsant Artesà
        MaterialCava mc10 = new MaterialCava(null, cava4, m4);

        mcRepo.saveAll(List.of(mc1, mc2, mc3, mc4, mc5, mc6, mc7, mc8, mc9, mc10));
//        // Insertar datos de ejemplo
//        Partida p1 = new Partida("18pin", LocalDate.now());
//        Partida p2 = new Partida("20pin", LocalDate.now());
//        partidaRepo.saveAll(List.of(p1, p2));
//
//        Cava cava1 = new Cava("Mas Xarot Brut", true, 0);
//        cavaRepo.save(cava1);
//
//        CavaPartida cp1 = new CavaPartida(cava1, p1, 100, true);
//        cavaPartidaRepo.save(cp1);
    }
}