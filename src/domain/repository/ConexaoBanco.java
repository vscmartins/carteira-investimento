package domain.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    public static Connection obterConexao() throws SQLException {
        // O Java lê os bilhetes da mochila
        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String senha = System.getenv("DB_PASSWORD");

        // O condutor do JDBC liga a ignição e tenta conectar
        return DriverManager.getConnection(url, usuario, senha);
    }
}