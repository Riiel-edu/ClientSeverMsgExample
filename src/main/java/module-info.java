module org.example.clientsevermsgexample {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;


    opens org.example.clientsevermsgexample to javafx.fxml;

    exports org.example.clientsevermsgexample;

}