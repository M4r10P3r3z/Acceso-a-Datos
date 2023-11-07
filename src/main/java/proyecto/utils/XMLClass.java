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

        XPath xPath = XPathFactory.newInstance().newXPath();
        String busquedaNodos = Propiedades.getPropiedad("XPATH_RUTA_NODOS");
        NodeList nodeList = (NodeList) xPath.compile(busquedaNodos).evaluate(doc, XPathConstants.NODESET);

        escribirContratoBBDD(nodeList);
    }

    private static void escribirContratoBBDD(NodeList nodeList) throws ClassNotFoundException, SQLException, IOException, XPathExpressionException {
        contratoDao = new ContratoDao(ConexionMariaDB.prepararBBDD());
        for (int i = 1; i < nodeList.getLength(); i++) {
            Node nodo = nodeList.item(i);
            XPath xPath = XPathFactory.newInstance().newXPath();
            String busquedaDatos = Propiedades.getPropiedad("XPATH_RUTA_DATOS");
            NodeList listaDatos = (NodeList) xPath.compile(busquedaDatos).evaluate(nodo, XPathConstants.NODESET);
            if (nodo.getChildNodes().getLength() == 9) {
                String nif = listaDatos.item(0).getTextContent();
                String adjudicatario = listaDatos.item(1).getTextContent();
                String objetoGenerico = listaDatos.item(2).getTextContent();
                String objeto = listaDatos.item(3).getTextContent();
                String fecha = listaDatos.item(4).getTextContent();
                String importe = listaDatos.item(5).getTextContent();
                String proveedoresConsultados = listaDatos.item(6).getTextContent();
                String tipoContrato = listaDatos.item(7).getTextContent();
                contratoDao.escribirContrato(new Contrato(nif, adjudicatario, objetoGenerico, objeto, fecha, importe,
                        proveedoresConsultados, tipoContrato));
            }
        }
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
        //StringBuilder xmlStringBuilder = crearStringBuilder(doc);
        //xmlToArchivo(xmlStringBuilder, Constantes.PATH_WRITE_XML);
        escribirXML(doc, pathWriteXml);
    }

    private static StringBuilder crearStringBuilder(Document doc) {
        StringBuilder textoXML = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        textoXML.append(escribirListaNodos(doc.getChildNodes(), 0));
        return textoXML;
    }

    private static StringBuilder escribirListaNodos(NodeList nodeList, int nivel) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
                res.append("\t".repeat(nivel)).append("<").append(node.getNodeName()).append(">").
                        append(node.getChildNodes().item(0).getNodeValue()).
                        append("</").append(node.getNodeName()).append(">").append("\n");
            } else {
                res.append("\t".repeat(nivel)).append("<").append(node.getNodeName()).append(">").append("\n");
                if (node.hasChildNodes()) {
                    res.append(escribirListaNodos(node.getChildNodes(), nivel + 1));
                }
                res.append("\t".repeat(nivel)).append("</").append(node.getNodeName()).append(">").append("\n");
            }
        }
        return res;
    }

    private static Node crearNodo(Document doc, String nombre, String valor) {
        Node nodo = doc.createElement(nombre);
        nodo.setTextContent(valor);
        return nodo;
    }

    private static void xmlToArchivo(StringBuilder xmlStringBuilder, String pathWriteXml) throws FileNotFoundException {
        try {
            File miArchivo = new File(pathWriteXml);
            if (miArchivo.exists()) miArchivo.delete();
            PrintWriter out = new PrintWriter(miArchivo);
            out.write(xmlStringBuilder.toString());
            out.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Problema al crear el archivo XML.");
        }
    }

    private static void escribirXML(Document doc, String pathWriteXml) {
        try {
            File miArchivo = new File(pathWriteXml);
            if (miArchivo.exists()) miArchivo.delete();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(miArchivo));
        } catch (TransformerException e) {
            System.out.println("Error al crear el nuevo archivo XML");
        }
    }
}
