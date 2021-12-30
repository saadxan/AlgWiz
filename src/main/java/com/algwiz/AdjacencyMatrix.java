package com.algwiz;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;

public class AdjacencyMatrix extends TableView<VertexEntry> {

    public AdjacencyMatrix() {
        super();
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        setEditable(false);
        setFocusTraversable(false);
        setPlaceholder(new Label("No Vertices & Edges Detected"));
        setTooltip(new Tooltip("Shows list of all vertices and describes a set of all of their neighboring vertices."));
        setUpMatrix();
    }

    public void setUpMatrix() {
        TableColumn<VertexEntry, String> key = new TableColumn<>("(V)");
        key.setStyle("-fx-alignment: CENTER;");
        key.setMinWidth(50);
        key.setMaxWidth(50);

        key.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getKey().toString()));

        TableColumn<VertexEntry, String> val = new TableColumn<>("->");

        val.setCellValueFactory(item -> {
            StringBuilder tempEdges = new StringBuilder();

            if (item.getValue().getEdges() == null)
                return new SimpleObjectProperty<>("");

            for (Edge e : item.getValue().getEdges())
                tempEdges.append(String.format("%s ", e.destination));

            return new SimpleObjectProperty<>(tempEdges.toString());
        });

        getColumns().add(0, key);
        getColumns().add(1, val);
    }
}
