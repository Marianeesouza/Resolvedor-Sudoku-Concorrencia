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


public class HelloApplication extends Application {

    private static final int SIZE = 9;
    private Label[][] cells = new Label[SIZE][SIZE];
    private Sudoku sudoku;

    @Override
    public void start(Stage primaryStage) {
        int[][] board = new int[SIZE][SIZE]; // Tabuleiro inicial vazio
        sudoku = new Sudoku(board);

        GridPane grid = new GridPane();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Label cell = new Label("");
                cell.setStyle("-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center;");
                cells[i][j] = cell;
                grid.add(cell, j, i);
            }
        }

        Scene scene = new Scene(grid, 400, 400);
        primaryStage.setTitle("Evolução do Sudoku");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Configurando o callback. Note que usamos o import da interface (está dentro de Solver.Sudoku).
        sudoku.setUpdateCallback((row, col, value) -> updateCell(row, col, value));

        new Thread(() -> sudoku.solveConcurrently()).start();
    }

    public void updateCell(int row, int col, int value) {
        Platform.runLater(() -> {
            cells[row][col].setText(value == 0 ? "" : String.valueOf(value));
            cells[row][col].setStyle(
                    "-fx-border-color: black; -fx-min-width: 40px; -fx-min-height: 40px; -fx-alignment: center; -fx-background-color: lightyellow;"
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


