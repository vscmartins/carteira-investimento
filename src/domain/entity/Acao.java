package domain.entity;

public class Acao extends AtivoFinanceiro {

    public Acao(String ticker, String nome, int qntd, double precoMedio) {
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
