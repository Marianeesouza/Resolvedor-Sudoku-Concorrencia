package org.example.resolvedor_sudoku_concorrencia;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import Solver.Sudoku;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private Button generateButton;

    @FXML
    private GridPane sudokuGrid; // Grade do Sudoku na interface

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onGenerateButtonClick() {
        Sudoku sudoku = new Sudoku(new int[9][9]);  // Criando tabuleiro vazio

        sudoku.generateBoard();  // Método para gerar automaticamente um Sudoku válido
        sudoku.print();  // Exibe o tabuleiro no console (para testes)

        sudoku.solve();  // Resolve o Sudoku
        sudoku.print();  // Exibe o tabuleiro resolvido (para testes)

        updateUI(sudoku);  // Atualiza a interface gráfica com o tabuleiro gerado e resolvido
    }

    private void updateUI(Sudoku sudoku) {
        sudokuGrid.getChildren().clear(); // Limpa a grade antes de atualizar

        int[][] board = sudoku.getBoard(); // Obtém o tabuleiro gerado
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Text cell = new Text(board[row][col] == 0 ? "" : String.valueOf(board[row][col]));
                sudokuGrid.add(cell, col, row);
            }
        }
    }
}
