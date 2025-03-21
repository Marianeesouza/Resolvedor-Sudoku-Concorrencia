package org.example.resolvedor_sudoku_concorrencia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Stage TelaAtual;


    public static void TrocarTela(Parent root) {
        Scene scene = new Scene(root);
        TelaAtual.setScene(scene);
    }

    @Override
    public void start(Stage stage) throws IOException {
        TelaAtual = stage;
        FXMLLoader root = new FXMLLoader(Main.class.getResource("telaInicial.fxml"));

        Image icon = new Image(getClass().getResource("/icon.png").toExternalForm());
        stage.getIcons().add(icon);

        Scene scene = new Scene(root.load());
        stage.setScene(scene);
        stage.setTitle("Sudoku Concorrência");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}