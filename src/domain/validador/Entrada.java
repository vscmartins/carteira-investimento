package domain.validador;

import static java.lang.IO.*;

public class Entrada {

    public static String lerEntrada(String mensagem) {
        while (true) {

            String entrada = readln(mensagem);


            if (entrada == null) {
                println("\nEntrada encerrada (Ctrl+D). Saindo do Sistema...");
                System.exit(0);
            }

            entrada = entrada.trim();
            if (!entrada.isEmpty()) return entrada;
            println("Erro: O texto não pode ser vazio.");
        }
    }

    public static String lerEntradaComRegex(String mensagem, String regex, String msgErro) {
        while (true) {
            String entrada = lerEntrada(mensagem).toUpperCase();
            if (entrada.matches(regex)) {
                return entrada;
            }
            println("Erro: " + msgErro);
        }
    }

    public static double lerDouble(String mensagem) {
        while (true) {
            try {
                String entrada = lerEntrada(mensagem).replace(",", ".");
                double valor = Double.parseDouble(entrada);
                if (valor > 0)
                    return valor;
                println("Erro: O valor deve ser estritamente positivo.");
            } catch (NumberFormatException e) {
                println("Erro: Digite um número válido. Evite letras.");
            }
        }
    }
}