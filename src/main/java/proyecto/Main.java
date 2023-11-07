package proyecto;

import org.xml.sax.SAXException;
import proyecto.utils.ConexionMariaDB;
import proyecto.utils.Propiedades;
import proyecto.utils.XMLClass;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        try {
            XMLClass.xmlToBBDD(Propiedades.getPropiedad("PATH_READ_XML"));
            XMLClass.BBDDToXml(Propiedades.getPropiedad("PATH_WRITE_XML"));
            System.out.println("Programa Terminado correctamente");
        } catch (XPathExpressionException | IOException | SAXException | ParserConfigurationException |
                 ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            ConexionMariaDB.cerrarConexion();
        }
    }
}
