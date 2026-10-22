package domain.repository;

import domain.entity.Acao;
import domain.entity.AtivoFinanceiro;
import domain.entity.Criptomoeda;
import domain.entity.TipoAtivo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<AtivoFinanceiro> listarPorUsuario(int usuarioId) {
        List<AtivoFinanceiro> ativos = new ArrayList<>();
        String sql = "SELECT ticker, nome_empresa, tipo_ativo, quantidade, preco_medio FROM ativos WHERE usuario_id = ?";

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ticker = rs.getString("ticker");
                    String nome = rs.getString("nome_empresa");
                    String tipoStr = rs.getString("tipo_ativo");
                    double quantidade = rs.getDouble("quantidade");
                    double precoMedio = rs.getDouble("preco_medio");

                    TipoAtivo tipo = TipoAtivo.valueOf(tipoStr);
                    AtivoFinanceiro ativo;

                    if (tipo == TipoAtivo.ACAO) {
                        ativo = new Acao(ticker, nome, quantidade, precoMedio);
                    } else {
                        ativo = new Criptomoeda(ticker, nome, quantidade, precoMedio);
                    }

                    ativos.add(ativo);
                }
            }

        } catch (SQLException e) {
            println("Erro ao consultar ativos na base de dados: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return ativos;
    }
}