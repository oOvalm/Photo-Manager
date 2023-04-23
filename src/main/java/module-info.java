module com.demo.photomanage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.controlsfx.controls;

    opens com.demo.photomanage to javafx.fxml;
    exports com.demo.photomanage;
    exports com.demo.photomanage.controller;
    opens com.demo.photomanage.controller to javafx.fxml;
}