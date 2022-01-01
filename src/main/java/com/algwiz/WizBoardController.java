package com.algwiz;

import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.*;

public class WizBoardController implements Initializable {

    int tempActionNode = -1;

    ObservableMap<Vertex, ObservableSet<Edge>> vertices = FXCollections.observableHashMap();
    ObservableList<VertexEntry> mapList = FXCollections.observableArrayList();
    MapChangeListener<Vertex, ObservableSet<Edge>> verticesMapListener;

    @FXML
    Pane board;

    @FXML
    ToggleGroup modify;

    @FXML
    ToggleButton addVertexButton;

    @FXML
    ToggleButton removeVertexButton;

    @FXML
    ToggleButton addEdgeButton;

    @FXML
    ToggleButton removeEdgeButton;

    @FXML
    AdjacencyMatrix matrix;

    @FXML
    ComboBox<String> algorithmBox;

    @FXML
    Button executeButton;

    @FXML
    Label dialogLabel;


    public int lowestMissingIndex() {
        int i = 1;

        List<Vertex> vertexList = new ArrayList<>(vertices.keySet());
        Collections.sort(vertexList);

        for(Vertex v : vertexList) {
            if (v.index.get() != i)
                return i;
            i++;
        }

        return vertices.size() + 1;
    }

    public Vertex getTargetVertex(EventTarget target) {
        Parent targetParent = ((Node) target).getParent();
        if ((targetParent instanceof Vertex))
            return (Vertex) targetParent;

        return null;
    }

    public Edge getTargetEdge(EventTarget target) {
        Node targetParent = ((Node) target).getParent();
        if ((targetParent instanceof Edge))
            return (Edge) targetParent;

        return null;
    }

    public void addVertex() {
        removeAllHandlers();
        board.addEventHandler(MouseEvent.MOUSE_CLICKED, addVertexHandler);
    }

