package proyecto.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionMariaDB {
    private static Connection conn=null;

    private ConexionMariaDB() throws SQLException, ClassNotFoundException, IOException {
        try {
            Class.forName(Propiedades.getPropiedad("DRIVER"));
            conn = DriverManager.getConnection(Propiedades.getPropiedad("URL_CONEXION"),
                    Propiedades.getPropiedad("usuario"), Propiedades.getPropiedad("password"));
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Error con el cargador de la clase Driver");
        } catch (SQLException e) {
            throw new SQLException("No se puede conectar con la BBDD compruebe con el xampp está levantado");
        } catch (IOException e) {
            throw new IOException("Problema con el archivo de configuracion.");
        }
    }
    public static Connection prepararBBDD() throws SQLException, ClassNotFoundException, IOException {
        if (conn == null) {
            new ConexionMariaDB();
            borrarTablaBBDD();
            crearTablaBBDD();
        }
        return conn;
    }
    private static void crearTablaBBDD() throws SQLException {

        String selectTableSQL = "CREATE TABLE DATOS (" +
                "NIF varchar(255)," +
                "ADJUDICATARIO varchar(255)," +
                "OBJETO_GENERICO varchar(255)," +
                "OBJETO varchar(255)," +
                "FECHA_DE_ADJUDICACION varchar(255)," +
                "IMPORTE varchar(255)," +
                "PROVEEDORES_CONSULTADOS varchar(255)," +
                "TIPO_DE_CONTRATO varchar(255)" +
                ");";
        try (Statement statement = conn.createStatement()) {
            statement.executeQuery(selectTableSQL);
        } catch (SQLException e) {
            throw new SQLException("Error al crear la base de datos");
        }
    }
    public static void borrarTablaBBDD() {
        String sentenciaSqlString = "drop table if exists datos;";
        try (Statement sentenciaSql = conn.createStatement()) {
            sentenciaSql.execute(sentenciaSqlString);
        } catch (SQLException e) {
            System.out.println("No se puede borrar la tabla datos");
        }
    }
    public static void cerrarConexion() {
        if (conn!=null) {
            try {
                conn.close();
                System.out.println("Cerrando la conexión");
            } catch (SQLException e) {
                System.out.println("No se puede cerrar la conexión con la BBDD");
            }
        }
    }
}