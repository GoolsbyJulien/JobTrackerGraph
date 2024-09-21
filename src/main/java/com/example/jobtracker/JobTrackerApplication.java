package com.example.jobtracker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;

public class JobTrackerApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    private static Menu getOptionsMenu(LineChartManager lm) {
        Menu menu = new Menu("Settings");

        MenuItem toggleRollingAverage = new MenuItem("Edit File");
        toggleRollingAverage.setOnAction(actionEvent -> {

            FileManager.openFile();
            actionEvent.consume();
        });

        MenuItem reloadData = new MenuItem("Reload Data");
        reloadData.setOnAction(actionEvent -> {


            lm.reloadData();
            actionEvent.consume();
        });
        menu.getItems().add(toggleRollingAverage);
        menu.getItems().add(reloadData);

        return menu;
    }

    private static Menu getWindowsMenu(LineChartManager lm) {
        Menu menu = new Menu("Windows");

        MenuItem toggleRollingAverage = new MenuItem("Daily Averages");
        toggleRollingAverage.setOnAction(actionEvent -> {

            lm.barStage();

            actionEvent.consume();
        });
        menu.getItems().add(toggleRollingAverage);
        return menu;
    }

    private static Menu geViewMenu(StackPane sPane, LineChartManager lm) {
        Menu menu = new Menu("View");
        ToggleGroup toggleGroup = new ToggleGroup();


        RadioMenuItem dailyChartView = new RadioMenuItem("Daily Chart");
        dailyChartView.setOnAction(actionEvent -> {


            changeChart(sPane, lm, 0);

            actionEvent.consume();
        });


        RadioMenuItem sevenDayView = new RadioMenuItem("Seven Day Chart");
        sevenDayView.setOnAction(actionEvent -> {


            changeChart(sPane, lm, 1);

            actionEvent.consume();
        });

        RadioMenuItem weeklyTotalView = new RadioMenuItem("Weekly Total Chart");
        weeklyTotalView.setOnAction(actionEvent -> {


            changeChart(sPane, lm, 2);

            actionEvent.consume();
        });

        RadioMenuItem sevenDayVSAVGView = new RadioMenuItem("Seven Day / AVG");
        sevenDayVSAVGView.setOnAction(actionEvent -> {


            changeChart(sPane, lm, 3);

            actionEvent.consume();
        });
        weeklyTotalView.setToggleGroup(toggleGroup);
        sevenDayView.setToggleGroup(toggleGroup);
        dailyChartView.setToggleGroup(toggleGroup);
        sevenDayVSAVGView.setToggleGroup(toggleGroup);

        toggleGroup.selectToggle(dailyChartView);

        menu.getItems().add(dailyChartView);
        menu.getItems().add(sevenDayView);
        menu.getItems().add(weeklyTotalView);
        menu.getItems().add(sevenDayVSAVGView);


        return menu;
    }

    private static void changeChart(StackPane sPane, LineChartManager lm, int newChart) {
        sPane.getChildren().remove(lm.getCurrentChart());


        switch (newChart) {

            case 0:
                lm.setCurrentChart(lm.dailyJobsChart(-1));
                break;

            case 1:
                lm.setCurrentChart(lm.sevenDayChart());
                break;

            case 2:
                lm.setCurrentChart(lm.weeklyTotalChart());
                break;
            case 3:
                lm.setCurrentChart(lm.SevenDayVSAVG());

        }

        sPane.getChildren().add(lm.getCurrentChart());
        lm.getCurrentChart().toBack();
        VBox.setVgrow(sPane, Priority.ALWAYS);
    }

    @Override
    public void start(Stage stage) {

        //creating the chart
        stage.setTitle("Job Tracker //// " + LocalDate.now());
        stage.setOnCloseRequest(t -> System.exit(0));
        LineChartManager lm = new LineChartManager();

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
        Menu windowsMenu = getWindowsMenu(lm);

        Menu viewMenu = geViewMenu(sPane, lm);

        VBox vBox = new VBox(menuBar, sPane);

        VBox.setVgrow(sPane, Priority.ALWAYS);
        vBox.setPadding(Insets.EMPTY);  // Remove padding
        vBox.setSpacing(0);
        Scene scene = new Scene(vBox, 800, 600);
        new InputManager(scene);


        menuBar.getMenus().add(optionsMenu);
        menuBar.getMenus().add(viewMenu);
        menuBar.getMenus().add(windowsMenu);


        scene.getStylesheets().add("style.css");
        stage.setScene(scene);
        stage.show();
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
}