package domain.repository;

import domain.entity.Investidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.IO.*;

public class UsuarioRepository {

    public void inserir(Investidor investidor) {
        String sql = "INSERT INTO usuarios (nome_usuario, cpf, email, data_nascimento, data_cadastro) VALUES (?, ?, ?, ?, NOW())";

        String cpfLimpo = investidor.getCpf().replaceAll("\\D", "");

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, investidor.getNome());
            stmt.setString(2, cpfLimpo);
            stmt.setString(3, investidor.getEmail());
            stmt.setDate(4, java.sql.Date.valueOf(investidor.getDataNascimento()));

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    investidor.setId(generatedKeys.getInt(1));
                }
            }

            println("Usuário cadastrado com sucesso no banco de dados.");

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {
                if (e.getMessage().contains("cpf")) {
                    throw new RuntimeException("Erro: Este CPF já está cadastrado no sistema!");
                } else if (e.getMessage().contains("email")) {
                    throw new RuntimeException("Erro: Este e-mail já está cadastrado no sistema!");
                } else {
                    throw new RuntimeException("Erro: Registro duplicado na base de dados.");
                }
            }
            println("Erro ao inserir usuário no banco de dados: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}