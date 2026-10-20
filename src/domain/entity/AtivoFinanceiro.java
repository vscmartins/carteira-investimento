package domain.entity;

import domain.interfaces.Lucravel;
import domain.interfaces.Tributavel;

public abstract class AtivoFinanceiro implements Lucravel, Tributavel {

    private String ticker;
    private String nome;
    private double qntd;
    private double precoMedio;
    private TipoAtivo tipo;

    public AtivoFinanceiro(String ticker,
                           String nome,
                           double qntd,
                           double precoMedio,
                           TipoAtivo tipo) {
        this.ticker = ticker;
        this.nome = nome;
        this.qntd = qntd;
        this.precoMedio = precoMedio;
        this.tipo = tipo;
    }

    public String getTicker() {
        return ticker;
    }
    public String getNome() {
        return nome;
    }
    public double getQntd() {
        return qntd;
    }
    public double getPrecoMedio() {
        return precoMedio;
    }
    public TipoAtivo getTipo() {
        return tipo;
    }

    public void addQuantidade(double qtd, double precoCompra) {
        double valorTotalAtual = this.qntd * this.precoMedio;
        double valorNovaCompra = qtd * precoCompra;

        this.qntd += qtd;
        this.precoMedio = (valorNovaCompra + valorTotalAtual)/ this.qntd;
    }

    public void subtrairQntd(int qtd) {
        this.qntd -= qtd;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Qtd: %d | Preco Medio: R$ %.2f", ticker, nome , qntd, precoMedio);
    }
}
