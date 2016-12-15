package bsr.bank.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class RestHelper {

    static Properties prop = new Properties();
    private static InputStream input = null;

    static {
        try {
            System.out.println("Wczytuje property file.");
            input = new FileInputStream(new File(RestClient.class.getClassLoader().getResource("banks.properties").getFile()));
            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (input != null) {
                try { input.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
}
