module com.devtty {
    requires java.net.http;
    requires javafx.fxml;
    requires javafx.controls;

    opens com.devtty to javafx.fxml;
    exports com.devtty;
}
