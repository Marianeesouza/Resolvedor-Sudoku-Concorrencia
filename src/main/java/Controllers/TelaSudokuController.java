package Controllers;

import Solver.Sudoku;
import Utils.TamanhoMatriz;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class TelaSudokuController implements Initializable {

    @FXML
    private AnchorPane panePrincipal;

    @FXML
    private Button btnResolver;

    private int tamanhoMatriz;
    private TextField[][] campos;
    private int[][] board;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tamanhoMatriz = Integer.parseInt(TamanhoMatriz.tamanhoMatriz.split("x")[0]);
        board = new int[tamanhoMatriz][tamanhoMatriz];

        criarGridSudoku();
        carregarBoardFixo();
        atualizarInterface();
    }

    private void criarGridSudoku() {
        GridPane grid = new GridPane();
        campos = new TextField[tamanhoMatriz][tamanhoMatriz];

        for (int i = 0; i < tamanhoMatriz; i++) {
            for (int j = 0; j < tamanhoMatriz; j++) {
                TextField campo = new TextField();
                campo.setPrefSize(40, 40);
                campo.setStyle("-fx-font-size: 14px; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1;");
                campo.setEditable(false);

                campos[i][j] = campo;
                grid.add(campo, j, i);
            }
        }

        grid.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2;");
        grid.setLayoutX(200);
        grid.setLayoutY(100);
        panePrincipal.getChildren().add(grid);
    }

    private void carregarBoardFixo() {
        if (this.tamanhoMatriz == 16) {
            this.board = new int[][]{
                    {1, 0, 0, 4, 0, 6, 7, 0, 9, 0, 11, 0, 13, 14, 0, 16},
                    {5, 0, 0, 0, 9, 0, 11, 12, 0, 14, 15, 0, 1, 0, 3, 0},
                    {0, 10, 11, 0, 13, 0, 0, 16, 1, 0, 3, 4, 5, 0, 7, 8},
                    {13, 14, 0, 0, 1, 0, 3, 4, 5, 0, 7, 0, 9, 0, 11, 0},

                    {2, 1, 0, 0, 6, 5, 0, 0, 10, 9, 0, 11, 14, 0, 0, 15},
                    {0, 5, 0, 7, 10, 0, 12, 0, 14, 0, 0, 15, 2, 0, 4, 3},
                    {10, 0, 12, 11, 14, 13, 0, 0, 2, 1, 4, 0, 0, 5, 8, 0},
                    {14, 0, 16, 0, 0, 1, 4, 3, 0, 5, 0, 7, 10, 9, 0, 11},

                    {0, 4, 1, 2, 7, 8, 0, 6, 0, 0, 9, 10, 0, 16, 0, 14},
                    {7, 0, 5, 6, 11, 0, 9, 10, 15, 0, 0, 14, 3, 0, 1, 0},
                    {11, 12, 9, 0, 15, 0, 0, 14, 3, 4, 1, 0, 7, 0, 0, 6},
                    {15, 0, 0, 14, 3, 4, 1, 0, 7, 8, 0, 6, 11, 12, 9, 0},

                    {4, 0, 0, 1, 8, 0, 6, 5, 12, 0, 10, 0, 16, 0, 14, 0},
                    {0, 7, 0, 5, 12, 0, 0, 9, 0, 15, 0, 13, 4, 3, 0, 1},
                    {12, 0, 10, 0, 16, 15, 14, 0, 0, 3, 2, 1, 8, 7, 0, 5},
                    {16, 0, 0, 13, 4, 3, 0, 1, 8, 7, 6, 0, 12, 11, 0, 0}
            };
        } else if (this.tamanhoMatriz == 9) {
            this.board = new int[][]{
                    {5, 3, 0, 0, 7, 0, 0, 0, 0},
                    {6, 0, 0, 1, 9, 5, 0, 0, 0},
                    {0, 9, 8, 0, 0, 0, 0, 6, 0},
                    {8, 0, 0, 0, 6, 0, 0, 0, 3},
                    {4, 0, 0, 8, 0, 3, 0, 0, 1},
                    {7, 0, 0, 0, 2, 0, 0, 0, 6},
                    {0, 6, 0, 0, 0, 0, 2, 8, 0},
                    {0, 0, 0, 4, 1, 9, 0, 0, 5},
                    {0, 0, 0, 0, 8, 0, 0, 7, 9}
            };
        }
    }


    @FXML
    private void resolverSudoku(ActionEvent event) {
        new Thread(() -> {
            Sudoku sudoku = new Sudoku(board);
            if (sudoku.solve()) {
                Platform.runLater(this::atualizarInterface);
            } else {
                System.out.println("Nenhuma solução encontrada!");
            }
        }).start();
    }

    private void atualizarInterface() {
        for (int i = 0; i < tamanhoMatriz; i++) {
            for (int j = 0; j < tamanhoMatriz; j++) {
                campos[i][j].setText(board[i][j] == 0 ? "" : String.valueOf(board[i][j]));
            }
        }
    }
}
