package org.example.resolvedor_sudoku_concorrencia;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import Solver.CellUpdateCallback;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}