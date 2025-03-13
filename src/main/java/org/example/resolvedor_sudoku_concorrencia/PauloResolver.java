package org.example.resolvedor_sudoku_concorrencia;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label; 
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Solver.Sudoku;
import Solver.SudokuGenerator;
import Solver.CellUpdateCallback;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PauloResolver extends Application {

    private static int SIZE = 9;
    private Label[][] mainBoard = new Label[SIZE][SIZE];
    private int[][] board = new int[SIZE][SIZE];
    private Map<Thread, Label[][]> threadBoards = new HashMap<>();
    private Sudoku sudoku;
    private GridPane mainGrid;
    private HBox root;
    private ComboBox<Integer> threadSelector; // Seletor fixo de threads
    private Label timeLabel;
    private static Label failureLabel =  new Label("Falhas: ");
    private Button startButton;

    @Override
    public void start(Stage primaryStage) {
        SudokuGenerator generator = new SudokuGenerator(9);
        board = generator.generateBoard(50); // Gera um novo Sudoku com 40 células vazias

        // Criar o seletor de número de threads (apenas 1, 3 ou 9)
        threadSelector = new ComboBox<>();
        threadSelector.getItems().addAll(1, 3, 9);
        threadSelector.setValue(3); // Valor padrão

        // Criar o botão para iniciar a resolução
        startButton = new Button("Iniciar Sudoku");
        startButton.setOnAction(e -> iniciarSudoku());

        timeLabel = new Label("Tempo: --"); // Inicialmente vazio

        // Layout da interface
        mainGrid = createGrid(mainBoard, 0);
        root = new HBox(10);
        root.getChildren().add(mainGrid);
        root.getChildren().add(timeLabel);
        root.getChildren().add(failureLabel);

        VBox layout = new VBox(10, new Label("Número de Threads:"), threadSelector, startButton, root);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setTitle("Evolução do Sudoku com Threads");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void iniciarSudoku() {
        startButton.setDisable(true); // Desabilita o botão enquanto o Sudoku está sendo resolvido
        int numThreads = threadSelector.getValue(); // Pega o valor escolhido (1, 3 ou 9)
        sudoku = new Sudoku(board, numThreads); // Criando Sudoku com a escolha do usuário

        System.out.println(sudoku.getUpdateCallback());
        // Configurar callback para atualizar a UI
        sudoku.setUpdateCallback((board, thread) -> updateBoard(board, Thread.currentThread()));

        new Thread(() -> {
            sudoku.solveConcurrently(); // Resolver o Sudoku

            // Atualiza o tempo de execução na UI
            Platform.runLater(() -> {
                Duration duration = Sudoku.getDuration(); // Pegando o tempo de execução
                timeLabel.setText("Tempo: " + duration.toMillis() + " ms");
                List<String> failureMessages = sudoku.getFailureLabel();
                for (String failureMsg : failureMessages) {
                    String msg = failureLabel.getText();
                    failureLabel.setText(msg+"\n"+failureMsg);
                }
                startButton.setDisable(false); // Habilita o botão novamente
                startButton.setText("Reiniciar Sudoku");
                startButton.setOnAction(e -> { reiniciarSudoku();});
            });
        }).start();
    }

    private void reiniciarSudoku() {
        Sudoku.resetGame(); // Reseta todas as variáveis estáticas
    
        SudokuGenerator generator = new SudokuGenerator(9);
        int[][] newBoard = generator.generateBoard(20);
    
        this.board = newBoard;
    
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.close();
    
    }
    
    

    private GridPane createGrid(Label[][] labels, int size) {
        GridPane grid = new GridPane();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Label cell = new Label("");
                if (size == 0) {
                    cell.setStyle("-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: white;");
                    cell.setText(this.board[i][j] == 0 ? "" : String.valueOf(this.board[i][j]));
                } else {
                    cell.setStyle("-fx-border-color: black; -fx-min-width: 20px; -fx-min-height: 20px; -fx-alignment: center;");
                }
                labels[i][j] = cell;
                grid.add(cell, j, i);
            }
        }
        return grid;
    }

    public void updateBoard(int[][] board, Thread thread) {
        Platform.runLater(() -> {
            // Atualize o tabuleiro principal
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int value = board[i][j];
                    mainBoard[i][j].setText(value == 0 ? "" : String.valueOf(value));
                    mainBoard[i][j].setStyle(value == 0 ? "-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: white;" : "-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: lightyellow;");
                }
            }

            if (!threadBoards.containsKey(thread)) {
                Label[][] threadBoard = new Label[SIZE][SIZE];
                GridPane threadGrid = createGrid(threadBoard, 1);
                threadBoards.put(thread, threadBoard);
                Platform.runLater(() -> root.getChildren().add(threadGrid));
            }

            // Verifique se a thread tem um tabuleiro associado e atualize-o
            if (threadBoards.containsKey(thread)) {
                Label[][] threadBoard = threadBoards.get(thread);
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        int value = board[i][j];
                        threadBoard[i][j].setText(value == 0 ? "" : String.valueOf(value));
                        threadBoard[i][j].setStyle(value == 0 ?
                                "-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: white;"
                                : "-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: lightblue;");
                    }
                }
            }
        });
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBoard(int[][] board){
        Platform.runLater(() -> {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int value = board[i][j];
                    mainBoard[i][j].setText(value == 0 ? "" : String.valueOf(value));
                    mainBoard[i][j].setStyle(value == 0 ? "-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: white;" : "-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: lightyellow;");
                }
            }
        });
    }

    public synchronized void updateCell(int row, int col, int value, Thread thread) {
        Platform.runLater(() -> {
            mainBoard[row][col].setText(value == 0 ? "" : String.valueOf(value));
            mainBoard[row][col].setStyle("-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: lightyellow;");

            if (!threadBoards.containsKey(thread)) {
                Label[][] threadBoard = new Label[SIZE][SIZE];
                GridPane threadGrid = createGrid(threadBoard, 1);
                threadBoards.put(thread, threadBoard);
                root.getChildren().add(threadGrid);
            }

            Label[][] threadBoard = threadBoards.get(thread);
            threadBoard[row][col].setText(value == 0 ? "" : String.valueOf(value));
            threadBoard[row][col].setStyle("-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: lightblue;");
        });

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}