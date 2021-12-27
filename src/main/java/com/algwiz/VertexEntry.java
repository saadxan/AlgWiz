package com.algwiz;

import javafx.collections.ObservableSet;

import java.util.AbstractMap;

class VertexEntry extends AbstractMap.SimpleEntry<Vertex, ObservableSet<Edge>> implements Comparable<VertexEntry>{

    Vertex vertex, key;
    ObservableSet<Edge> edges, value;

    public VertexEntry(Vertex key, ObservableSet<Edge> value) {
        super(key, value);
        this.key = vertex = key;
        this.value = edges = value;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    public ObservableSet<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ObservableSet<Edge> edges) {
        this.edges = edges;
    }

    @Override
    public int compareTo(VertexEntry o) {
        return this.key.compareTo(o.key);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof VertexEntry && this.getKey() == ((VertexEntry) o).getKey();
    }
}
