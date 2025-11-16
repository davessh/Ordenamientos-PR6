package interfaz;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logica.lectorCSV;
import logica.Ordenamientos;
import java.util.*;
import java.util.stream.Collectors;


public class interfazClima extends Application {

    private static final int IDX_FECHA = 0;
    private static final int IDX_COND = 1;
    private static final int IDX_PRECIP = 2;
    private static final int IDX_TEMP = 3;
    private static final int IDX_TEMP_AP = 4;
    private static final int IDX_HUM = 5;
    private static final int IDX_VIS = 6;
    private static final int IDX_WIND_SPEED = 7;
    private static final int IDX_WIND_DIR = 8;
    private static final int IDX_CLOUD_COVER = 9;
    private static final int IDX_PRESS = 10;
    private static final int IDX_DESC = 11;

    private VBox contenedorGrafica;
    private ChoiceBox<String> cbColumna;
    private Label etiquetaSinDatos;

    private final ObservableList<RegistroClima> registros = FXCollections.observableArrayList();
    private TableView<RegistroClima> vistaTabla;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenario) {
        escenario.setTitle("Ordenamientos comparaciones");

        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(10));
        TabPane panelPestanas = new TabPane();
        panelPestanas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab pestanaDatos = new Tab("Datos");
        vistaTabla = new TableView<>();
        vistaTabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnasTabla();
        pestanaDatos.setContent(vistaTabla);

        // Pestaña 2: Gráficas
        Tab pestanaGraficas = new Tab("Gráficas");
        contenedorGrafica = new VBox(10);
        contenedorGrafica.setPadding(new Insets(15));

        etiquetaSinDatos = new Label("No hay simulaciones realizadas");
        etiquetaSinDatos.setStyle("-fx-font-size: 14px; -fx-text-fill: gray; -fx-padding: 50px;");
        contenedorGrafica.getChildren().add(etiquetaSinDatos);

        ScrollPane scrollGraficas = new ScrollPane(contenedorGrafica);
        scrollGraficas.setFitToWidth(true);
        pestanaGraficas.setContent(scrollGraficas);

        panelPestanas.getTabs().addAll(pestanaDatos, pestanaGraficas);
        Button btnCargar = new Button("Cargar Datos");
        cbColumna = new ChoiceBox<>();
        cbColumna.getItems().addAll("fecha", "temperatura", "temperatura aparente", "humedad",
                "velocidad del viento", "dirección del viento",
                "visibilidad", "cobertura de nubes");
        cbColumna.setValue("temperatura");

        VBox cajaAlgoritmos = new VBox(4);
        CheckBox cbQuick = new CheckBox("quickSort");
        CheckBox cbMerge = new CheckBox("mergeSort");
        CheckBox cbInsercion = new CheckBox("inserción");
        CheckBox cbSelection = new CheckBox("selección");
        CheckBox cbShell = new CheckBox("shell");
        CheckBox cbRadix = new CheckBox("radix (cadenas)");
        CheckBox cbArraysSort = new CheckBox("Arrays.sort");
        CheckBox cbParallelSort = new CheckBox("Arrays.parallelSort");
        cbQuick.setSelected(true);
        cajaAlgoritmos.getChildren().addAll(cbQuick, cbMerge, cbInsercion, cbSelection, cbShell, cbRadix, cbArraysSort, cbParallelSort);

        Button btnOrdenar = new Button("Ordenar");
        Button btnHistograma = new Button("Generar Histograma");

        HBox controles = new HBox(8, btnCargar, new Label("Columna:"), cbColumna,
                new Label("Algoritmos:"), cajaAlgoritmos, btnOrdenar, btnHistograma);
        controles.setPadding(new Insets(5));

        raiz.setTop(controles);
        raiz.setCenter(panelPestanas);
        btnCargar.setOnAction(e -> {
            String ruta = "src/resources/weatherHistory.csv";
            cargarCsvATabla(ruta);
        });

        btnOrdenar.setOnAction(e -> {
            String col = cbColumna.getValue();
            List<String> algos = obtenerAlgoritmosSeleccionados(cajaAlgoritmos);
            if (algos.isEmpty()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING, "Selecciona al menos un algoritmo");
                alerta.show();
                return;
            }

            Map<String, Double> tiempos = new LinkedHashMap<>();
            for (String algo : algos) {
                int n = registros.size();
                if (n == 0) return;

                long tiempoInicio = System.nanoTime();
                List<RegistroClima> respaldo = new ArrayList<>(registros);
                ordenarPorColumna(col, algo);
                long tiempoFin = System.nanoTime();

                double tiempoMs = (tiempoFin - tiempoInicio) / 1_000_000.0;
                tiempos.put(algo, tiempoMs);
                registros.setAll(respaldo);
            }

            mostrarComparativaOrdenamiento(col, tiempos);

            panelPestanas.getSelectionModel().select(pestanaGraficas);
        });

        btnHistograma.setOnAction(e -> {
            String col = cbColumna.getValue();
            List<String> algos = obtenerAlgoritmosSeleccionados(cajaAlgoritmos);
            if (algos.isEmpty()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING, "Error,Selecciona al menos un algoritmo");
                alerta.show();
                return;
            }

            Map<String, Double> tiempos = new LinkedHashMap<>();
            for (String algo : algos) {
                int n = registros.size();
                if (n == 0) return;

                long tiempoInicio = System.nanoTime();
                List<RegistroClima> respaldo = new ArrayList<>(registros);
                ordenarPorColumna(col, algo);
                long tiempoFin = System.nanoTime();

                double tiempoMs = (tiempoFin - tiempoInicio) / 1_000_000.0;
                tiempos.put(algo, tiempoMs);
                registros.setAll(respaldo);
            }

            actualizarGraficas(col, tiempos);
            panelPestanas.getSelectionModel().select(pestanaGraficas);
        });

        Scene escena = new Scene(raiz, 1200, 700);
        escenario.setScene(escena);
        escenario.show();
    }

    private void configurarColumnasTabla() {
        TableColumn<RegistroClima, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        TableColumn<RegistroClima, String> colCond = new TableColumn<>("Condición");
        colCond.setCellValueFactory(new PropertyValueFactory<>("condicion"));

        TableColumn<RegistroClima, Double> colTemp = new TableColumn<>("Temperatura");
        colTemp.setCellValueFactory(new PropertyValueFactory<>("temperatura"));

        TableColumn<RegistroClima, Double> colTempAp = new TableColumn<>("Temp. Aparente");
        colTempAp.setCellValueFactory(new PropertyValueFactory<>("temperaturaAparente"));

        TableColumn<RegistroClima, Double> colHum = new TableColumn<>("Humedad");
        colHum.setCellValueFactory(new PropertyValueFactory<>("humedad"));

        TableColumn<RegistroClima, Double> colVelViento = new TableColumn<>("Vel. Viento");
        colVelViento.setCellValueFactory(new PropertyValueFactory<>("velocidadViento"));

        TableColumn<RegistroClima, Double> colDirViento = new TableColumn<>("Dir. Viento");
        colDirViento.setCellValueFactory(new PropertyValueFactory<>("direccionViento"));

        TableColumn<RegistroClima, Double> colVis = new TableColumn<>("Visibilidad");
        colVis.setCellValueFactory(new PropertyValueFactory<>("visibilidad"));

        TableColumn<RegistroClima, Double> colNubes = new TableColumn<>("Cob. Nubes");
        colNubes.setCellValueFactory(new PropertyValueFactory<>("coberturaNubes"));

        TableColumn<RegistroClima, String> colDesc = new TableColumn<>("Descripción");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        vistaTabla.getColumns().addAll(colFecha, colCond, colTemp, colTempAp, colHum,
                colVelViento, colDirViento, colVis, colNubes, colDesc);
        vistaTabla.setItems(registros);
    }

    private void cargarCsvATabla(String ruta) {
        lectorCSV lector = new lectorCSV(ruta);
        String[][] datos = lector.getDatos();
        registros.clear();
        if (datos == null) return;

        for (String[] fila : datos) {
            String fecha = obtenerSeguro(fila, IDX_FECHA);
            String cond = obtenerSeguro(fila, IDX_COND);
            double temp = parsearDoubleSeguro(obtenerSeguro(fila, IDX_TEMP));
            double tempAp = parsearDoubleSeguro(obtenerSeguro(fila, IDX_TEMP_AP));
            double hum = parsearDoubleSeguro(obtenerSeguro(fila, IDX_HUM));
            double vis = parsearDoubleSeguro(obtenerSeguro(fila, IDX_VIS));
            double velViento = parsearDoubleSeguro(obtenerSeguro(fila, IDX_WIND_SPEED));
            double dirViento = parsearDoubleSeguro(obtenerSeguro(fila, IDX_WIND_DIR));
            double nubes = parsearDoubleSeguro(obtenerSeguro(fila, IDX_CLOUD_COVER));
            String desc = obtenerSeguro(fila, IDX_DESC);

            registros.add(new RegistroClima(fecha, cond, temp, tempAp, hum, vis,
                    velViento, dirViento, nubes, desc));
        }

        contenedorGrafica.getChildren().clear();
        contenedorGrafica.getChildren().add(etiquetaSinDatos);
    }

    private String obtenerSeguro(String[] fila, int idx) {
        if (fila == null) return "";
        if (idx < 0 || idx >= fila.length) return "";
        return fila[idx];
    }

    private double parsearDoubleSeguro(String s) {
        if (s == null || s.isEmpty()) return Double.NaN;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private void ordenarPorColumna(String columna, String algoritmo) {
        int n = registros.size();
        if (n == 0) return;

        switch (columna) {
            case "fecha": {
                String[] arr = new String[n];
                Map<String, RegistroClima> mapa = new LinkedHashMap<>();
                for (int i = 0; i < n; i++) {
                    arr[i] = registros.get(i).getFecha();
                    mapa.put(arr[i] + "__" + i, registros.get(i));
                }
                aplicarOrdenamientoCadenas(arr, algoritmo);
                reordenarDesdeCadenas(arr, mapa);
                break;
            }
            case "temperatura":
            case "temperatura aparente":
            case "humedad":
            case "velocidad del viento":
            case "dirección del viento":
            case "visibilidad":
            case "cobertura de nubes": {
                double[] arr = new double[n];
                Map<Integer, RegistroClima> mapaIndice = new HashMap<>();
                for (int i = 0; i < n; i++) {
                    RegistroClima r = registros.get(i);
                    double v = Double.NaN;
                    switch (columna) {
                        case "temperatura": v = r.getTemperatura(); break;
                        case "temperatura aparente": v = r.getTemperaturaAparente(); break;
                        case "humedad": v = r.getHumedad(); break;
                        case "velocidad del viento": v = r.getVelocidadViento(); break;
                        case "dirección del viento": v = r.getDireccionViento(); break;
                        case "visibilidad": v = r.getVisibilidad(); break;
                        case "cobertura de nubes": v = r.getCoberturaNubes(); break;
                    }
                    if (Double.isNaN(v)) arr[i] = Double.NEGATIVE_INFINITY;
                    else arr[i] = v;
                    mapaIndice.put(i, r);
                }

                double[] primitivo = new double[n];
                for (int i = 0; i < n; i++) primitivo[i] = arr[i];

                aplicarOrdenamientoDouble(primitivo, algoritmo);

                List<RegistroClima> nuevoOrden = new ArrayList<>();
                boolean[] usado = new boolean[n];
                for (double valorOrdenado : primitivo) {
                    for (int j = 0; j < n; j++) {
                        double valorOriginal = arr[j];
                        if (Double.compare(valorOriginal, valorOrdenado) == 0 && !usado[j]) {
                            nuevoOrden.add(mapaIndice.get(j));
                            usado[j] = true;
                            break;
                        }
                    }
                }
                for (int j = 0; j < n; j++) if (!usado[j]) nuevoOrden.add(mapaIndice.get(j));

                registros.setAll(nuevoOrden);
                break;
            }
            default:
        }
    }

    private void aplicarOrdenamientoCadenas(String[] arr, String algoritmo) {
        switch (algoritmo) {
            case "quickSort": Ordenamientos.quickSort(arr); break;
            case "mergeSort": Ordenamientos.mergeSort(arr); break;
            case "inserción": Ordenamientos.insercion(arr, arr.length); break;
            case "selección": Ordenamientos.selection(arr, arr.length); break;
            case "shell": Ordenamientos.shell(arr, arr.length); break;
            case "radix (cadenas)": Ordenamientos.radixSortStrings(arr); break;
            default: Ordenamientos.quickSort(arr); break;
        }
    }

    private void aplicarOrdenamientoDouble(double[] arr, String algoritmo) {
        switch (algoritmo) {
            case "quickSort": Ordenamientos.quickSort(arr); break;
            case "mergeSort": Ordenamientos.mergeSort(arr); break;
            case "inserción": Ordenamientos.insercion(arr, arr.length); break;
            case "selección": Ordenamientos.selection(arr, arr.length); break;
            case "shell": Ordenamientos.shell(arr, arr.length); break;
            default: Ordenamientos.quickSort(arr); break;
        }
    }

    private void reordenarDesdeCadenas(String[] arrOrdenado, Map<String, RegistroClima> mapa) {
        List<RegistroClima> nuevoOrden = new ArrayList<>();
        for (String s : arrOrdenado) {
            Optional<String> claveOpt = mapa.keySet().stream()
                    .filter(k -> k.startsWith(s + "__"))
                    .findFirst();
            if (claveOpt.isPresent()) {
                nuevoOrden.add(mapa.get(claveOpt.get()));
                mapa.remove(claveOpt.get());
            }
        }
        nuevoOrden.addAll(mapa.values());
        registros.setAll(nuevoOrden);
    }

    private XYChart<String, Number> crearHistogramaPara(String columna) {
        switch (columna) {
            case "fecha":
                return histogramaPorFecha();
            case "temperatura":
                return histogramaNumerico(registros.stream().mapToDouble(RegistroClima::getTemperatura).toArray(), "Temperatura");
            case "temperatura aparente":
                return histogramaNumerico(registros.stream().mapToDouble(RegistroClima::getTemperaturaAparente).toArray(), "Temperatura Aparente");
            case "humedad":
                return histogramaNumerico(registros.stream().mapToDouble(RegistroClima::getHumedad).toArray(), "Humedad");
            case "velocidad del viento":
                return histogramaNumerico(registros.stream().mapToDouble(RegistroClima::getVelocidadViento).toArray(), "Velocidad del viento");
            case "dirección del viento":
                return histogramaNumerico(registros.stream().mapToDouble(RegistroClima::getDireccionViento).toArray(), "Dirección del viento");
            case "visibilidad":
                return histogramaNumerico(registros.stream().mapToDouble(RegistroClima::getVisibilidad).toArray(), "Visibilidad");
            case "cobertura de nubes":
                return histogramaNumerico(registros.stream().mapToDouble(RegistroClima::getCoberturaNubes).toArray(), "Cobertura de nubes");
            default:
                return null;
        }
    }

    private XYChart<String, Number> histogramaPorFecha() {
        CategoryAxis ejeX = new CategoryAxis();
        NumberAxis ejeY = new NumberAxis();
        BarChart<String, Number> grafica = new BarChart<>(ejeX, ejeY);
        grafica.setTitle("Histograma por fecha (conteo por día)");
        ejeX.setLabel("Fecha");
        ejeY.setLabel("Conteo");

        Map<String, Long> conteos = registros.stream()
                .map(RegistroClima::getFecha)
                .filter(s -> s != null && !s.isEmpty())
                .map(s -> {
                    if (s.length() >= 10) return s.substring(0, 10);
                    return s;
                })
                .collect(Collectors.groupingBy(d -> d, LinkedHashMap::new, Collectors.counting()));

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (Map.Entry<String, Long> e : conteos.entrySet()) {
            serie.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        grafica.getData().add(serie);
        grafica.setLegendVisible(false);
        grafica.setPrefWidth(900);
        grafica.setPrefHeight(400);
        grafica.setCategoryGap(2);
        return grafica;
    }

    private XYChart<String, Number> histogramaNumerico(double[] valores, String titulo) {
        double[] vals = Arrays.stream(valores)
                .filter(d -> !Double.isNaN(d) && !Double.isInfinite(d))
                .toArray();
        if (vals.length == 0) return null;

        double min = Arrays.stream(vals).min().getAsDouble();
        double max = Arrays.stream(vals).max().getAsDouble();
        int bins = Math.min(20, Math.max(5, (int)Math.sqrt(vals.length)));
        double ancho = (max - min) / bins;
        if (ancho == 0) ancho = 1;

        long[] conteos = new long[bins];
        for (double v : vals) {
            int b = (int)Math.floor((v - min) / ancho);
            if (b < 0) b = 0;
            if (b >= bins) b = bins - 1;
            conteos[b]++;
        }

        CategoryAxis ejeX = new CategoryAxis();
        NumberAxis ejeY = new NumberAxis();
        BarChart<String, Number> grafica = new BarChart<>(ejeX, ejeY);
        grafica.setTitle("Histograma - " + titulo);
        ejeX.setLabel(titulo + " (intervalos)");
        ejeY.setLabel("Conteo");

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (int i = 0; i < bins; i++) {
            double lo = min + i * ancho;
            double hi = lo + ancho;
            String etiqueta = String.format("%.2f - %.2f", lo, hi);
            serie.getData().add(new XYChart.Data<>(etiqueta, conteos[i]));
        }
        grafica.getData().add(serie);
        grafica.setLegendVisible(false);
        grafica.setPrefWidth(900);
        grafica.setPrefHeight(400);
        return grafica;
    }

    private List<String> obtenerAlgoritmosSeleccionados(VBox cajaAlgo) {
        List<String> seleccionados = new ArrayList<>();
        for (Node nodo : cajaAlgo.getChildren()) {
            if (nodo instanceof CheckBox) {
                CheckBox cb = (CheckBox) nodo;
                if (cb.isSelected()) {
                    seleccionados.add(cb.getText());
                }
            }
        }
        return seleccionados;
    }

    private void mostrarComparativaOrdenamiento(String columna, Map<String, Double> tiempos) {
        StringBuilder resultado = new StringBuilder();
        resultado.append("Columna: ").append(columna).append("\n");
        resultado.append("Elementos: ").append(registros.size()).append("\n\n");

        tiempos.entrySet().stream()
                .sorted((a, b) -> Double.compare(a.getValue(), b.getValue()))
                .forEach(e -> resultado.append(String.format("%-25s: %.4f ms\n", e.getKey(), e.getValue())));

        String masRapido = tiempos.entrySet().stream()
                .min((a, b) -> Double.compare(a.getValue(), b.getValue()))
                .map(Map.Entry::getKey)
                .orElse("N/A");

        resultado.append("\nMás rápido: ").append(masRapido);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Comparativa de Ordenamientos");
        alerta.setHeaderText("Resultados de la ejecución");
        alerta.setContentText(resultado.toString());
        alerta.show();
        actualizarGraficas(columna, tiempos);
    }

    private void actualizarGraficas(String columna, Map<String, Double> tiempos) {
        contenedorGrafica.getChildren().clear();
        Label titulo = new Label("Resultados de Simulación - " + columna);
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px;");
        contenedorGrafica.getChildren().add(titulo);

        XYChart<String, Number> graficaTiempos = crearGraficaTiempos(columna, tiempos);
        if (graficaTiempos != null) {
            contenedorGrafica.getChildren().add(graficaTiempos);
        }

        XYChart<String, Number> histograma = crearHistogramaPara(columna);
        if (histograma != null) {
            Label tituloHist = new Label("Distribución de Datos");
            tituloHist.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 20px 10px 10px 10px;");
            contenedorGrafica.getChildren().addAll(tituloHist, histograma);
        }
    }

    private XYChart<String, Number> crearGraficaTiempos(String columna, Map<String, Double> tiempos) {
        CategoryAxis ejeX = new CategoryAxis();
        NumberAxis ejeY = new NumberAxis();
        BarChart<String, Number> grafica = new BarChart<>(ejeX, ejeY);
        grafica.setTitle("Comparativa de Tiempos - " + columna);
        ejeX.setLabel("Algoritmo");
        ejeY.setLabel("Tiempo (ms)");

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Tiempo de ejecución");

        for (Map.Entry<String, Double> entrada : tiempos.entrySet()) {
            serie.getData().add(new XYChart.Data<>(entrada.getKey(), entrada.getValue()));
        }

        grafica.getData().add(serie);
        grafica.setPrefWidth(900);
        grafica.setPrefHeight(400);
        grafica.setLegendVisible(true);

        return grafica;
    }

    public static class RegistroClima {
        private final String fecha;
        private final String condicion;
        private final double temperatura;
        private final double temperaturaAparente;
        private final double humedad;
        private final double visibilidad;
        private final double velocidadViento;
        private final double direccionViento;
        private final double coberturaNubes;
        private final String descripcion;

        public RegistroClima(String fecha, String condicion, double temperatura, double temperaturaAparente,
                             double humedad, double visibilidad, double velocidadViento,
                             double direccionViento, double coberturaNubes, String descripcion) {
            this.fecha = fecha;
            this.condicion = condicion;
            this.temperatura = temperatura;
            this.temperaturaAparente = temperaturaAparente;
            this.humedad = humedad;
            this.visibilidad = visibilidad;
            this.velocidadViento = velocidadViento;
            this.direccionViento = direccionViento;
            this.coberturaNubes = coberturaNubes;
            this.descripcion = descripcion;
        }

        public String getFecha() { return fecha; }
        public String getCondicion() { return condicion; }
        public double getTemperatura() { return temperatura; }
        public double getTemperaturaAparente() { return temperaturaAparente; }
        public double getHumedad() { return humedad; }
        public double getVisibilidad() { return visibilidad; }
        public double getVelocidadViento() { return velocidadViento; }
        public double getDireccionViento() { return direccionViento; }
        public double getCoberturaNubes() { return coberturaNubes; }
        public String getDescripcion() { return descripcion; }
    }
}