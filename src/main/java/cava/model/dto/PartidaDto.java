package cava.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartidaDto {
    private String id;
    private String fechaEmbotellado;
    private int botellasRima;
    private int botellasStock;
    private int botellasVendidas;
    private int botellasMerma;
    private int botellasRotas;
    private boolean ecologico;
    private String botella;
    private String tapon;
    private String proveedor;
    private String variedad1;
    private String variedad2;
    private String variedad3;
    private String variedad4;
    private double costeBotella;

}
