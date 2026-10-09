package domain.model;

public class Criptomoeda extends AtivoFinanceiro {

    public Criptomoeda(String ticker,
                       String nome,
                       int qntd,
                       double precoMedio) {
        super(ticker,nome, qntd, precoMedio, TipoAtivo.CRIPTOMOEDA);
    }

    @Override
    public double calcularLucro (double precoVenda) {
        return precoVenda - getPrecoMedio();
    }

    @Override
    public double calcularImposto(double lucro, double totalVendidoNoMes) {
        if (totalVendidoNoMes <= 35000.0 || lucro <= 0.0) {
            return 0.0;
        }
        return lucro * 0.15;
    }
}
