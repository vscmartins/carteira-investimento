package domain.dto;

public record ResumoCarteiraDTO(
        int quantidadeTotalAtivos,
        double valorTotalAlocado,
        double saldoDisponivel
) {}