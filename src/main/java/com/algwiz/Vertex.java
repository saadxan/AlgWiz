package com.algwiz;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.*;

/**
 * WizBoard has x-width of xw, y-height of yh
 * It is positioned at ps as default
 * Vertex has a radius r
 * (ps + r, ps + r)
 *
 * Use dimensions above for left & top corner Vertex translations
 * (xw - (ps + r), yh - (ps + r))
 * Use dimensions above for right & bottom corner Vertex translations
 *
 * i.e. xw = 1024, yh = 576, ps = 10, r = 10
 * (20, 20) for left & top ; net +20
 * (1004, 556) for right & bottom ; net -20
 */

public class Vertex extends StackPane implements Comparable<Vertex> {

    double x;
    double y;
    SimpleIntegerProperty index;
    private final Circle circle;
    private final Set<Integer> incomingEdges;

    public Vertex(double[] coordinates, int indexValue) {
        index = new SimpleIntegerProperty(indexValue);
        incomingEdges = new HashSet<>();

        circle = new Circle();

        circle.setStyle("-fx-fill: yellowgreen; -fx-stroke: black; -fx-stroke-type: inside; -fx-stroke-width: 1px;");
        circle.setRadius(20.0);
        circle.setCenterX(coordinates[0]);
        circle.setCenterY(coordinates[1]);

        if (circle.getCenterX() < 20)
            circle.setCenterX(20);
        else if (circle.getCenterX() > 1004)
            circle.setCenterX(1004);
        if (circle.getCenterY() < 20)
            circle.setCenterY(20);
        else if (circle.getCenterY() > 556)
            circle.setCenterY(556);

        layoutXProperty().bind(circle.centerXProperty().subtract(circle.radiusProperty()));
        layoutYProperty().bind(circle.centerYProperty().subtract(circle.radiusProperty()));

        x = circle.getCenterX();
        y = circle.getCenterY();

        Text indexText = new Text(String.valueOf(index.get()));
        this.getChildren().addAll(circle, indexText);
        this.setAlignment(Pos.CENTER);
    }

    public DoubleProperty centerXProperty() {
        return circle.centerXProperty();
    }

    public DoubleProperty centerYProperty() {
        return circle.centerYProperty();
    }

    public double[] getMidPoint() {
        return new double[]{circle.getCenterX(), circle.getCenterY()};
    }

    public int getIndex() {
        return index.get();
    }

    public SimpleIntegerProperty indexProperty() {
        return index;
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    public void removeIncomingEdge(Edge e) {
        incomingEdges.remove(e.destination.index.get());
    }

    public Circle getCircle() {
        return this.circle;
    }

    @Override
    public int compareTo(Vertex o) {
        return this.index.get() - o.index.get();
    }

    @Override
    public String toString() {
        return String.format("(%d)", index.get());
    }
}
