package domain.model;

import domain.interfaces.Lucravel;
import domain.interfaces.Tributavel;

public abstract class AtivoFinanceiro implements Lucravel, Tributavel {

    private String ticket;
    private String nome;
    private int qntd;
    private double precoMedio;
    private TipoAtivo tipo;

    public AtivoFinanceiro(String ticket,
                           String nome,
                           int qntd,
                           double precoMedio,
                           TipoAtivo tipo) {
        this.ticket = ticket;
        this.nome = nome;
        this.qntd = qntd;
        this.precoMedio = precoMedio;
        this.tipo = tipo;
    }

    public String getTicket() {
        return ticket;
    }
    public String getNome() {
        return nome;
    }
    public int getQntd() {
        return qntd;
    }
    public double getPrecoMedio() {
        return precoMedio;
    }
    public TipoAtivo getTipo() {
        return tipo;
    }

    public void addQuantidade(int qtd, double precoCompra) {
        double valorTotalAtual = this.qntd * precoMedio;
        double valorNovaCompra = qtd * precoCompra;

        this.qntd += qtd;
        this.precoMedio += (valorNovaCompra + valorTotalAtual)/ this.qntd;
    }

    public void subtrairQntd(int qtd) {
        this.qntd -= qtd;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Qtd: %d | Preco Medio: R$ %.2f", ticket, nome , qntd, precoMedio);
    }
}
