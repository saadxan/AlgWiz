package com.algwiz;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public final class AlgBook {

    static boolean[] visited;
    static LinkedList<Vertex> queue;
    static Stack<Vertex> stack;
    static int[] previous;
    static int[] distance;
    static SequentialTransition seqAnimation;

    public static void BFS(ObservableMap<Vertex, ObservableSet<Edge>> vertices, Vertex u) {
        int max = Collections.max(vertices.keySet()).index.get();
        visited = new boolean[max];
        queue = new LinkedList<>();
        seqAnimation = new SequentialTransition();

        visited [u.index.get()-1] = true;
        queue.add(u);
        seqAnimation.getChildren().add(vertexFill(u));

        while (queue.size() != 0) {
            u = queue.poll();

            for (Edge e : vertices.get(u)) {
                Vertex v = e.destination;

                seqAnimation.getChildren().add(edgeStroke(e));

                if (!visited[v.index.get() - 1]) {
                    visited[v.index.get() - 1] = true;
                    queue.add(v);
                    seqAnimation.getChildren().add(vertexFill(v));
                }
            }
        }

        kickOffAnimation();
    }

    public static void DFS(ObservableMap<Vertex, ObservableSet<Edge>> vertices, Vertex u) {
        int max = Collections.max(vertices.keySet()).index.get();
        visited = new boolean[max];
        stack = new Stack<>();
        seqAnimation = new SequentialTransition();

        stack.push(u);
        seqAnimation.getChildren().add(vertexFill(u));

        while (!stack.isEmpty()) {
            u = stack.pop();
            visited[u.index.get()-1] = true;

            for (Edge v : vertices.get(u)) {
                seqAnimation.getChildren().add(edgeStroke(v));

                if (!visited[v.destination.index.get()-1]) {
                    stack.push(v.destination);
                    seqAnimation.getChildren().add(vertexFill(v.destination));
                }
            }
        }

        kickOffAnimation();
    }

    public static void dijkstra(ObservableMap<Vertex, ObservableSet<Edge>> vertices, Vertex s, Vertex d) {
        int max = Collections.max(vertices.keySet()).index.get();
        queue =  new LinkedList<>();
        previous = new int[max];
        distance = new int[max];
        seqAnimation = new SequentialTransition();
        Stack<Integer> path = minCostPath(vertices, s, d);

        for (Vertex v : vertices.keySet()) {
            previous[v.index.get()-1] = v.index.get() == s.index.get() ? s.index.get() : -1;
            distance[v.index.get()-1] = v.index.get() == s.index.get() ? 0 : Integer.MAX_VALUE;
        }

        queue.add(s);

        while (!queue.isEmpty()) {
            Vertex u = queue.pollFirst();

            if (path.contains(u.index.get()))
                seqAnimation.getChildren().add(vertexFill(u));
            else
                seqAnimation.getChildren().add(vertexUnFill(u));

            if (distance[u.index.get()-1] == Integer.MAX_VALUE)
                break;

            for (Edge a : vertices.get(u)) {
                Vertex v = a.destination;

                final int altDistance = distance[u.index.get()-1] + a.weight;

                if (altDistance < distance[v.index.get()-1]) {
                    queue.remove(v);
                    distance[v.index.get()-1] = altDistance;
                    previous[v.index.get()-1] = u.index.get();
                    queue.add(v);

                    if (path.contains(a.origin.index.get()) && path.contains(a.destination.index.get()))
                        seqAnimation.getChildren().add(edgeStroke(a));
                    else
                        seqAnimation.getChildren().add(edgeUnStroke(a));
                }
            }
        }

        kickOffAnimation();
    }

    private static Stack<Integer> minCostPath(ObservableMap<Vertex, ObservableSet<Edge>> vertices, Vertex s, Vertex d) {
        int max = Collections.max(vertices.keySet()).index.get();
        queue =  new LinkedList<>();
        previous = new int[max];
        distance = new int[max];
        seqAnimation = new SequentialTransition();

        for (Vertex v : vertices.keySet()) {
            previous[v.index.get()-1] = v.index.get() == s.index.get() ? s.index.get() : -1;
            distance[v.index.get()-1] = v.index.get() == s.index.get() ? 0 : Integer.MAX_VALUE;
        }

        queue.add(s);

        while (!queue.isEmpty()) {
            Vertex u = queue.pollFirst();

            if (distance[u.index.get()-1] == Integer.MAX_VALUE)
                break;

            for (Edge a : vertices.get(u)) {
                Vertex v = a.destination;

                final int altDistance = distance[u.index.get()-1] + a.weight;

                if (altDistance < distance[v.index.get()-1]) {
                    queue.remove(v);
                    distance[v.index.get()-1] = altDistance;
                    previous[v.index.get()-1] = u.index.get();
                    queue.add(v);
                }
            }
        }

        Stack<Integer> path = new Stack<>();
        int curVertex = d.index.get();

        if (previous[curVertex-1] == -1)
            return path;

        path.add(curVertex);

        while (curVertex != s.index.get()) {
            curVertex = previous[curVertex-1];
            path.add(curVertex);
        }

        return path;
    }

    private static FillTransition vertexFill(Vertex v) {
        return new FillTransition(Duration.millis(1000), v.getCircle(), Color.YELLOWGREEN, Color.OLIVE);
    }

    private static ParallelTransition edgeStroke(Edge e) {
        StrokeTransition lineStroke =  new StrokeTransition(Duration.millis(1000), e.getLine(), Color.DARKGRAY, Color.BLACK);
        FillTransition arrowFill =  new FillTransition(Duration.millis(1000), e.getArrowHead(), Color.TOMATO, Color.CRIMSON);
        return new ParallelTransition(lineStroke, arrowFill);
    }

    private static FillTransition vertexUnFill(Vertex v) {
        return new FillTransition(Duration.millis(1000), v.getCircle(), Color.YELLOWGREEN, Color.LIGHTGRAY);
    }

    private static ParallelTransition edgeUnStroke(Edge e) {
        StrokeTransition lineStroke =  new StrokeTransition(Duration.millis(1000), e.getLine(), Color.DARKGRAY, Color.LIGHTGRAY);
        FillTransition arrowFill =  new FillTransition(Duration.millis(1000), e.getArrowHead(), Color.TOMATO, Color.LIGHTGRAY);
        return new ParallelTransition(lineStroke, arrowFill);
    }

    private static void kickOffAnimation() {
        seqAnimation.play();

        seqAnimation.setOnFinished(actionEvent -> Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to replay animation?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> choice = alert.showAndWait();

            if (choice.isEmpty()) {
                return;
            }

            if (choice.get() == ButtonType.YES)
                seqAnimation.play();
            else if (choice.get() == ButtonType.NO) {
                seqAnimation.play();
                seqAnimation.jumpTo(Duration.ZERO);
                seqAnimation.stop();
            }
        }));
    }
}
