package domain.repository;

import domain.entity.AtivoFinanceiro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.IO.*;

public class AtivoRepository {

    public void inserirAtivo(int usuarioId, AtivoFinanceiro ativo) {
        String sql = "INSERT INTO ativos (usuario_id, ticker, nome_empresa, tipo_ativo, quantidade, preco_medio) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setString(2, ativo.getTicker());
            stmt.setString(3, ativo.getNome());
            stmt.setString(4, ativo.getTipo().name());
            stmt.setDouble(5, ativo.getQntd());
            stmt.setDouble(6, ativo.getPrecoMedio());

            stmt.executeUpdate();
            println("Ativo " + ativo.getTicker() + " persistido com sucesso no banco de dados.");

        } catch (SQLException e) {
            println("Erro ao inserir ativo no banco de dados: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}