package BBDD;

import org.junit.jupiter.api.*;
import proyecto.dao.ContratoDao;
import proyecto.modelo.Contrato;
import proyecto.utils.ConexionMariaDB;

import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

public class BBDDTest {
    static Connection connection;

    @BeforeAll
    static void beforeAll() {
        assertDoesNotThrow(() -> {
            connection = ConexionMariaDB.prepararBBDD();
        });
    }

    @AfterAll
    static void afterAll() {
        assertDoesNotThrow(() -> {
            ConexionMariaDB.borrarTablaBBDD();
            connection.close();
        });
    }
    @Test
    void contratoTest() {

        Contrato contrato = new Contrato("a", "c", "d", "e", "f",
                "1234", "5", "1");
        assertDoesNotThrow(() -> {
            ContratoDao contratoDao = new ContratoDao(connection);
            contratoDao.escribirContrato(contrato);
            ResultSet rs = contratoDao.obtenerDatos();
            rs.next();
            Contrato contrato2 = new Contrato(
                    rs.getString(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getString(6),
                    rs.getString(7),rs.getString(8));
            assertEquals(contrato, contrato2);
        });
    }
}
