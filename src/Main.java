import domain.entity.Acao;
import domain.entity.AtivoFinanceiro;
import domain.entity.Criptomoeda;
import domain.service.CarteiraService;
import domain.validador.Entrada;
import domain.dto.ResumoCarteiraDTO;

import java.time.LocalDate;

import static java.lang.IO.*;

void main(String[] args) {

        try {
                java.sql.Connection conexao = domain.repository.ConexaoBanco.obterConexao();
                println(" SUCESSO: Ligado à base de dados MySQL!");
                conexao.close();
        } catch (Exception e) {
                println(" ERRO: Não foi possível ligar à base de dados.");
                println("Detalhe do erro: " + e.getMessage());
                System.exit(1);
        }

        println("==============================================");
        println("    BEM-VINDO AO CARTEIRA DE INVESTIMENTO     ");
        println("==============================================");

        CarteiraService carteira = new CarteiraService();
        String nome;

        while (true) {
                nome = Entrada.lerEntrada("Digite seu nome: ");
                String cpf = Entrada.lerCpf("Digite seu CPF (com ou sem pontos): ");
                String email = Entrada.lerEmail("Digite seu e-mail: ");
                LocalDate dataNascimento = Entrada.lerData("Digite sua data de nascimento (AAAA-MM-DD): ");
                String nomeCorretora = Entrada.lerEntrada("Digite nome da Corretora: ");

                try {
                        carteira.cadastroCliente(nome, cpf, email, dataNascimento, nomeCorretora);
                        break;
                } catch (Exception e) {
                        println("\n" + e.getMessage());
                        println(" Dica: Tente utilizar outro CPF ou e-mail.\n");
                }
        }

        while (true) {
                println("\n--- MENU DE " + nome.toUpperCase() + " ---");
                println("1. Comprar Ativo");
                println("2. Vender Ativo");
                println("3. Listar Carteira");
                println("4. Ver Resumo");
                println("5. Sair");

                String opcao = Entrada.lerEntradaComRegex(
                        "Escolha uma opção (1-5): ",
                        "[1-5]",
                        "Digite um número de 1 a 5.");

                try {
                        switch (opcao) {
                                case "1" -> comprarAtivo(carteira);
                                case "2" -> venderAtivo(carteira);
                                case "3" -> listarCarteira(carteira);
                                case "4" -> mostrarResumo(carteira);
                                case "5" -> {
                                        println("Saindo do Carteira de Investimento. Até logo!");
                                        System.exit(0);
                                }
                        }
                } catch (Exception e) {
                        println("\n[!] " + e.getMessage());
                }
        }
}

void comprarAtivo(CarteiraService carteira) {
        println("\n-- COMPRAR ATIVO --");
        String tipo = Entrada.lerEntradaComRegex(
                "Tipo (1 - Ação | 2 - Cripto): ",
                "[12]",
                "Digite 1 ou 2.");

        String ticker = Entrada.lerEntrada("Ticker (ex: PETR4, BTC): ").toUpperCase();
        String nome = Entrada.lerEntrada("Nome da empresa/moeda: ");
        double qtd = Entrada.lerDouble("Quantidade: ");
        double preco = Entrada.lerDouble("Preço médio (R$): ");

        AtivoFinanceiro novoAtivo;
        if (tipo.equals("1")) {
                novoAtivo = new Acao(ticker, nome, qtd, preco);
        } else {
                novoAtivo = new Criptomoeda(ticker, nome, qtd, preco);
        }

        carteira.comprar(novoAtivo);
        println("Compra registrada com sucesso!");
}

void venderAtivo(CarteiraService carteira) {
        println("\n-- VENDER ATIVO --");
        String ticker = Entrada.lerEntrada("Ticker para vender: ").toUpperCase();
        carteira.buscarAtivo(ticker);

        double qtd = Entrada.lerDouble("Quantidade a vender: ");
        double preco = Entrada.lerDouble("Preço de venda (R$): ");

        carteira.vender(ticker, qtd, preco);
        println("Venda realizada com sucesso!");
}

void listarCarteira(CarteiraService carteira) {
        println("\n-- SUA CARTEIRA --");
        if (carteira.getAtivos().isEmpty()) {
                println("Sua carteira está vazia.");
                return;
        }
        for (AtivoFinanceiro ativo : carteira.getAtivos()) {
                println(ativo.toString());
        }
}

void mostrarResumo(CarteiraService carteira) {
        println("\n-- RESUMO GERAL --");
        ResumoCarteiraDTO resumo = carteira.obterResumo();

        println("Ativos diferentes na carteira: " + resumo.quantidadeTotalAtivos());
        println(String.format("Valor total investido: R$ %.2f", resumo.valorTotalAlocado()));
}