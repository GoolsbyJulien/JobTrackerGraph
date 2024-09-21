package com.example.jobtracker;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LineChartManager extends Parent {
    private static final String[] DAYS_OF_WEEK = {"Mon", "Tues", "Wed", "Thur", "Fri", "Sat", "Sun"};
    private final Stage barChartStage = new Stage();
    public XYChart.Series<String, Number> line1 = new XYChart.Series<>();
    public XYChart.Series<String, Number> line2 = new XYChart.Series<>();
    Text t;
    //    double mouseDragStartX, mouseDragStartY;
//    double mouseDragEndX, mouseDragEndY;
    private boolean showLine2 = true;
    private boolean showLine1 = true;
    private LineChart<String, Number> currentChart;
    private List<String> fileLines;

    public LineChartManager() {
        reloadData();
    }

    public LineChart<String, Number> dailyJobsChart(int limited) {

        LineChart<String, Number> lineChart = getBaseChart("Daily Jobs", "Day", "Number of Jobs");

        int startValue = limited != -1 ? fileLines.size() - limited : 0;
        int sum = 0;
        for (int i = 0; i < fileLines.size(); i++) {

            String line = fileLines.get(i);

            String date = line.split(":")[0];

            int value = Integer.parseInt(line.split(":")[1]);
            sum += value;

            if (i >= startValue) {
                line1.getData().add(new XYChart.Data<>(date, value));

                line2.getData().add(new XYChart.Data<>(date, sum / (i + 1)));
            }
        }
        lineChart.getData().add(line1);
        lineChart.getData().add(line2);
        line1.setName("Daily");
        line2.setName("Rolling Average");
        return lineChart;
    }


    public LineChart<String, Number> sevenDayChart() {
        LineChart<String, Number> lineChart = getBaseChart("7 Daily Comparison", "Time Frame", "Number Of Jobs");


        int lastWeekStart = fileLines.size() - 14;
        int thisWeekStart = fileLines.size() - 7;

        for (int i = lastWeekStart; i < fileLines.size(); i++) {

            String line = fileLines.get(i);

            String date = line.split(":")[0];

            int value = Integer.parseInt(line.split(":")[1]);

            if (i < thisWeekStart) {


                line1.getData().add(new XYChart.Data<>(dayOfWeek(date), value));

            } else line2.getData().add(new XYChart.Data<>(dayOfWeek(date), value));
        }
        lineChart.getData().add(line1);
        lineChart.getData().add(line2);
        line1.setName("Prev 7 Days");
        line2.setName("Current 7 Days");

        return lineChart;
    }

    public LineChart<String, Number> weeklyTotalChart() {
        LineChart<String, Number> lineChart = getBaseChart("Weekly Total", "Week", "Number of Jobs Per Week");


        int sum = 0;

        int fullSum = 0;

        String firstDay = null;
        for (int i = 0; i < fileLines.size(); i++) {

            String line = fileLines.get(i);
            String date = line.split(":")[0];
            if (firstDay == null)
                firstDay = date;
            int value = Integer.parseInt(line.split(":")[1]);
            sum += value;


            // System.out.println("day of week " + i % 7);
            // System.out.println(week + " " + value);
            if (i % 7 == 6 /*|| i == s.size() - 1*/) {

                fullSum += sum;
                line1.getData().add(new XYChart.Data<>(firstDay + " - " + date, sum));

                line2.getData().add(new XYChart.Data<>(firstDay + " - " + date, fullSum / (i / 6)));
                sum = 0;
                firstDay = null;

            }


        }

        lineChart.getData().add(line1);
        lineChart.getData().add(line2);

        line1.setName("Weekly Total");
        line2.setName("Weekly Average");

        return lineChart;
    }

    public LineChart<String, Number> SevenDayVSAVG() {
        LineChart<String, Number> lineChart = getBaseChart("7 Daily Comparison", "Time Frame", "Number Of Jobs");


        int[] dailyAverages = getDailyAverages();

        int startDay = fileLines.size() - 7;

        lineChart.getData().add(line1);
        lineChart.getData().add(line2);

        for (int i = startDay; i < fileLines.size(); i++) {

            String line = fileLines.get(i);

            String date = line.split(":")[0];

            int value = Integer.parseInt(line.split(":")[1]);


            line1.getData().add(new XYChart.Data<>(dayOfWeek(date), value));


            line2.getData().add(new XYChart.Data<>(dayOfWeek(date), dailyAverages[dayOfWeekInt(date)]));
        }
        line1.setName("Prev Week");
        line2.setName("Day Average");

        return lineChart;
    }

    public LineChart<String, Number> getCurrentChart() {
        return currentChart;
    }

    public void setCurrentChart(LineChart<String, Number> stringNumberLineChart) {
        this.currentChart = stringNumberLineChart;


        System.out.println(line1.getData().size() + " " + line2.getData().size());
        for (int i = 0; i < line1.getData().size(); i++) {
            line1.getData().get(i).getNode().setOpacity(0);
            line2.getData().get(i).getNode().setOpacity(0);

        }


        for (Node legend : currentChart.lookupAll(".chart-legend-item")) {

            Label label = (Label) legend;

            label.setOnMouseClicked(e -> {
                // System.out.println(label.getText());
                if (label.getText().equals(line1.getName())) {

                    if (showLine1) {
//                        for (XYChart.Data<String, Number> data : line1.getData()) {
//                            //  data.getNode().setOpacity(0); // Set opacity to 50%
//                        }
                        label.setOpacity(0.5);
                        line1.getNode().lookup(".chart-series-line").setStyle("-fx-opacity: 0.2;");

                    } else {

//                        for (XYChart.Data<String, Number> data : line1.getData()) {
//                            //  data.getNode().setOpacity(1); // Set opacity to 50%
//                        }
                        label.setOpacity(1);
                        line1.getNode().lookup(".chart-series-line").setStyle("-fx-opacity: 1;");
                    }
                    showLine1 = !showLine1;
                }
                if (label.getText().equals(line2.getName())) {
                    if (showLine2) {
//                        for (XYChart.Data<String, Number> data : line2.getData()) {
//                            //data.getNode().setOpacity(0); // Set opacity to 50%
//                        }
                        label.setOpacity(0.5);
                        line2.getNode().lookup(".chart-series-line").setStyle("-fx-opacity: 0.2;");

                    } else {

//                        for (XYChart.Data<String, Number> data : line2.getData()) {
//                            //data.getNode().setOpacity(1); // Set opacity to 50%
//                        }
                        label.setOpacity(1);
                        line2.getNode().lookup(".chart-series-line").setStyle("-fx-opacity: 1;");
                    }
                    showLine2 = !showLine2;
                }
            });


        }

    }

    private LineChart<String, Number> getBaseChart(String title, String xAxisLabel, String YAxisLabel) {

        line1 = new XYChart.Series<>();
        line2 = new XYChart.Series<>();
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(YAxisLabel);
        final LineChart<String, Number> lineChart =
                new LineChart<>(xAxis, yAxis);
        setOnMouseMove(lineChart);
        lineChart.setTitle(title);

        lineChart.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        //populating the series with data


        line1.getData().clear();
        line2.getData().clear();

        return lineChart;

    }

    public void barStage() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();

        int[] dailyAverages = getDailyAverages();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setStyle("-fx-background-color: black; -fx-text-fill: white;");


        for (int i = 0; i < dailyAverages.length; i++)
            series1.getData().add(new XYChart.Data<>(DAYS_OF_WEEK[i] + "\n" + dailyAverages[i], dailyAverages[i]));


        barChart.getData().add(series1);


        series1.getData().get(0).getNode().setOnMouseClicked(s -> {


            System.out.println(0);
        });
        series1.getData().get(1).getNode().setOnMouseClicked(s -> {

            System.out.println(1);
        });

        series1.getData().get(1).getNode().setOnMouseClicked(s -> {

            System.out.println(1);
        });
        series1.getData().get(3).getNode().setOnMouseClicked(s -> {

            System.out.println(4);
        });
        series1.getData().get(5).getNode().setOnMouseClicked(s -> {

            System.out.println(6);
        });


        Scene sc = new Scene(barChart);
        sc.getStylesheets().add("style.css");

        barChartStage.setScene(sc);
        barChartStage.show();
    }

    private void setOnMouseMove(LineChart<String, Number> chart) {

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
                            line1.getData().get(i).getNode().setOpacity(0);
                            line2.getData().get(i).getNode().setOpacity(0);

                            if (dist < closest) {
                                closest = dist;

                                closestDataPoint = data;
                                closestDataPoint2 = line2.getData().get(i);
                            }
                        }

                        if (closestDataPoint == null || closestDataPoint2 == null)
                            return;

                        if (!dayOfWeek(closestDataPoint.getXValue()).equals("null")) {

                            t.setText(
                                    closestDataPoint.getXValue() + "(" + dayOfWeek(closestDataPoint.getXValue()) + ")" + "\n" +
                                            line1.getName() + " " + closestDataPoint.getYValue() + ", " + line2.getName() + " " + closestDataPoint2.getYValue());
                        } else {
                            // TODO Display how many days ago the weekday was
                            t.setText(
                                    closestDataPoint.getXValue() + "\n" +
                                            line1.getName() + " " + closestDataPoint.getYValue() + ", " + line2.getName() + " " + closestDataPoint2.getYValue());

                        }

                        closestDataPoint.getNode().setOpacity(1);
                        closestDataPoint2.getNode().setOpacity(1);

                    } else {
                        Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());

                        double xInAxis = xAxis.sceneToLocal(mouseSceneCoords).getX();
                        int yInAxis = (int) yAxis.sceneToLocal(mouseSceneCoords).getY();


                        t.setText(
                                ((xAxis.getValueForDisplay(xInAxis)) + ", " +
                                        (yAxis.getValueForDisplay(yInAxis).intValue())
                                ));
                    }

                }


        );
    }

    private String dayOfWeek(String s) {

        int dayOfWeek = dayOfWeekInt(s);
        if (dayOfWeek != -1)
            return DAYS_OF_WEEK[dayOfWeek];
        else
            return "null";
    }

    private int dayOfWeekInt(String s) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date;
        try {
            date = LocalDate.parse(s, formatter);
        } catch (Exception e) {
            return -1;
        }
        DayOfWeek dow = date.getDayOfWeek();  // Extracts a `DayOfWeek` enum object.
        //System.out.println(dow.getValue());
        return dow.getValue() - 1;
    }

    private int[] getDailyAverages() {

        int[] dailyAverages = {0, 0, 0, 0, 0, 0, 0};
        int[] dailySum = {0, 0, 0, 0, 0, 0, 0};
        int[] dailyNumber = {0, 0, 0, 0, 0, 0, 0};

        for (String line : FileManager.getFileLines()) {

            String date = line.split(":")[0];
            int dayOfWeekNum = dayOfWeekInt(date);
            int value = Integer.parseInt(line.split(":")[1]);
            dailySum[dayOfWeekNum] += value;
            dailyNumber[dayOfWeekNum]++;
            dailyAverages[dayOfWeekNum] = dailySum[dayOfWeekNum] / dailyNumber[dayOfWeekNum];
        }


        return dailyAverages;
    }

    public void reloadData() {
        fileLines = FileManager.getFileLines();
        setCurrentChart(dailyJobsChart(-1));

    }

    public void setFollower(Text t) {
        this.t = t;
    }

}
