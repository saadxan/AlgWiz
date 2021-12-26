package com.algwiz;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.*;

public class Edge extends StackPane {

    int weight;
    Vertex origin, destination;
    private TextField weightField;
    private Line line;
    private Polygon arrowHead;
    public boolean isCopy = false;

    public Edge(Vertex originVertex) {
        setUpEdgeLine();

        origin = originVertex;
        line.setStartX(originVertex.x);
        line.setStartY(originVertex.y);

        line.setEndX(originVertex.x);
        line.setEndY(originVertex.y);

        setWidth(0);
        setHeight(line.getStrokeWidth());
        setLayoutX((line.getStartX()));
        setLayoutY((line.getStartY()));

        arrowHead = new Polygon();
        arrowHead.getPoints().addAll(0.0, 0.0,
                0.0, 25.0,
                40.0, 12.5);
        arrowHead.setStyle("-fx-fill: tomato; -fx-opacity: 100.0; -fx-stroke: black; -fx-stroke-width: 1px;");
        getChildren().addAll(line, arrowHead, weightField);

        layoutXProperty().bind(line.startXProperty());
        layoutYProperty().bind(line.startYProperty());
    }

    public void setUpEdgeLine() {
        line = new Line();
        line.setStyle("-fx-stroke: darkgray; -fx-stroke-width: 1px; -fx-stroke-type: centered; -fx-stroke-line-join: miter; -fx-stroke-dash-offset: 0.0;");

        weightField = new TextField();
        weightField.setStyle("-fx-background-color: rgba(0,0,0,0); -fx-text-fill: white;");
        weightField.setAlignment(Pos.CENTER);
        weightField.setMaxWidth(45);
        weightField.setMaxHeight(15);
        weightField.setFocusTraversable(false);
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
                this.requestFocus();
            }
        });
    }

    public double getLineTravelAngle() {
        return Math.toDegrees(Math.atan2(line.getEndY() - line.getStartY(), line.getEndX() - line.getStartX()));
    }

    public void setEndXY(double x, double y) {
        if (x < line.getStartX()) {
            layoutXProperty().bind(line.endXProperty());
            if (Math.abs(getLineTravelAngle()) > 84.0 && Math.abs(getLineTravelAngle()) < 98.0) {
                DoubleBinding r = widthProperty().divide(2);
                layoutXProperty().bind(line.endXProperty().subtract(r));
            }
        } else {
            layoutXProperty().bind(line.startXProperty());
            if (Math.abs(getLineTravelAngle()) > 84.0 && Math.abs(getLineTravelAngle()) < 98.0) {
                DoubleBinding r = widthProperty().divide(2);
                layoutXProperty().bind(line.startXProperty().subtract(r));
            }
        }
        
        line.setEndX(x);
        arrowHead.setRotate(getLineTravelAngle());

        if (y < line.getStartY()) {
            layoutYProperty().bind(line.endYProperty());
            if (getLineTravelAngle() > -2 && getLineTravelAngle() < 4) {
                DoubleBinding r = heightProperty().divide(2);
                layoutYProperty().bind(line.endYProperty().subtract(r));
            }
        } else {
            layoutYProperty().bind(line.startYProperty());
            if (Math.abs(getLineTravelAngle()) > 174 && Math.abs(getLineTravelAngle()) < 180) {
                DoubleBinding r = heightProperty().divide(2);
                layoutYProperty().bind(line.startYProperty().subtract(r));
            }
        }

        line.setEndY(y);
        arrowHead.setRotate(getLineTravelAngle());
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public void connectEdge(Vertex targetVertex) {
        setDestination(targetVertex);

        double[] originMidPoint = origin.getMidPoint();
        double[] destinationMidPoint = destination.getMidPoint();
        line.setStartX(originMidPoint[0]);
        line.setStartY(originMidPoint[1]);
        line.setEndX(destinationMidPoint[0]);
        line.setEndY(destinationMidPoint[1]);
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
