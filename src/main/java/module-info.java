module com.example.jobtracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;


    opens com.example.jobtracker to javafx.fxml;
    exports com.example.jobtracker;
}