    EventHandler<MouseEvent> addVertexHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            double[] coordinates = {mouseEvent.getX(), mouseEvent.getY()};
            Vertex v = new Vertex(coordinates, lowestMissingIndex());
            vertices.put(v, FXCollections.observableSet());
            board.getChildren().add(v);
            matrix.refresh();
        }
    };

    public void removeVertex() {
        removeAllHandlers();
        board.addEventHandler(MouseEvent.MOUSE_CLICKED, removeVertexHandler);
    }

    EventHandler<MouseEvent> removeVertexHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Vertex v = getTargetVertex(mouseEvent.getTarget());
            if (v == null)
                return;

            vertices.replaceAll((key, val) -> {
                val.removeIf(edge -> edge.destination == v || edge.origin == v);
                return val;
            });

            board.getChildren().removeIf(node -> {
                if (node instanceof Edge)
                    return ((Edge) node).destination == v || ((Edge) node).origin == v;

                return false;
            });

            vertices.remove(v);
            board.getChildren().remove(v);
            matrix.refresh();
        }
    };

    public void addEdge() {
        removeAllHandlers();
        board.addEventHandler(MouseEvent.DRAG_DETECTED, startDragEdgeHandler);
        board.addEventHandler(MouseEvent.MOUSE_DRAGGED, dragEdgeHandler);
        board.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, edgeDragReleasedHandler);
    }

    EventHandler<MouseEvent> startDragEdgeHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            board.startFullDrag();
            mouseEvent.consume();
        }
    };

    EventHandler<MouseEvent> dragEdgeHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Vertex v = getTargetVertex(mouseEvent.getTarget());
            if (v == null)
                return;

            if (tempActionNode == -1) {
                Edge e = new Edge(v);
                board.getChildren().add(0, e);
                tempActionNode = board.getChildren().indexOf(e);
            }
            ((Edge) board.getChildren().get(tempActionNode)).setEndXY(mouseEvent.getX(), mouseEvent.getY());

            mouseEvent.consume();
        }
    };

    EventHandler<MouseEvent> edgeDragReleasedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Vertex targetVertex = getTargetVertex(mouseEvent.getTarget());

            if (targetVertex == null && tempActionNode != -1)
                board.getChildren().remove(tempActionNode);
            else if (tempActionNode != -1){
                ((Edge) board.getChildren().get(tempActionNode)).connectEdge(targetVertex);
                Edge e = ((Edge) board.getChildren().get(tempActionNode));

                if (e.origin != e.destination) {
                    vertices.get(e.origin).add(e);
                    matrix.refresh();
                } else
                    board.getChildren().remove(e);
            }

            tempActionNode = -1;
            mouseEvent.consume();
        }
    };

    public void removeEdge() {
        removeAllHandlers();
        board.addEventHandler(MouseEvent.MOUSE_CLICKED, removeEdgeHandler);
    }

    EventHandler<MouseEvent> removeEdgeHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Edge e = getTargetEdge(mouseEvent.getTarget());
            if (e == null)
                return;

            vertices.get(e.origin).remove(e);
            board.getChildren().remove(e);
            matrix.refresh();
        }
    };

    EventHandler<MouseEvent> selectVertexHandler = new EventHandler<>() {
        Vertex v = null;
        Vertex v2 = null;

        @Override
        public void handle(MouseEvent mouseEvent) {
            int choice = algorithmBox.getSelectionModel().getSelectedIndex();

            if (v == null)
                v = getTargetVertex(mouseEvent.getTarget());
            else if (choice == 2)
                v2 = getTargetVertex(mouseEvent.getTarget());

            if (v == null || choice == 2 && v2 == null)
                return;

            switch (choice) {
                case 0:
                    dialogLabel.setText(String.format("%s | Source Vertex: %s\n", algorithmBox.getValue(), v));
                    AlgBook.BFS(vertices, v);
                    break;
                case 1:
                    dialogLabel.setText(String.format("%s | Source Vertex: %s\n", algorithmBox.getValue(), v));
                    AlgBook.DFS(vertices, v);
                    break;
                case 2:
                    dialogLabel.setText(String.format("%s | Source Vertex: %s | Destination Vertex: %s\n", algorithmBox.getValue(), v, v2));
                    AlgBook.dijkstra(vertices, v, v2);
                    break;
            }

            v = null;
            v2 = null;
            dialogLabel.setText("");
            
            board.removeEventHandler(MouseEvent.MOUSE_CLICKED, selectVertexHandler);
            mouseEvent.consume();
        }
    };

    public void removeAllHandlers() {
        board.removeEventHandler(MouseEvent.MOUSE_CLICKED, addVertexHandler);
        board.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeVertexHandler);
        board.removeEventHandler(MouseEvent.DRAG_DETECTED, startDragEdgeHandler);
        board.removeEventHandler(MouseEvent.MOUSE_DRAGGED, dragEdgeHandler);
        board.removeEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, edgeDragReleasedHandler);
        board.removeEventHandler(MouseEvent.MOUSE_CLICKED, selectVertexHandler);
    }

    public void doAlgorithm(int choice) {
        addEdgeButton.getToggleGroup().selectToggle(null);
        removeAllHandlers();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (choice == 0 || choice == 1) {
            alert.setHeaderText(String.format("%s requires a source vertex.", algorithmBox.getValue()));
            alert.setContentText("Please select a source vertex from the board to execute.");
        } else if (choice == 2) {
            alert.setHeaderText(String.format("%s requires a source and destination vertex.", algorithmBox.getValue()));
            alert.setContentText("Please select a source and destination vertex from the board to execute.");
        } else
            return;

        alert.show();
        alert.setOnCloseRequest(dialogEvent -> board.addEventHandler(MouseEvent.MOUSE_CLICKED, selectVertexHandler));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        matrix.setItems(mapList);

        verticesMapListener = change -> {
            if (change.wasAdded())
                mapList.add(new VertexEntry(change.getKey(), change.getValueAdded()));
            if (change.wasRemoved())
                mapList.remove(new VertexEntry(change.getKey(), change.getValueRemoved()));
        };

        vertices.addListener(verticesMapListener);

        algorithmBox.getItems().addAll("Breadth-First Search", "Depth-First Search", "Dijkstra's Shortest Path");

        executeButton.setOnMouseClicked(mouseEvent -> doAlgorithm(algorithmBox.getSelectionModel().getSelectedIndex()));
    }
}
