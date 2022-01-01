package com.algwiz;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class Edge extends Pane {

    int weight;
    Vertex origin, destination;
    private TextField weightField;
    private Line line;
    private final Polygon arrowHead;

    public Edge(Vertex originVertex) {
        origin = originVertex;

        setPickOnBounds(false);
        setUpEdgeLine();

        line.startXProperty().bind(origin.centerXProperty());
        line.startYProperty().bind(origin.centerYProperty());
        line.setEndX(originVertex.x);
        line.setEndY(originVertex.y);

        arrowHead = new Polygon();
        arrowHead.getPoints().addAll(0.0, 0.0,
                0.0, 25.0,
                40.0, 12.5);
        arrowHead.getStyleClass().add("edge-arrow-head");

        arrowHead.layoutXProperty().bind(((origin.centerXProperty().add(line.endXProperty())).divide(2)).subtract(20));
        arrowHead.layoutYProperty().bind(((origin.centerYProperty().add(line.endYProperty())).divide(2)).subtract(12.5));

        weightField.layoutXProperty().bind(arrowHead.layoutXProperty());
        weightField.layoutYProperty().bind(arrowHead.layoutYProperty());

        getChildren().addAll(line, arrowHead, weightField);
    }

    public void setUpEdgeLine() {
        line = new Line();
        line.getStyleClass().add("edge-line");

        weightField = new TextField();
        weightField.getStyleClass().add("edge-weight-field");
        weightField.setPickOnBounds(true);
        weightField.setFocusTraversable(true);

        ToggleButton addEdgeButtonRef = (ToggleButton) origin.getScene().lookup("#addEdgeButton");
        weightField.editableProperty().bind(addEdgeButtonRef.selectedProperty());
        weightField.disableProperty().bind(addEdgeButtonRef.selectedProperty().not());
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
        line.setEndX(x);
        line.setEndY(y);

        arrowHead.setRotate(getLineTravelAngle());
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public void connectEdge(Vertex targetVertex) {
        setDestination(targetVertex);

        line.setEndX(destination.getMidPoint()[0]);
        line.setEndY(destination.getMidPoint()[1]);
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
