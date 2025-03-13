package Controllers;

import Solver.Sudoku;
import Solver.SudokuGenerator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    private final int SIZE = 9;
    private Label[][] mainBoard = new Label[SIZE][SIZE];
    private int[][] board = new int[SIZE][SIZE];

    private final Map<Thread, Label[][]> threadBoards = new HashMap<>();
    private Sudoku sudoku;

    private GridPane mainGrid;
    private HBox root;
    private HBox threadBoardsContainer;
    private ComboBox<Integer> threadSelector;
    private Label timeLabel;
    private static final Label failureLabel = new Label("Falhas: ");

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SudokuGenerator generator = new SudokuGenerator();
        board = generator.generateBoard(20);

        threadSelector = new ComboBox<>();
        threadSelector.getItems().addAll(1, 3, 9);
        threadSelector.setValue(3);
        threadSelector.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        btnResolver.setText("Resolver Sudoku");
        btnResolver.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px;");

        timeLabel = new Label("Tempo: --");
        timeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        failureLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");

        mainGrid = createGrid(mainBoard, 40);
        mainGrid.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-width: 3px; -fx-background-color: #f0f0f0;");
        updateMainGrid();

        HBox controls = new HBox(15, new Label("Número de Threads:"), threadSelector, btnResolver);
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-padding: 10px;");

        VBox infoPanel = new VBox(5, timeLabel, failureLabel);
        infoPanel.setAlignment(Pos.CENTER);

        threadBoardsContainer = new HBox(10);
        threadBoardsContainer.setAlignment(Pos.CENTER);
        threadBoardsContainer.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-width: 1px;");

        root = new HBox(20, mainGrid, threadBoardsContainer);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 10px;");

        VBox layout = new VBox(10, controls, root, infoPanel);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 15px;");

        panePrincipal.getChildren().add(layout);

        btnResolver.setOnAction(this::resolverSudoku);
    }
    private void updateMainGrid() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int value = board[i][j];
                mainBoard[i][j].setText(value == 0 ? "" : String.valueOf(value));
                mainBoard[i][j].setStyle(value == 0
                        ? "-fx-border-color: black; -fx-font-size: 16px; -fx-alignment: center; -fx-background-color: white; -fx-border-radius: 5px;"
                        : "-fx-border-color: black; -fx-font-size: 16px; -fx-alignment: center; -fx-background-color: lightgray; -fx-border-radius: 5px;");
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

    public void updateBoard(int[][] board, Thread thread) {
        Platform.runLater(() -> {
            if (!threadBoards.containsKey(thread)) {
                Label[][] threadBoard = new Label[SIZE][SIZE];
                GridPane threadGrid = createGrid(threadBoard, 35);
                threadBoards.put(thread, threadBoard);

                Label threadLabel = new Label("Thread " + thread.getName());
                threadLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: blue;");

                VBox threadBox = new VBox(5, threadLabel, threadGrid);
                threadBox.setAlignment(Pos.CENTER);
                threadBox.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 5px; -fx-background-color: #e3f2fd;");

                threadBoardsContainer.getChildren().add(threadBox);
            }

            Label[][] threadBoard = threadBoards.get(thread);
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int value = board[i][j];
                    threadBoard[i][j].setText(value == 0 ? "" : String.valueOf(value));
                    threadBoard[i][j].setStyle(value == 0
                            ? "-fx-border-color: black; -fx-font-size: 14px; -fx-alignment: center; -fx-background-color: white; -fx-border-radius: 5px;"
                            : "-fx-border-color: black; -fx-font-size: 14px; -fx-alignment: center; -fx-background-color: lightblue; -fx-border-radius: 5px;");
                }
            }
        });
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void iniciarSudoku() {
        btnResolver.setDisable(true);
        threadBoardsContainer.getChildren().clear();
        threadBoards.clear();

        int numThreads = threadSelector.getValue();
        sudoku = new Sudoku(board, numThreads);

        sudoku.setUpdateCallback((board, thread) -> updateBoard(board, thread));

        Task<Void> sudokuTask = new Task<>() {
            @Override
            protected Void call() {
                sudoku.solveConcurrently();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Duration duration = Sudoku.getDuration();
                    timeLabel.setText("Tempo: " + duration.toMillis() + " ms");

                    List<String> failureMessages = sudoku.getFailureLabel();
                    failureMessages.forEach(msg -> failureLabel.setText(failureLabel.getText() + "\n" + msg));

                    btnResolver.setDisable(false);
                    btnResolver.setText("Reiniciar Sudoku");
                    btnResolver.setStyle("-fx-background-color: #f57c00;");
                    btnResolver.setOnAction(e -> reiniciarSudoku());
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> failureLabel.setText("Erro ao resolver Sudoku!"));
            }
        };

        executorService.submit(sudokuTask);
    }

    private void reiniciarSudoku() {
        Sudoku.resetGame();
        SudokuGenerator generator = new SudokuGenerator();
        board = generator.generateBoard(20);
        updateBoard(board, null);
        btnResolver.setText("Resolver Sudoku");
        btnResolver.setStyle("-fx-background-color: #4CAF50;");
    }

    @FXML
    private void resolverSudoku(ActionEvent event) {
        iniciarSudoku();
    }
}