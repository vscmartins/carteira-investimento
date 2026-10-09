import domain.model.Acao;

import static java.lang.IO.*;

void main(String[] args) {

        Acao minhaAcao = new Acao("PETR4",
                "Petrobras",
                100,
                35.50 );

        println(minhaAcao.toString());
        println("Tipo do Ativo: " + minhaAcao.getTipo());

        double imposto = minhaAcao.calcularImposto(500,15000);
        println("Imposto: " + imposto);

}
