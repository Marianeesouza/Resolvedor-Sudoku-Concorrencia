package Controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RelatorioController {

    @FXML
    private VBox containerGraficos;

    private static final String FILE_NAME = "sudoku_resultados.csv";

    public void initialize() {
        Map<Integer, Map<Integer, Integer>> somaTemposSize9 = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> contagemTemposSize9 = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> somaTemposSize16 = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> contagemTemposSize16 = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) { //pula o cabeçalho do csv
                    primeiraLinha = false;
                    continue;
                }

                String[] partes = linha.split(",");
                if (partes.length < 4) continue;

                int threads = Integer.parseInt(partes[0]);
                int tempo = Integer.parseInt(partes[1]);
                int espacos = Integer.parseInt(partes[2]);
                int size = Integer.parseInt(partes[3]);

                Map<Integer, Map<Integer, Integer>> somaTempos = (size == 9) ? somaTemposSize9 : somaTemposSize16;
                Map<Integer, Map<Integer, Integer>> contagemTempos = (size == 9) ? contagemTemposSize9 : contagemTemposSize16;

                somaTempos.putIfAbsent(espacos, new HashMap<>());
                contagemTempos.putIfAbsent(espacos, new HashMap<>());

                somaTempos.get(espacos).put(threads, somaTempos.get(espacos).getOrDefault(threads, 0) + tempo);
                contagemTempos.get(espacos).put(threads, contagemTempos.get(espacos).getOrDefault(threads, 0) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        LineChart<Number, Number> graficoSize9 = criarGrafico("Média - Tabuleiro 9x9", somaTemposSize9, contagemTemposSize9);
        LineChart<Number, Number> graficoSize16 = criarGrafico("Média - Tabuleiro 16x16", somaTemposSize16, contagemTemposSize16);

        containerGraficos.getChildren().addAll(graficoSize9, graficoSize16);
    }

    private LineChart<Number, Number> criarGrafico(String titulo, Map<Integer, Map<Integer, Integer>> somaTempos, Map<Integer, Map<Integer, Integer>> contagemTempos) {
        NumberAxis eixoX = new NumberAxis();
        NumberAxis eixoY = new NumberAxis();
        eixoX.setLabel("Número de Threads");
        eixoY.setLabel("Tempo Médio de Execução (ms)");

        LineChart<Number, Number> grafico = new LineChart<>(eixoX, eixoY);
        grafico.setTitle(titulo);

        for (Integer espacos : somaTempos.keySet()) {
            XYChart.Series<Number, Number> serie = new XYChart.Series<>();
            serie.setName("Espaços Vazios: " + espacos);

            for (Integer threads : somaTempos.get(espacos).keySet()) {
                int soma = somaTempos.get(espacos).get(threads);
                int quantidade = contagemTempos.get(espacos).get(threads);
                double media = (double) soma / quantidade;

                serie.getData().add(new XYChart.Data<>(threads, media));
            }

            grafico.getData().add(serie);
        }

        return grafico;
    }
}
