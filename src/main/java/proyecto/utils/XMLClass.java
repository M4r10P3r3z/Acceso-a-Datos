package proyecto.utils;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import proyecto.dao.ContratoDao;
import proyecto.modelo.Contrato;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class XMLClass {

    private static ContratoDao contratoDao;

    public static void xmlToBBDD(String path) throws SQLException, XPathExpressionException, IOException,
            SAXException, ParserConfigurationException, ClassNotFoundException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(path);
        doc.getDocumentElement().normalize();

        contratoDao = new ContratoDao(ConexionMariaDB.prepararBBDD());

        XPath xPath = XPathFactory.newInstance().newXPath();
        String busquedaNodos = Propiedades.getPropiedad("XPATH_RUTA_NODOS");
        NodeList nodeList = (NodeList) xPath.compile(busquedaNodos).evaluate(doc, XPathConstants.NODESET);
        for (int i = 1; i < 500; i++) {
            escribirContratoBBDD(nodeList.item(i), contratoDao);
        }
    }

    private static void escribirContratoBBDD(Node nodo, ContratoDao contratoDao) throws SQLException {
        String nif = nodo.getChildNodes().item(0).getTextContent();
        String adjudicatario = nodo.getChildNodes().item(1).getTextContent();
        String objetoGenerico = nodo.getChildNodes().item(2).getTextContent();
        String objeto = nodo.getChildNodes().item(3).getTextContent();
        String fecha = nodo.getChildNodes().item(4).getTextContent();
        String importe = nodo.getChildNodes().item(5).getTextContent();
        String proveedoresConsultados = nodo.getChildNodes().item(6).getTextContent();
        String tipoContrato = nodo.getChildNodes().item(7).getTextContent();
        contratoDao.escribirContrato(new Contrato(nif, adjudicatario, objetoGenerico, objeto, fecha, importe,
                proveedoresConsultados, tipoContrato));
    }

    public static void BBDDToXml(String pathWriteXml) throws ParserConfigurationException, SQLException, FileNotFoundException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element raiz = doc.createElement("contratos");
        doc.appendChild(raiz);
        try (ResultSet rs = contratoDao.obtenerDatos()) {
            while (rs.next()) {
                Element contrato = doc.createElement("contrato");
                contrato.appendChild(crearNodo(doc, "nif", rs.getString(1)));
                contrato.appendChild(crearNodo(doc, "adjudicatario", rs.getString(2)));
                contrato.appendChild(crearNodo(doc, "objetoGenerico", rs.getString(3)));
                contrato.appendChild(crearNodo(doc, "objeto", rs.getString(4)));
                contrato.appendChild(crearNodo(doc, "fecha", rs.getString(5)));
                contrato.appendChild(crearNodo(doc, "importe", rs.getString(6)));
                contrato.appendChild(crearNodo(doc, "proveedoresConsultados", rs.getString(7)));
                raiz.appendChild(contrato);
            }
        }
        escribirXML(doc, pathWriteXml);
    }

    private static Node crearNodo(Document doc, String nombre, String valor) {
        Node nodo = doc.createElement(nombre);
        nodo.setTextContent(valor);
        return nodo;
    }

    private static void escribirXML(Document doc, String pathWriteXml) {
        try {
            File miArchivo = new File(pathWriteXml);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(miArchivo));
        } catch (TransformerException e) {
            System.out.println("Error al crear el nuevo archivo XML");
        }
    }
}
