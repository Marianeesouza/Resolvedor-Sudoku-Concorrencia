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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TelaInicialController implements Initializable {

    @FXML
    private ComboBox<String> comboMatriz;

    @FXML
    private Button btnIniciar;

    @FXML
    private Text MensagemErro;

    private List<String> opcoes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.comboMatriz.getItems().addAll("16x16", "9x9");
    }

    @FXML
    void iniciarJogo(ActionEvent event) throws IOException {
        if (this.comboMatriz.getSelectionModel().getSelectedItem() == null) {
            this.MensagemErro.setText("Selecione uma opção");
            return;
        }

        TamanhoMatriz.tamanhoMatriz = this.comboMatriz.getSelectionModel().getSelectedItem();
        Main.TrocarTela(new FXMLLoader(Main.class.getResource("telaResolvedorSudoku.fxml")).load());
    }



}
