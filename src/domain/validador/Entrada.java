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
    public static String lerCpf(String mensagem) {
        while (true) {
            String cpf = lerEntrada(mensagem).replaceAll("[^0-9]", "");

            if (cpf.length() == 11 && isCpfValido(cpf)) {
                return cpf;
            }
            println("Erro: CPF inválido. Digite um CPF válido com 11 dígitos.");
        }
    }
    private static boolean isCpfValido(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }
            int resto = 11 - (soma % 11);
            int digito1 = (resto == 10 || resto == 11) ? 0 : resto;
            if (digito1 != (cpf.charAt(9) - '0')) return false;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }
            resto = 11 - (soma % 11);
            int digito2 = (resto == 10 || resto == 11) ? 0 : resto;
            return digito2 == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }
}