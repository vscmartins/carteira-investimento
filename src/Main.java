import domain.entity.Acao;
import domain.entity.AtivoFinanceiro;
import domain.entity.Criptomoeda;
import domain.entity.Investidor;
import domain.service.CarteiraService;
import domain.validador.Entrada;
import domain.dto.ResumoCarteiraDTO;

import static java.lang.IO.*;

void main(String[] args) {

        // --- TESTE DE LIGAÇÃO À BASE DE DADOS ---
        try {
                java.sql.Connection conexao = domain.repository.ConexaoBanco.obterConexao();
                println("✅ SUCESSO: Ligado à base de dados MySQL!");
                conexao.close(); // Fechamos logo a seguir só para testar
        } catch (Exception e) {
                println("❌ ERRO: Não foi possível ligar à base de dados.");
                println("Detalhe do erro: " + e.getMessage());
                System.exit(1); // Para o programa se não houver base de dados
        }
        // ----------------------------------------

        println("==============================================");
        println("    BEM-VINDO AO CARTEIRA DE INVESTIMENTO     ");
        println("==============================================");


        String nome = Entrada.lerEntrada("Digite seu nome: ");
        String cpf = Entrada.lerCpf("Digite seu CPF (com ou sem pontos): ");
        Investidor investidor = new Investidor(nome, cpf);

        CarteiraService carteira = new CarteiraService();

        while (true) {
                println("\n--- MENU DE " + investidor.getNome().toUpperCase() + " ---");
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

        String ticket = Entrada.lerEntrada("Ticket (ex: PETR4, BTC): ").toUpperCase();
        String nome = Entrada.lerEntrada("Nome da empresa/moeda: ");
        int qtd = (int) Entrada.lerDouble("Quantidade: ");
        double preco = Entrada.lerDouble("Preço médio (R$): ");

        AtivoFinanceiro novoAtivo;
        if (tipo.equals("1")) {
                novoAtivo = new Acao(ticket, nome, qtd, preco);
        } else {
                novoAtivo = new Criptomoeda(ticket, nome, qtd, preco);
        }

        carteira.comprar(novoAtivo);
        println("✅ Compra registrada com sucesso!");
}

void venderAtivo(CarteiraService carteira) {
        println("\n-- VENDER ATIVO --");
        String ticket = Entrada.lerEntrada("Ticket para vender: ").toUpperCase();
        carteira.buscarAtivo(ticket);

        int qtd = (int) Entrada.lerDouble("Quantidade a vender: ");
        double preco = Entrada.lerDouble("Preço de venda (R$): ");

        carteira.vender(ticket, qtd, preco);
        println("✅ Venda realizada com sucesso!");
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