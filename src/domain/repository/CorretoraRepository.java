package domain.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.IO.*;

public class CorretoraRepository {

    public int buscarOuInserir(String nomeCorretora) {
        String sqlSelect = "SELECT id_corretora FROM corretoras WHERE nome_corretora = ?";
        String sqlInsert = "INSERT INTO corretoras (nome_corretora) VALUES (?)";

        try (Connection conexao = ConexaoBanco.obterConexao()) {
            try (PreparedStatement stmt = conexao.prepareStatement(sqlSelect)) {
                stmt.setString(1, nomeCorretora);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id_corretora");
                    }
                }
            }

            try (PreparedStatement stmt = conexao.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, nomeCorretora);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            println("Erro ao processar corretora: " + e.getMessage());
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Não foi possível registrar a corretora.");
    }
}