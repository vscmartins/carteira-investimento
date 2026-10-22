package domain.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.IO.*;

public class MovimentacaoRepository {

    public void registrarMovimentacao(
            int idConta,
            Integer idAtivo,
            String tipoMovimentacao,
            Double quantidade,
            Double precoUnitario,
            double valorTotal) {
        String sql = "INSERT INTO movimentacoes (id_conta, id_ativo, tipo_movimentacao, quantidade, preco_unitario, valor_total) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idConta);

            if (idAtivo != null) {
                stmt.setInt(2, idAtivo);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.setString(3, tipoMovimentacao);

            if (quantidade != null) {
                stmt.setDouble(4, quantidade);
            } else {
                stmt.setNull(4, java.sql.Types.DECIMAL);
            }

            if (precoUnitario != null) {
                stmt.setDouble(5, precoUnitario);
            } else {
                stmt.setNull(5, java.sql.Types.DECIMAL);
            }

            stmt.setDouble(6, valorTotal);

            stmt.executeUpdate();
            println("Histórico de movimentação registado com sucesso.");

        } catch (SQLException e) {
            println("Erro ao registar movimentação na base de dados: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}