module com.algwiz {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.algwiz to javafx.fxml;
    exports com.algwiz;
}