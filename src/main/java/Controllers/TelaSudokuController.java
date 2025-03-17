package Controllers;

import Solver.Sudoku;
import Solver.SudokuGenerator;
import Utils.TamanhoMatriz;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.example.resolvedor_sudoku_concorrencia.Main;

public class TelaSudokuController implements Initializable {

    @FXML
    private AnchorPane panePrincipal;
    @FXML
    private Button btnResolver;
    @FXML
    private Button btnMudarTabuleiro;
    @FXML
    private Button btnVoltar;

    private final int SIZE = TamanhoMatriz.tamanhoMatriz;
    private Label[][] mainBoard = new Label[SIZE][SIZE];
    private int[][] board = new int[SIZE][SIZE];
    private final Map<Thread, Label[][]> threadBoards = new HashMap<>();
    private final Map<Thread, GridPane> threadGrids = new HashMap<>();
    private Sudoku sudoku;
    private GridPane mainGrid;
    private HBox root;
    private GridPane threadBoardsContainer;
    private ComboBox<Integer> threadSelector;
    private ComboBox<Integer> emptyCellsSelector;
    private Label timeLabel;
    private static final Label failureLabel = new Label("Falhas: ");
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private int threadGridCounter = 0;
    public boolean jaFoiCriado = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Maximiza a janela após a cena ser carregada
        emptyCellsSelector = new ComboBox<>();
        if (SIZE == 9) {
            emptyCellsSelector.getItems().addAll(10, 20, 30, 40, 50, 60);
        } else if (SIZE == 16) {
            emptyCellsSelector.getItems().addAll(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150);
        }
        emptyCellsSelector.setPromptText("Escolha células vazias");

        threadSelector = new ComboBox<>();
        if (SIZE == 9) {
            threadSelector.getItems().addAll(1, 3, 9);
        } else if (SIZE == 16) {
            threadSelector.getItems().addAll(1, 2, 3, 4, 8, 16);
        }
        threadSelector.setValue(3);
        threadSelector.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        btnResolver.setText("Iniciar Sudoku");
        btnResolver.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px;");
        btnResolver.setDisable(true);

        btnMudarTabuleiro.setText("Mudar tabuleiro");
        btnMudarTabuleiro.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-color:  #FF9800; -fx-text-fill: white; -fx-border-radius: 5px;");
        btnMudarTabuleiro.setDisable(true);

