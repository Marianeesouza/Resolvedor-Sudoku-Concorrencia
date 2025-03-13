package Controllers;

import Solver.Sudoku;
import Solver.SudokuGenerator;
import Utils.TamanhoMatriz;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;

public class TelaSudokuController implements Initializable {

    @FXML
    private AnchorPane panePrincipal;

    @FXML
    private Button btnResolver;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emptyCellsSelector = new ComboBox<>();
        if (SIZE == 9) {
            emptyCellsSelector.getItems().addAll(10, 20, 30, 40, 50, 60);
        } else if (SIZE == 16) {
            emptyCellsSelector.getItems().addAll(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150);
        }
        emptyCellsSelector.setPromptText("Escolha células vazias");

        threadSelector = new ComboBox<>();
        threadSelector.getItems().addAll(1, 3, 9);
        threadSelector.setValue(3);
        threadSelector.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        btnResolver.setText("Iniciar Sudoku");
        btnResolver.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px;");
        btnResolver.setDisable(true);

        timeLabel = new Label("Tempo: --");
        timeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        failureLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");

        double cellSize = SIZE > 9 ? 20 : 30;
        mainGrid = createGrid(mainBoard, cellSize);
        mainGrid.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-width: 3px; -fx-background-color: #f0f0f0;");

        HBox controls = new HBox(15, new Label("Células Vazias:"), emptyCellsSelector, new Label("Número de Threads:"), threadSelector, btnResolver);
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
        threadBoardsContainer.getChildren().clear();
        threadBoards.clear();
        threadGrids.clear();
        threadGridCounter = 0;
        failureLabel.setText("Falhas: ");

        int numThreads = threadSelector.getValue();
        int emptyCells = emptyCellsSelector.getValue();
        SudokuGenerator generator = new SudokuGenerator(SIZE);
        board = generator.generateBoard(emptyCells);
        updateMainGrid();

        sudoku = new Sudoku(board, numThreads);
        sudoku.setUpdateCallback(this::updateBoard);

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
                    Duration duration = Sudoku.getDuration();
                    timeLabel.setText("Tempo: " + duration.toMillis() + " ms");

                    // Atualiza as falhas corretamente
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
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void resolverSudoku(ActionEvent event) {
        iniciarSudoku();
    }
}
