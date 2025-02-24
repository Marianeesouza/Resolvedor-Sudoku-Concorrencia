module org.example.resolvedor_sudoku_concorrencia {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens org.example.resolvedor_sudoku_concorrencia to javafx.fxml;
    exports org.example.resolvedor_sudoku_concorrencia;
}