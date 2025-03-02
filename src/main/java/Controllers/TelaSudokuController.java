package Controllers;

import Solver.Sudoku;
import Utils.TamanhoMatriz;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
        carregarBoardAleatorio();  // Chama o método para gerar o tabuleiro aleatório
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

    private void carregarBoardAleatorio() {
        board = new int[tamanhoMatriz][tamanhoMatriz];

        // Gerar um tabuleiro parcialmente preenchido aleatoriamente
        for (int i = 0; i < tamanhoMatriz; i++) {
            for (int j = 0; j < tamanhoMatriz; j++) {
                if (Math.random() > 0.5) {
                    board[i][j] = (int) (Math.random() * 9) + 1; // Preenche com números de 1 a 9
                } else {
                    board[i][j] = 0; // Deixa a célula vazia
                }
            }
        }

        // Se você quiser garantir que o Sudoku gerado seja válido ou ajustado, implemente uma lógica adicional aqui
    }

    @FXML
    private void resolverSudoku(ActionEvent event) {
        new Thread(() -> {
            Sudoku sudoku = new Sudoku(board);
            if (sudoku.solve()) {
                board = sudoku.getBoard();  // Se a solução modificar outra matriz, atualiza aqui.
                Platform.runLater(this::atualizarInterface);
            } else {
                Platform.runLater(() -> {
                    System.out.println("Nenhuma solução encontrada!");
                    exibirAlertaErro("Nenhuma solução foi encontrada para este Sudoku!");
                });
            }
        }).start();
    }

    private void exibirAlertaErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void atualizarInterface() {
        for (int i = 0; i < tamanhoMatriz; i++) {
            for (int j = 0; j < tamanhoMatriz; j++) {
                campos[i][j].setText(board[i][j] == 0 ? "" : String.valueOf(board[i][j]));
            }
        }
    }
}