        btnVoltar = new Button("Voltar");
        btnVoltar.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-color: #F44336; -fx-text-fill: white; -fx-border-radius: 5px;");
        btnVoltar.setOnAction(event -> {
            try {
                voltarParaTelaInicial();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        timeLabel = new Label("Tempo: --");
        timeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        failureLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");

        double cellSize = SIZE > 9 ? 20 : 30;
        mainGrid = createGrid(mainBoard, cellSize);
        mainGrid.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-width: 3px; -fx-background-color: #f0f0f0;");

        HBox controls = new HBox(15,
                new Label("Células Vazias:"), emptyCellsSelector,
                new Label("Número de Threads:"), threadSelector,
                btnResolver, btnMudarTabuleiro, btnVoltar
        );
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-padding: 10px;");

        VBox infoPanel = new VBox(5, timeLabel, failureLabel);
        infoPanel.setAlignment(Pos.CENTER);

        threadBoardsContainer = new GridPane();
        threadBoardsContainer.setHgap(10);
        threadBoardsContainer.setVgap(10);
        threadBoardsContainer.setAlignment(Pos.CENTER);
        threadBoardsContainer.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-width: 1px;");

        ScrollPane threadScrollPane = new ScrollPane();
        threadScrollPane.setContent(threadBoardsContainer);
        threadScrollPane.setFitToWidth(true);
        threadScrollPane.setPrefHeight(400);

        root = new HBox(20, mainGrid, threadScrollPane);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 10px;");

        VBox layout = new VBox(10, controls, root, infoPanel);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(15));

        panePrincipal.getChildren().add(layout);


        emptyCellsSelector.setOnAction(event -> btnResolver.setDisable(false));
        btnResolver.setOnAction(this::resolverSudoku);
    }

    @FXML
    private void voltarParaTelaInicial() throws IOException {
        Main.TrocarTela(new FXMLLoader(Main.class.getResource("telaInicial.fxml")).load());
    }


    private void updateMainGrid() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int value = board[i][j];
                mainBoard[i][j].setText(value == 0 ? "" : String.valueOf(value));
            }
        }
    }

    private GridPane createGrid(Label[][] labels, double cellSize) {
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(3);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-padding: 5px;");

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Label cell = new Label("");
                cell.setMinSize(cellSize, cellSize);
                cell.setStyle("-fx-border-color: black; -fx-font-size: 16px; -fx-alignment: center; -fx-background-color: white; -fx-border-radius: 5px;");
                labels[i][j] = cell;
                grid.add(cell, j, i);
            }
        }
        return grid;
    }

    private void iniciarSudoku() {
        btnResolver.setDisable(true);
        int emptyCells = emptyCellsSelector.getValue();

        Task<Void> sudokuTask = new Task<>() {
            @Override
            protected Void call() {
                sudoku.solveConcurrently();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    btnResolver.setDisable(false);
                    Duration duration = sudoku.getDuration();
                    timeLabel.setText("Tempo: " + duration.toMillis() + " ms");

                    // Atualiza as falhas corretamente
                    salvarResultadosSudoku(threadSelector.getValue(), duration, emptyCells, SIZE);
                    List<String> failureMessages = sudoku.getFailureLabel();
                    if (!failureMessages.isEmpty()) {
                        StringBuilder sb = new StringBuilder("Falhas:");
                        failureMessages.forEach(msg -> sb.append("\n").append(msg));
                        failureLabel.setText(sb.toString());
                    } else {
                        failureLabel.setText("Nenhuma falha encontrada.");
                    }
                });
            }
        };

        executorService.submit(sudokuTask);
    }

    public void updateBoard(int[][] board, Thread thread) {
        Platform.runLater(() -> {
            Label[][] threadBoard;
            if (!threadBoards.containsKey(thread)) {
                threadBoard = new Label[SIZE][SIZE];
                GridPane threadGrid = createGrid(threadBoard, 25);
                threadBoards.put(thread, threadBoard);
                threadGrids.put(thread, threadGrid);

                Label threadLabel = new Label("Thread " + thread.getName());
                threadLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

                VBox threadBox = new VBox(5, threadLabel, threadGrid);
                threadBox.setAlignment(Pos.CENTER);
                threadBox.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 5px; -fx-background-color: #e3f2fd;");

                int row = threadGridCounter / 2;
                int col = threadGridCounter % 2;
                threadBoardsContainer.add(threadBox, col, row);

                threadGridCounter++;
            } else {
                threadBoard = threadBoards.get(thread);
            }

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int value = board[i][j];
                    threadBoard[i][j].setText(value == 0 ? "" : String.valueOf(value));
                }
            }
        });

    }

    @FXML
    private void resolverSudoku(ActionEvent event) {
        int numThreads = threadSelector.getValue();
        threadBoardsContainer.getChildren().clear();
        threadBoards.clear();
        if (!jaFoiCriado){
            btnResolver.setDisable(true);

            threadGrids.clear();
            threadGridCounter = 0;
            failureLabel.setText("Falhas: ");

            int emptyCells = emptyCellsSelector.getValue();
            SudokuGenerator generator = new SudokuGenerator(SIZE);
            board = generator.generateBoard(emptyCells);
            updateMainGrid();

            sudoku = new Sudoku(board, numThreads);
            sudoku.setUpdateCallback(this::updateBoard);
            jaFoiCriado = true;
            iniciarSudoku();

        }else{
            sudoku = new Sudoku(board, numThreads);
            sudoku.setUpdateCallback(this::updateBoard);
            iniciarSudoku();
        }
        btnMudarTabuleiro.setDisable(false);
    }

    @FXML
    private void mudarTabuleiro(ActionEvent event) {
        btnResolver.setDisable(false);
        threadBoardsContainer.getChildren().clear();
        threadBoards.clear();
        threadGrids.clear();
        threadGridCounter = 0;
        failureLabel.setText("Falhas: ");

        int emptyCells = emptyCellsSelector.getValue();
        SudokuGenerator generator = new SudokuGenerator(SIZE);
        board = generator.generateBoard(emptyCells);
        updateMainGrid();
        int numThreads = threadSelector.getValue();
        sudoku = new Sudoku(board, numThreads);
        sudoku.setUpdateCallback(this::updateBoard);

        jaFoiCriado = true;
    }


    private void salvarResultadosSudoku(int numThreads, Duration tempoExecucao, int espacos, int size) {
        String nomeArquivo = "sudoku_resultados.csv";
        boolean arquivoExiste = new File(nomeArquivo).exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            if (!arquivoExiste) {
                writer.write("Threads,Tempo (ms),Espaços,Tamanho\n");
            }
            writer.write(numThreads + "," + tempoExecucao.toMillis() + "," + espacos + "," + size);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Erro ao salvar os resultados do Sudoku: " + e.getMessage());
        }
    }


}
