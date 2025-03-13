package org.example.resolvedor_sudoku_concorrencia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Stage TelaAtual;


    public static void TrocarTela(Parent root){
        Scene scene = new Scene(root);
        TelaAtual.setScene(scene);
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setMinHeight(768);
        stage.setMinWidth(1024);
        TelaAtual = stage;
        FXMLLoader root = new FXMLLoader(Main.class.getResource("telaInicial.fxml"));
        Scene scene = new Scene(root.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Sudoku Concorrência");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}