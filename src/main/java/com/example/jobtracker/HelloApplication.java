package com.example.jobtracker;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {

        //creating the chart

        stage.setTitle("Line Chart Sample");

        LineChartManager lm = new LineChartManager(stage);

        final LineChart<String, Number> lineChart = lm.getCurrentChart();


        Text t = new Text("");
        StackPane.setAlignment(t, Pos.TOP_LEFT);

        t.setX(500);
        t.setY(200);
        t.setFill(Color.WHITE);
        t.setId("cursorViewer");

        StackPane sPane = new StackPane(lineChart, t);
        lm.setFollower(t);

        MenuBar menuBar = new MenuBar();
        Menu optionsMenu = getOptionsMenu(lm);

        Menu viewMenu = geViewMenu(sPane, lm);

        VBox vBox = new VBox(menuBar, sPane);

        VBox.setVgrow(sPane, Priority.ALWAYS);
        vBox.setPadding(Insets.EMPTY);  // Remove padding
        vBox.setSpacing(0);
        Scene scene = new Scene(vBox, 800, 600);
        new InputManager(scene);


        menuBar.getMenus().add(optionsMenu);
        menuBar.getMenus().add(viewMenu);


        scene.getStylesheets().add("style.css");
        stage.setScene(scene);
        stage.show();
    }


    private static Menu getOptionsMenu(LineChartManager lm) {
        Menu menu = new Menu("Options");

        MenuItem toggleRollingAverage = new MenuItem("Toggle Rolling Average");
        toggleRollingAverage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {


                actionEvent.consume();
            }
        });
        menu.getItems().add(toggleRollingAverage);
        return menu;
    }

    private static Menu geViewMenu(StackPane sPane, LineChartManager lm) {
        Menu menu = new Menu("View");
        ToggleGroup toggleGroup = new ToggleGroup();


        RadioMenuItem dailyChartView = new RadioMenuItem("Daily Chart");
        dailyChartView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {


                changeChart(sPane, lm, 0);

                actionEvent.consume();
            }


        });


        RadioMenuItem sevenDayView = new RadioMenuItem("Seven Day Chart");
        sevenDayView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {


                changeChart(sPane, lm, 1);

                actionEvent.consume();
            }


        });

        RadioMenuItem weeklyTotalView = new RadioMenuItem("Weekly Total Chart");
        weeklyTotalView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {


                changeChart(sPane, lm, 2);

                actionEvent.consume();
            }


        });
        weeklyTotalView.setToggleGroup(toggleGroup);
        sevenDayView.setToggleGroup(toggleGroup);
        dailyChartView.setToggleGroup(toggleGroup);
        toggleGroup.selectToggle(dailyChartView);

        menu.getItems().add(dailyChartView);
        menu.getItems().add(sevenDayView);
        menu.getItems().add(weeklyTotalView);

        return menu;
    }

    private static void changeChart(StackPane sPane, LineChartManager lm, int newChart) {
        sPane.getChildren().remove(lm.getCurrentChart());


        System.out.println(newChart);
        switch (newChart) {

            case 0:
                lm.setCurrentChart(lm.dailyJobsChart());
                break;

            case 1:
                lm.setCurrentChart(lm.sevenDayChart());
                break;

            case 2:
                lm.setCurrentChart(lm.weeklyTotalChart());
                break;

        }

        sPane.getChildren().add(lm.getCurrentChart());
        lm.getCurrentChart().toBack();
        VBox.setVgrow(sPane, Priority.ALWAYS);
    }

    public void toggleLine(XYChart.Series<String, Number> series) {
        // Perform the toggle action here
        series.getNode().setVisible(!series.getNode().isVisible());
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getNode() != null) {
                data.getNode().setVisible(!data.getNode().isVisible());  // Hides the dots
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}