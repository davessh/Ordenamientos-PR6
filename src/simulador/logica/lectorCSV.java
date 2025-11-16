package logica;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class lectorCSV {

    private String[][] datos;

    public lectorCSV(String rutaArchivo) {
        leerCSV(rutaArchivo);
    }

    public void leerCSV(String ruta) {
        ArrayList<String[]> temporal = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                temporal.add(linea.split(","));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        datos = temporal.toArray(new String[0][0]);
    }

    public String[][] getDatos() {
        return datos;
    }

    public static void main(String[] args) {
        String ruta = "src/resources/weatherHistory.csv";
        lectorCSV lector = new lectorCSV(ruta);

        String[][] contenido = lector.getDatos();

        System.out.println("Filas leídas: " + contenido.length);
    }
}

