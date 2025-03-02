package Controllers;

import Utils.TamanhoMatriz;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.example.resolvedor_sudoku_concorrencia.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TelaInicialController implements Initializable {

    @FXML
    private ComboBox<String> comboMatriz;

    @FXML
    private Button btnIniciar;

    @FXML
    private Text MensagemErro;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Adicionando as opções no ComboBox
        this.comboMatriz.getItems().addAll("16x16", "9x9");
    }

    @FXML
    void iniciarJogo(ActionEvent event) throws IOException {
        // Verifica se o usuário selecionou uma opção
        if (this.comboMatriz.getSelectionModel().getSelectedItem() == null) {
            this.MensagemErro.setText("Selecione uma opção");
            return;
        }

        // Armazena o tamanho da matriz escolhido na classe TamanhoMatriz
        TamanhoMatriz.tamanhoMatriz = this.comboMatriz.getSelectionModel().getSelectedItem();

        // Troca a tela para a tela de resolução do Sudoku
        Main.TrocarTela(new FXMLLoader(Main.class.getResource("telaResolvedorSudoku.fxml")).load());
    }
}
