module chisli {
    requires chislib;
    
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens chisli to javafx.fxml;
    exports chisli;

    requires javafx.swing;
    requires java.desktop; 
}
