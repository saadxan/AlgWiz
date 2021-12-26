package com.algwiz;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;

public class AdjacencyMatrix extends TableView<VertexEntry> {

    public AdjacencyMatrix() {
        super();
        setEditable(false);
        setFocusTraversable(false);
        setSelectionModel(null);
        setUpMatrix();
    }

    public void setUpMatrix() {
        TableColumn<VertexEntry, String> key = new TableColumn<>("(V)");

        key.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getKey().toString()));
        key.setMaxWidth(30);

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
