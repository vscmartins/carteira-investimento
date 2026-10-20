package domain.service;

import domain.entity.AtivoFinanceiro;
import domain.entity.TipoAtivo;
import domain.dto.ResumoCarteiraDTO;
import domain.exception.AtivoNaoEncontradoException;
import domain.exception.SaldoInsuficienteException;

import java.util.ArrayList;
import java.util.List;

public class CarteiraService {

    private List<AtivoFinanceiro> ativos = new ArrayList<>();

    private double totalVendidoMesAcoes = 0.0;
    private double totalVendidoMesCripto = 0.0;


    public void comprar(AtivoFinanceiro novoAtivo) {
        for (AtivoFinanceiro ativoNaCarteira : ativos) {
            if (ativoNaCarteira.getTicket().equals(novoAtivo.getTicket())) {
                ativoNaCarteira.addQuantidade(novoAtivo.getQntd(), novoAtivo.getPrecoMedio());
                return;
            }
        }
        ativos.add(novoAtivo);
    }

    public List<AtivoFinanceiro> getAtivos() {
        return ativos;
    }

    public AtivoFinanceiro buscarAtivo(String ticket) {
        for (AtivoFinanceiro ativo : ativos) {
            if (ativo.getTicket().equals(ticket)) {
                return ativo;
            }
        }
        throw new AtivoNaoEncontradoException("Erro: Ativo " + ticket + " não encontrado na carteira.");
    }

    public void vender(String ticket, int quantidade, double precoVenda) {
        AtivoFinanceiro ativo = buscarAtivo(ticket);

        if (ativo.getQntd() < quantidade) {
            throw new SaldoInsuficienteException("Erro: Você tem apenas " + ativo.getQntd() + " cotas de " + ticket + ".");
        }

        ativo.subtrairQntd(quantidade);

        double volumeDaVenda = quantidade * precoVenda;
        if (ativo.getTipo() == TipoAtivo.ACAO) {
            totalVendidoMesAcoes += volumeDaVenda;
        } else {
            totalVendidoMesCripto += volumeDaVenda;
        }
    }

    public ResumoCarteiraDTO obterResumo() {
        double valorTotal = 0.0;
        for (AtivoFinanceiro ativo : ativos) {
            valorTotal += (ativo.getQntd() * ativo.getPrecoMedio());
        }
        return new ResumoCarteiraDTO(ativos.size(), valorTotal);
    }
}