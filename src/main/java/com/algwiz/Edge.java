package com.algwiz;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class Edge extends Region {

    int weight;
    Vertex origin, destination;
    private TextField weightField;
    private Line line;
    private Polygon arrowHead;
    public boolean isCopy = false;

    public Edge(Vertex originVertex) {
        setUpEdgeLine();

        origin = originVertex;

        line.startXProperty().bind(origin.centerXProperty());
        line.startYProperty().bind(origin.centerYProperty());
        line.setEndX(originVertex.x);
        line.setEndY(originVertex.y);

        arrowHead = new Polygon();
        arrowHead.getPoints().addAll(0.0, 0.0,
                0.0, 25.0,
                40.0, 12.5);
        arrowHead.setStyle("-fx-fill: tomato; -fx-opacity: 100.0; -fx-stroke: black; -fx-stroke-width: 1px;");

        getChildren().addAll(line);
    }

    public void setUpEdgeLine() {
        line = new Line();
        line.setStyle("-fx-stroke: darkgray; -fx-stroke-width: 1px; -fx-stroke-type: centered; -fx-stroke-line-join: miter; -fx-stroke-dash-offset: 0.0;");

        weightField = new TextField();
        weightField.setStyle("-fx-fill: blue; -fx-background-color: rgba(0,0,0,1); -fx-text-fill: white;");
        weightField.setAlignment(Pos.CENTER);
        weightField.setMaxWidth(45);
        weightField.setMaxHeight(15);
        weightField.setFocusTraversable(true);
        weightField.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.matches("\\d*"))
                weightField.setText(t1.replaceAll("[^\\d]", ""));
            else if (!weightField.getText().isBlank())
                weight = Integer.parseInt(weightField.getText());
        });
        weightField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (weightField.getText().isBlank())
                    weightField.setText("0");
                weight = Integer.parseInt(weightField.getText());
                weightField.requestFocus();
            }
        });
    }

    public double getLineTravelAngle() {
        return Math.toDegrees(Math.atan2(line.getEndY() - line.getStartY(), line.getEndX() - line.getStartX()));
    }

    public void setEndXY(double x, double y) {
        line.setEndX(x);
        line.setEndY(y);
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public void connectEdge(Vertex targetVertex) {
        setDestination(targetVertex);

        line.setEndX(destination.getMidPoint()[0]);
        line.setEndY(destination.getMidPoint()[1]);

        arrowHead.setRotate(getLineTravelAngle());
        arrowHead.layoutXProperty().bind(((origin.centerXProperty().add(destination.centerXProperty())).divide(2)).subtract(20));
        arrowHead.layoutYProperty().bind(((origin.centerYProperty().add(destination.centerYProperty())).divide(2)).subtract(12.5));

        weightField.layoutXProperty().bind(arrowHead.layoutXProperty());
        weightField.layoutYProperty().bind(arrowHead.layoutYProperty());

        getChildren().addAll(arrowHead, weightField);
    }

    public Line getLine() {
        return this.line;
    }

    public Polygon getArrowHead() {
        return this.arrowHead;
    }

    @Override
    public String toString() {
        return String.format("--<%d>-> %s", weight, destination);
    }
}
