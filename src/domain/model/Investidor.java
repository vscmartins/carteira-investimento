package domain.model;

import java.time.LocalDate;

public class Investidor {

    private int id;
    private String nome;
    private String cpf;
    private String email;
    private LocalDate dataNascimento;

    public Investidor(String nome, String cpf, String email, LocalDate dataNascimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9, 11);
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    @Override
    public String toString() {
        return "Investidor(a): " + nome + " | CPF: " + getCpf() + " | E-mail: " + email;
    }
}