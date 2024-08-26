package com.example.jobtracker;

import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class LineChartManager extends Parent {
    public XYChart.Series<String, Number> line1 = new XYChart.Series<String, Number>();
    public XYChart.Series<String, Number> line2 = new XYChart.Series<String, Number>();


    private LineChart<String, Number> currentChart;

    public LineChartManager(Stage stage) {
        setCurrentChart(dailyJobsChart());

    }

    public LineChart<String, Number> dailyJobsChart() {

        LineChart<String, Number> lineChart = getBaseChart("Daily Jobs", "Day", "Number of Jobs");

        List<String> s = FileManager.getFileLines();
        int i = 0;
        int sum = 0;
        for (String line : s) {


            String date = line.split(":")[0];

            int value = Integer.parseInt(line.split(":")[1]);
            sum += value;
            line1.getData().add(new XYChart.Data(date, value));

            line2.getData().add(new XYChart.Data<>(date, sum / (i + 1)));
            i++;
        }
        lineChart.getData().add(line1);
        lineChart.getData().add(line2);
        line1.setName("Daily");
        line2.setName("Rolling Average");

        return lineChart;
    }

    public LineChart<String, Number> sevenDayChart() {
        LineChart<String, Number> lineChart = getBaseChart("7 Daily Comparison", "Time Frame", "Number Of Jobs");


        List<String> s = FileManager.getFileLines();
        int i = 0;


        int lastWeekStart = s.size() - 14;
        int thisWeekStart = s.size() - 7;

        for (String line : s) {


            String date = line.split(":")[0];

            int value = Integer.parseInt(line.split(":")[1]);

            if (i >= lastWeekStart && i < thisWeekStart) {


                line1.getData().add(new XYChart.Data(dayOfWeek(date), value));

            } else if (i >= thisWeekStart)

                line2.getData().add(new XYChart.Data<>(dayOfWeek(date), value));
            i++;
        }
        lineChart.getData().add(line1);
        lineChart.getData().add(line2);
        line1.setName("Prev 7 Days");
        line2.setName("Current 7 Days");

        return lineChart;
    }

    public String dayOfWeek(String s) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = null;
        try {
            date = LocalDate.parse(s, formatter);
        } catch (Exception e) {
            return "null";
        }
        DayOfWeek dow = date.getDayOfWeek();  // Extracts a `DayOfWeek` enum object.
        return dow.getDisplayName(TextStyle.SHORT, Locale.US);
    }

    public LineChart<String, Number> weeklyTotalChart() {
        LineChart<String, Number> lineChart = getBaseChart("Weekly Total", "Week", "Number of Jobs Per Week");

        List<String> s = FileManager.getFileLines();
        int i = 0;
        int sum = 0;

        int fullSum = 0;
        for (String line : s) {
            int week = i / 7;

            String date = line.split(":")[0];

            int value = Integer.parseInt(line.split(":")[1]);
            sum += value;

            // System.out.println(week + " " + value);
            if (i % 7 == 0) {

                fullSum += sum;
                line1.getData().add(new XYChart.Data(date, sum));

                line2.getData().add(new XYChart.Data<>(date, fullSum / (i + 1)));
                sum = 0;

            }


            i++;
        }

        lineChart.getData().add(line1);
        lineChart.getData().add(line2);

        line1.setName("Prev 7 Days");
        line2.setName("Current 7 Days");

        return lineChart;
    }

    public LineChart<String, Number> getCurrentChart() {
        return currentChart;
    }


    private LineChart<String, Number> getBaseChart(String title, String xAxisLabel, String YAxisLabel) {

        line1 = new XYChart.Series<String, Number>();
        line2 = new XYChart.Series<String, Number>();

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(YAxisLabel);
        final LineChart<String, Number> lineChart =
                new LineChart<String, Number>(xAxis, yAxis);
        lineChart.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        setOnMouseMove(lineChart);
        lineChart.setTitle(title);


        //populating the series with data


        line1.getData().clear();
        line2.getData().clear();


        return lineChart;

    }

    public void setOnMouseMove(LineChart<String, Number> chart) {

        Axis<String> xAxis = chart.getXAxis();

        Axis<Number> yAxis = chart.getYAxis();
        chart.setOnMouseMoved((MouseEvent event) -> {

                    if (!InputManager.instance.control.get()) {
                        Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                        double closest = 1000;
                        XYChart.Data<String, Number> closestDataPoint = null;
                        XYChart.Data<String, Number> closestDataPoint2 = null;

                        for (int i = 0; i < line1.getData().size(); i++) {
                            XYChart.Data<String, Number> data = line1.getData().get(i);

                            // Convert the data node's layout coordinates to scene coordinates for accurate comparison
                            double dataNodeSceneX = data.getNode().localToScene(data.getNode().getBoundsInLocal()).getMinX();
                            int dist = (int) Math.abs(mouseSceneCoords.getX() - dataNodeSceneX);

                            if (dist < closest) {
                                closest = dist;

                                closestDataPoint = data;
                                closestDataPoint2 = line2.getData().get(i);
                            }
                        }


                        t.setText(
                                closestDataPoint.getXValue() + "(" + dayOfWeek(closestDataPoint.getXValue()) + ")" + "\n" +
                                        line1.getName() + " " + closestDataPoint.getYValue() + ", " + line2.getName() + closestDataPoint2.getYValue());

                    } else {
                        Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());

                        double xInAxis = xAxis.sceneToLocal(mouseSceneCoords).getX();
                        double yInAxis = yAxis.sceneToLocal(mouseSceneCoords).getY();


                        t.setText(
                                ((xAxis.getValueForDisplay(xInAxis)) + ", " +
                                        yAxis.getValueForDisplay(yInAxis)
                                ));
                    }

                }

        );
    }

    Text t;

    public void setCurrentChart(LineChart<String, Number> stringNumberLineChart) {
        this.currentChart = stringNumberLineChart;


    }

    public void setFollower(Text t) {
        this.t = t;
    }
}
