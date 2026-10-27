package domain.entity;

import domain.interfaces.Tributavel;

public class Acao extends AtivoFinanceiro implements Tributavel {

    public Acao(String ticker, String nome, double qntd, double precoMedio) {
        super(
                ticker,
                nome,
                qntd,
                precoMedio,
                TipoAtivo.ACAO);
    }

    @Override
    public double calcularLucro(double precoVenda) {
        return precoVenda - getPrecoMedio();
    }

    @Override
    public double calcularImposto (double lucro, double totalVendidoNoMes){
        if (totalVendidoNoMes <= 20000.0 || lucro <= 0){
            return 0.0;
        }
        return lucro * 0.15;
    }
}
