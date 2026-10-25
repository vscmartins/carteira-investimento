package domain.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.IO.*;

public class ContaRepository {

    public int criarConta(int usuarioId, int corretoraId, String numeroConta) {
        String sql = "INSERT INTO contas (id_usuario, id_corretora, numero_conta) VALUES (?, ?, ?)";

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, usuarioId);
            stmt.setInt(2, corretoraId);
            stmt.setString(3, numeroConta);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new RuntimeException("Erro: Este número de conta já está registado no sistema!");
            }
            println("Erro ao criar conta: " + e.getMessage());
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Não foi possível criar a conta.");
    }
}