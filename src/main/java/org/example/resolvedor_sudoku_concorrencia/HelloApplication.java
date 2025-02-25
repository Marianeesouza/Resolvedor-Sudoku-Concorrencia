package org.example.resolvedor_sudoku_concorrencia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import Solver.Sudoku;
import Solver.Sudoku.CellUpdateCallback;

import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;

public class HelloApplication extends Application {

    private static final int SIZE = 9;
    private Label[][] mainBoard = new Label[SIZE][SIZE];
    private Map<Thread, Label[][]> threadBoards = new HashMap<>();
    private Sudoku sudoku;
    private GridPane mainGrid;
    private HBox root;

    @Override
    public void start(Stage primaryStage) {
        int[][] board = new int[SIZE][SIZE]; // Tabuleiro inicial vazio
        sudoku = new Sudoku(board);

        mainGrid = createGrid(mainBoard);
        root = new HBox(10);
        root.getChildren().add(mainGrid);

        Scene scene = new Scene(root, 800, 400);
        primaryStage.setTitle("Evolução do Sudoku com Threads");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Configurar callback para atualizar a UI
        sudoku.setUpdateCallback((row, col, value, thread) -> updateCell(row, col, value, thread));

        new Thread(() -> sudoku.solveConcurrently()).start();
    }

    private GridPane createGrid(Label[][] labels) {
        GridPane grid = new GridPane();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Label cell = new Label("");
                cell.setStyle("-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center;");
                labels[i][j] = cell;
                grid.add(cell, j, i);
            }
        }
        return grid;
    }

    public void updateCell(int row, int col, int value, Thread thread) {
        Platform.runLater(() -> {
            // Atualiza o tabuleiro principal
            mainBoard[row][col].setText(value == 0 ? "" : String.valueOf(value));
            mainBoard[row][col].setStyle(
                    "-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: lightyellow;"
            );

            // Atualiza o tabuleiro da thread correspondente
            if (!threadBoards.containsKey(thread)) {
                Label[][] threadBoard = new Label[SIZE][SIZE];
                GridPane threadGrid = createGrid(threadBoard);
                threadBoards.put(thread, threadBoard);

                root.getChildren().add(threadGrid);
            }

            Label[][] threadBoard = threadBoards.get(thread);
            threadBoard[row][col].setText(value == 0 ? "" : String.valueOf(value));
            threadBoard[row][col].setStyle(
                    "-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: lightblue;"
            );
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


