package domain.model;

public class Investidor {

    private String nome;
    private String cpf;

    public Investidor(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }
    public String getNome() {
        return nome;
    }
    public String getCpf() {
        return cpf.substring(0,3) + "." +
                cpf.substring(3,6) + "." +
                cpf.substring(6,9) + "-" +
                cpf.substring(9,11);

    }

    @Override
    public String toString() {
        return "Investidor(a): " + nome + " | CPF: " + getCpf();
    }
}
