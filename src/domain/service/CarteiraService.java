package domain.service;

import domain.entity.AtivoFinanceiro;
import domain.entity.Investidor;
import domain.entity.TipoAtivo;
import domain.dto.ResumoCarteiraDTO;
import domain.exception.AtivoNaoEncontradoException;
import domain.exception.SaldoInsuficienteException;
import domain.repository.AtivoRepository;
import domain.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.IO.*;

public class CarteiraService {

    private List<AtivoFinanceiro> ativos = new ArrayList<>();
    private final UsuarioRepository usuarioRepository;
    private final AtivoRepository ativoRepository;
    private int usuarioIdAtual;

    private double totalVendidoMesAcoes = 0.0;
    private double totalVendidoMesCripto = 0.0;

    public CarteiraService() {
        this.usuarioRepository = new UsuarioRepository();
        this.ativoRepository = new AtivoRepository();
    }

    public void cadastrarInvestidor(String nome, String cpf, String email, LocalDate dataNascimento) {
        Investidor investidor = new Investidor(nome, cpf, email, dataNascimento);
        usuarioRepository.inserir(investidor);
        this.usuarioIdAtual = investidor.getId();
        println("Investidor processado com sucesso pelo serviço: " + investidor);
    }

    public void comprar(AtivoFinanceiro novoAtivo) {
        ativoRepository.inserirAtivo(usuarioIdAtual, novoAtivo);
        for (AtivoFinanceiro ativoNaCarteira : ativos) {
            if (ativoNaCarteira.getTicker().equals(novoAtivo.getTicker())) {
                ativoNaCarteira.addQuantidade(novoAtivo.getQntd(), novoAtivo.getPrecoMedio());
                return;
            }
        }
        ativos.add(novoAtivo);
    }

    public List<AtivoFinanceiro> getAtivos() {
        return ativos;
    }

    public AtivoFinanceiro buscarAtivo(String ticker) {
        for (AtivoFinanceiro ativo : ativos) {
            if (ativo.getTicker().equals(ticker)) {
                return ativo;
            }
        }
        throw new AtivoNaoEncontradoException("Erro: Ativo " + ticker + " não encontrado na carteira.");
    }

    public void vender(String ticker, int quantidade, double precoVenda) {
        AtivoFinanceiro ativo = buscarAtivo(ticker);

        if (ativo.getQntd() < quantidade) {
            throw new SaldoInsuficienteException("Erro: Você tem apenas " + ativo.getQntd() + " cotas de " + ticker + ".");
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