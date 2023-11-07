package proyecto.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Propiedades {
    static Properties prop = null;
    private Propiedades() throws IOException {
        try {
            prop = new Properties();
            FileInputStream ip = new FileInputStream("src/main/resources/config.properties");
            prop.load(ip);
        } catch (IOException e) {
            throw new IOException("No se puede cargar el archivo de propiedades");
        }
    }

    public static String getPropiedad (String nombre) throws IOException {
        if (prop == null) {
            new Propiedades();
        }
        return prop.getProperty(nombre);
    }
}
