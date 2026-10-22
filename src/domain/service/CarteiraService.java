package domain.service;

import domain.entity.AtivoFinanceiro;
import domain.entity.Investidor;
import domain.entity.TipoAtivo;
import domain.dto.ResumoCarteiraDTO;
import domain.exception.AtivoNaoEncontradoException;
import domain.exception.SaldoInsuficienteException;
import domain.repository.AtivoRepository;
import domain.repository.MovimentacaoRepository;
import domain.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

import static java.lang.IO.*;

public class CarteiraService {

    private final UsuarioRepository usuarioRepository;
    private final AtivoRepository ativoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private int usuarioIdAtual;

    private double totalVendidoMesAcoes = 0.0;
    private double totalVendidoMesCripto = 0.0;

    public CarteiraService() {
        this.usuarioRepository = new UsuarioRepository();
        this.ativoRepository = new AtivoRepository();
        this.movimentacaoRepository = new MovimentacaoRepository();
    }

    public void cadastrarInvestidor(String nome, String cpf, String email, LocalDate dataNascimento) {
        Investidor investidor = new Investidor(nome, cpf, email, dataNascimento);
        usuarioRepository.inserir(investidor);
        this.usuarioIdAtual = investidor.getId();
        println("Investidor processado com sucesso pelo serviço: " + investidor);
    }

    public void comprar(AtivoFinanceiro novoAtivo) {
        if (this.usuarioIdAtual == 0) {
            throw new IllegalStateException("Nenhum investidor cadastrado no sistema.");
        }

        ativoRepository.inserirAtivo(usuarioIdAtual, novoAtivo);
        double valorTotal = novoAtivo.getQntd() * novoAtivo.getPrecoMedio();
        movimentacaoRepository.registrarMovimentacao(
                1,
                null,
                "COMPRA",
                novoAtivo.getQntd(),
                novoAtivo.getPrecoMedio(),
                valorTotal
        );
    }

    public List<AtivoFinanceiro> getAtivos() {
        if (this.usuarioIdAtual == 0) {
            return List.of();
        }
        return ativoRepository.listarPorUsuario(this.usuarioIdAtual);
    }

    public AtivoFinanceiro buscarAtivo(String ticker) {
        List<AtivoFinanceiro> ativos = getAtivos();
        for (AtivoFinanceiro ativo : ativos) {
            if (ativo.getTicker().equalsIgnoreCase(ticker)) {
                return ativo;
            }
        }
        throw new AtivoNaoEncontradoException("Erro: Ativo " + ticker + " não encontrado na carteira.");
    }

    public void vender(String ticker, double quantidade, double precoVenda) {
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

        movimentacaoRepository.registrarMovimentacao(
                1,
                null,
                "VENDA",
                quantidade,
                precoVenda,
                volumeDaVenda
        );
    }

    public ResumoCarteiraDTO obterResumo() {
        List<AtivoFinanceiro> ativos = getAtivos();
        double valorTotal = 0.0;
        for (AtivoFinanceiro ativo : ativos) {
            valorTotal += (ativo.getQntd() * ativo.getPrecoMedio());
        }
        return new ResumoCarteiraDTO(ativos.size(), valorTotal);
    }
}