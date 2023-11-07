package proyecto.dao;

import proyecto.modelo.Contrato;

import java.sql.*;

public class ContratoDao {
    Connection conn;

    public ContratoDao(Connection conn) {
        this.conn = conn;
    }

    public void escribirContrato (Contrato contrato) throws SQLException {
        String sentenciaSqlString = "insert into datos (NIF, ADJUDICATARIO, OBJETO_GENERICO," +
                "OBJETO, FECHA_DE_ADJUDICACION, IMPORTE, PROVEEDORES_CONSULTADOS, TIPO_DE_CONTRATO)" +
                "VALUES (?,?,?,?,?,?,?,?);";
        try (PreparedStatement insertSql = conn.prepareStatement(sentenciaSqlString)) {
            insertSql.setString(1, contrato.nif());
            insertSql.setString(2, contrato.adjudicatario());
            insertSql.setString(3, contrato.objetoGenerico());
            insertSql.setString(4, contrato.objeto());
            insertSql.setString(5, contrato.fecha());
            insertSql.setString(6, contrato.importe());
            insertSql.setString(7, contrato.proveedoresConsultados());
            insertSql.setString(8, contrato.tipoContrato());
            insertSql.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al crear contrato");
        }
    }
    public ResultSet obtenerDatos() throws SQLException {
        String sentenciaSqlString = "select * from datos;";
        try (Statement selectSql = conn.createStatement()) {
            return selectSql.executeQuery(sentenciaSqlString);
        } catch (SQLException e) {
            throw new SQLException("Error al leer de la base de datos");
        }
    }
}
