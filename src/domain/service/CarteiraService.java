package domain.service;

import domain.entity.AtivoFinanceiro;
import domain.entity.Investidor;
import domain.entity.TipoAtivo;
import domain.dto.ResumoCarteiraDTO;
import domain.exception.AtivoNaoEncontradoException;
import domain.exception.SaldoInsuficienteException;
import domain.repository.AtivoRepository;
import domain.repository.CorretoraRepository;
import domain.repository.ContaRepository;
import domain.repository.MovimentacaoRepository;
import domain.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

import static java.lang.IO.*;

public class CarteiraService {

    private final UsuarioRepository usuarioRepository;
    private final AtivoRepository ativoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final CorretoraRepository corretoraRepository;
    private final ContaRepository contaRepository;

    private int usuarioIdAtual;
    private int contaIdAtual;

    public CarteiraService() {
        this.usuarioRepository = new UsuarioRepository();
        this.ativoRepository = new AtivoRepository();
        this.movimentacaoRepository = new MovimentacaoRepository();
        this.corretoraRepository = new CorretoraRepository();
        this.contaRepository = new ContaRepository();
    }

    public void cadastroCliente(
            String nome,
            String cpf,
            String email,
            LocalDate dataNascimento,
            String nomeCorretora) {

        Investidor investidor = new Investidor(nome, cpf, email, dataNascimento);
        usuarioRepository.inserir(investidor);
        this.usuarioIdAtual = investidor.getId();
        int corretoraId = corretoraRepository.buscarOuInserir(nomeCorretora);
        int randomDoisDigitos = (int) (Math.random() * 90) + 10;
        int digito = (this.usuarioIdAtual % 2 == 0) ? 0 : 1;

        String numContaGerado = "AC" + this.usuarioIdAtual + randomDoisDigitos + "-" + digito;

        this.contaIdAtual = contaRepository.criarConta(
                this.usuarioIdAtual,
                corretoraId,
                numContaGerado);

        println("Investidor e Conta configurados com sucesso! Conta nº: " + numContaGerado);
    }

    public void comprar(AtivoFinanceiro novoAtivo) {
        if (this.usuarioIdAtual == 0 || this.contaIdAtual == 0) {
            throw new IllegalStateException("Nenhum investidor ou conta ativa no sistema.");
        }
        double valorTotal = novoAtivo.getQntd() * novoAtivo.getPrecoMedio();
        double saldoAtual = movimentacaoRepository.calcularSaldo(this.contaIdAtual);
        if (valorTotal > saldoAtual) {
            throw new SaldoInsuficienteException(
                    String.format("Erro: Saldo insuficiente para compra. Saldo disponível: R$ %.2f | Total da compra: R$ %.2f", saldoAtual, valorTotal)
            );
        }

        ativoRepository.inserirAtivo(usuarioIdAtual, novoAtivo);

        movimentacaoRepository.registrarMovimentacao(
                this.contaIdAtual,
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
        if (this.usuarioIdAtual == 0 || this.contaIdAtual == 0) {
            throw new IllegalStateException("Nenhum investidor ou conta ativa no sistema.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Erro: A quantidade a vender deve ser maior que zero.");
        }
        if (precoVenda <= 0) {
            throw new IllegalArgumentException("Erro: O preço de venda deve ser maior que zero.");
        }

        AtivoFinanceiro ativo = buscarAtivo(ticker);
        if (quantidade > ativo.getQntd()) {
            throw new SaldoInsuficienteException(
                    String.format("Erro: Quantidade insuficiente em carteira. Você possui apenas %.4f cotas de %s | Tentativa de venda: %.4f",
                            ativo.getQntd(), ticker.toUpperCase(), quantidade)
            );
        }

        ativo.subtrairQntd(quantidade);
        double volumeDaVenda = quantidade * precoVenda;
        movimentacaoRepository.registrarMovimentacao(
                this.contaIdAtual,
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
        double saldoLivre = movimentacaoRepository.calcularSaldo(this.contaIdAtual);
        return new ResumoCarteiraDTO(ativos.size(), valorTotal, saldoLivre);
    }

    public void depositar(double valor) {
        if (this.contaIdAtual == 0) {
            throw new IllegalStateException("Nenhuma conta ativa no sistema.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("Erro: O valor do depósito deve ser maior que zero.");
        }

        movimentacaoRepository.registrarMovimentacao(
                this.contaIdAtual,
                null,
                "DEPOSITO",
                null,
                null,
                valor
        );
    }

    public void sacar(double valor) {
        if (this.contaIdAtual == 0) {
            throw new IllegalStateException("Nenhuma conta ativa no sistema.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("Erro: O valor do saque deve ser maior que zero.");
        }
        double saldoAtual = movimentacaoRepository.calcularSaldo(this.contaIdAtual);
        if (valor > saldoAtual) {
            throw new SaldoInsuficienteException(
                    String.format("Erro: Saldo insuficiente para realizar o saque. Saldo disponível: R$ %.2f | Tentativa: R$ %.2f", saldoAtual, valor)
            );
        }

        movimentacaoRepository.registrarMovimentacao(
                this.contaIdAtual,
                null,
                "SAQUE",
                null,
                null,
                valor
        );
    }
}