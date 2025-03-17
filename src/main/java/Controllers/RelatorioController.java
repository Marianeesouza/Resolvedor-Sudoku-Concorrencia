package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.resolvedor_sudoku_concorrencia.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RelatorioController {

    @FXML
    private HBox mainContainer;
    @FXML
    private Button btnVoltar = new Button();

    private static final String FILE_NAME = "sudoku_resultados.csv";

    public void initialize() {
        Map<Integer, Map<Integer, List<Integer>>> temposSize9 = new HashMap<>();
        Map<Integer, Map<Integer, List<Integer>>> temposSize16 = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                String[] partes = linha.split(",");
                if (partes.length < 4) continue;

                int threads = Integer.parseInt(partes[0]);
                int tempo = Integer.parseInt(partes[1]);
                int espacos = Integer.parseInt(partes[2]);
                int size = Integer.parseInt(partes[3]);

                Map<Integer, Map<Integer, List<Integer>>> tempos = (size == 9) ? temposSize9 : temposSize16;
                tempos.putIfAbsent(espacos, new HashMap<>());
                tempos.get(espacos).putIfAbsent(threads, new ArrayList<>());
                tempos.get(espacos).get(threads).add(tempo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        LineChart<String, Number> graficoSize9 = criarGrafico("Média - Tabuleiro 9x9", temposSize9, 9);
        LineChart<String, Number> graficoSize16 = criarGrafico("Média - Tabuleiro 16x16", temposSize16, 16);

        VBox graficosContainer = new VBox();
        graficosContainer.setMinWidth(900);
        graficosContainer.getChildren().addAll(graficoSize9, graficoSize16);

        btnVoltar.setText("Voltar");
        btnVoltar.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-color: #F44336; -fx-text-fill: white; -fx-border-radius: 5px;");
        btnVoltar.setOnAction(event -> {
            try {
                voltarParaTelaInicial();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        VBox botaoContainer = new VBox();
        botaoContainer.setMinWidth(10);
        botaoContainer.getChildren().add(btnVoltar);

        mainContainer.getChildren().addAll(graficosContainer, botaoContainer);
    }

    private LineChart<String, Number> criarGrafico(String titulo, Map<Integer, Map<Integer, List<Integer>>> tempos, int size) {
        CategoryAxis eixoX = new CategoryAxis();
        NumberAxis eixoY = new NumberAxis();
        eixoX.setLabel("Número de Threads");
        eixoY.setLabel("Log(Tempo Médio de Execução)");

        LineChart<String, Number> grafico = new LineChart<>(eixoX, eixoY);
        grafico.setTitle(titulo);

        Set<Integer> threadsPermitidos = (size == 9) ? new HashSet<>(Arrays.asList(1, 3, 9)) : new HashSet<>(Arrays.asList(1, 2, 3, 4, 8, 16));

        for (Integer espacos : tempos.keySet()) {
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Espaços Vazios: " + espacos);

            List<Integer> threadsOrdenadas = new ArrayList<>(tempos.get(espacos).keySet());
            Collections.sort(threadsOrdenadas);

            for (Integer threads : threadsOrdenadas) {
                if (threadsPermitidos.contains(threads)) {
                    List<Integer> valores = tempos.get(espacos).get(threads);
                    valores.sort(Integer::compareTo);

                    double q1 = valores.get(valores.size() / 4);
                    double q3 = valores.get(3 * valores.size() / 4);
                    double iqr = q3 - q1;
                    double limInf = q1 - 1.5 * iqr;
                    double limSup = q3 + 1.5 * iqr;

                    List<Integer> filtrados = new ArrayList<>();
                    for (int val : valores) {
                        if (val >= limInf && val <= limSup) {
                            filtrados.add(val);
                        }
                    }

                    if (!filtrados.isEmpty()) {
                        double media = filtrados.stream().mapToInt(Integer::intValue).average().orElse(0);
                        double logMedia = Math.log(media + 1);
                        serie.getData().add(new XYChart.Data<>(String.valueOf(threads), logMedia));
                    }
                }
            }
            grafico.getData().add(serie);
        }
        return grafico;
    }

    private void voltarParaTelaInicial() throws IOException {
        Main.TrocarTela(new FXMLLoader(Main.class.getResource("telaInicial.fxml")).load());
    }
}
