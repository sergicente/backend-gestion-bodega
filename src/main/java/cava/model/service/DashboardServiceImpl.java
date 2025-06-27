package cava.model.service;

import cava.model.dto.DashboardResumenDto;
import cava.model.dto.ResumenDeguellesDto;
import cava.model.entity.Deguelle;
import cava.model.entity.Partida;
import cava.model.repository.DeguelleRepository;
import cava.model.repository.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.TreeMap;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService{


	@Autowired
	private PartidaRepository pRepo;

	@Autowired
	private DeguelleRepository dRepo;

	@Override
	public DashboardResumenDto generarResumen() {
		int totalRima = calcularTotalBotellasEnRima();
		double crianzaMedia = calcularCrianzaMedia();
		int salidas = calcularProduccion();
		int salidasP = calcularProduccionPasada();
		int embotellada = calcularEmbotellada();
		int embotelladaP = calcularEmbotelladaPasada();
		return new DashboardResumenDto(totalRima, salidas, salidasP, embotellada, embotelladaP, crianzaMedia);
	}

	@Override
	public ResumenDeguellesDto obtenerResumenMensualDeguellades() {
	    LocalDateTime hoy = LocalDateTime.now();
	    LocalDateTime hace12Meses = hoy.minusMonths(11).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
	    List<Deguelle> deguelles = dRepo.findByFechaBetween(hace12Meses, hoy);

	    TreeMap<YearMonth, Integer> resumenPorMes = new TreeMap<>();
	    YearMonth inicio = YearMonth.from(hace12Meses);
	    for (int i = 0; i < 12; i++) {
	        resumenPorMes.put(inicio.plusMonths(i), 0);
	    }

	    for (Deguelle d : deguelles) {
	        YearMonth ym = YearMonth.from(d.getFecha());
	        if (resumenPorMes.containsKey(ym)) {
                resumenPorMes.compute(ym, (k, cantidadActual) -> cantidadActual + d.getCantidad());
	        } else {
	            resumenPorMes.put(ym, d.getCantidad());
	        }
	    }

	    List<String> meses = new java.util.ArrayList<>();
	    List<Integer> cantidades = new java.util.ArrayList<>();
	    for (Map.Entry<YearMonth, Integer> entry : resumenPorMes.entrySet()) {
	        YearMonth ym = entry.getKey();
	        int cantidad = entry.getValue();
	        meses.add(ym.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("es")));
	        cantidades.add(cantidad);
	    }

	    return new ResumenDeguellesDto(meses, cantidades);
	}

	private double calcularCrianzaMedia() {
		LocalDate avui = LocalDate.now();
		List<Partida> partidas = pRepo.findAll();

		double sumaPonderada = 0;
		int totalBotellasRima = 0;

		for (Partida p : partidas) {
			int botellasRima = p.getBotellasRima();
			if (botellasRima <= 0) continue; // Saltamos partidas sin botellas en rima

			long dies = ChronoUnit.DAYS.between(p.getFechaEmbotellado(), avui);
			double mesos = dies / 30.44;

			sumaPonderada += mesos * botellasRima;
			totalBotellasRima += botellasRima;
		}

		if (totalBotellasRima == 0) return 0.0;

		return sumaPonderada / totalBotellasRima;
	}

	private int calcularEmbotellada() {
		int anyActual = LocalDate.now().getYear();
		LocalDate inicioAnyo = LocalDate.of(anyActual -1, 6, 1);
		LocalDate hoy = LocalDate.of(anyActual, 6, 1);

		List<Partida> embotellada = pRepo.findByFechaEmbotelladoBetween(inicioAnyo, hoy);

		int totalProduccio = 0;
		for(Partida p: embotellada){
			totalProduccio += p.getBotellasMerma()+p.getBotellasRima()+p.getBotellasStock()+p.getBotellasVendidas();
		}
		return totalProduccio;
	}

	private int calcularEmbotelladaPasada() {
		int anyActual = LocalDate.now().getYear();
		LocalDate inicioAnyo = LocalDate.of(anyActual -2, 6, 1);
		LocalDate hoy = LocalDate.of(anyActual-1, 6, 1);

		List<Partida> embotellada = pRepo.findByFechaEmbotelladoBetween(inicioAnyo, hoy);

		int totalProduccio = 0;
		for(Partida p: embotellada){
			totalProduccio += p.getBotellasMerma()+p.getBotellasRima()+p.getBotellasStock()+p.getBotellasVendidas();
		}
		return totalProduccio;
	}

	private int calcularProduccion() {
		int anyActual = LocalDate.now().getYear();
		LocalDateTime inicioAnyo = LocalDate.of(anyActual, 1, 1).atStartOfDay();
		LocalDateTime hoy = LocalDate.now().atTime(23, 59, 59);
		List<Deguelle> deguelles = dRepo.findByFechaBetween(inicioAnyo, hoy);
		int totalProduccio = 0;
		for(Deguelle d: deguelles){
			totalProduccio += d.getCantidad();
		}
		return totalProduccio;
	}

	private int calcularProduccionPasada() {
		LocalDate haceUnAnoHoy = LocalDate.now().minusYears(1);
		LocalDateTime inicioAnyoPasado = haceUnAnoHoy.withDayOfYear(1).atStartOfDay();
		LocalDateTime finHastaHoyMismo = haceUnAnoHoy.atTime(23, 59, 59);
		List<Deguelle> deguelles = dRepo.findByFechaBetween(inicioAnyoPasado, finHastaHoyMismo);
		int totalProduccio = 0;
		for (Deguelle d : deguelles) {
			totalProduccio += d.getCantidad();
		}
		return totalProduccio;
	}

	private int calcularTotalBotellasEnRima() {
		int total = 0;
		List<Partida> partidas = pRepo.findAll();
		for(Partida p : partidas){
			total += p.getBotellasRima();
		}
		return total;
	}
}
