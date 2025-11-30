import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class BFSGraphView extends Pane {
    private Graph<? extends Displayable> graph;
    private AbstractGraph<? extends Displayable>.Tree bfsTree;
    private int startUserId;

    @SuppressWarnings("unchecked")
    public BFSGraphView(Graph<? extends Displayable> graph, AbstractGraph<? extends Displayable>.Tree bfsTree, int startUserId) {
        this.graph = graph;
        this.bfsTree = bfsTree;
        this.startUserId = startUserId;

        int nodeRadius = 20;
        int size = graph.getSize();
        int[] cx = new int[size];
        int[] cy = new int[size];

        List<? extends Displayable> vertices = graph.getVertices();

        Set<Integer> connectedNodes = getConnectedNodesOnly();

        calculateUnifiedLayout(cx, cy);

        drawOriginalEdges(cx, cy, connectedNodes);

        drawBFSTreeEdges(cx, cy, connectedNodes);

        drawNodes(cx, cy, nodeRadius, vertices, connectedNodes);

        drawBFSOrder(vertices);

        drawMethodInfo();

        drawLegend();
    }

    private Set<Integer> getConnectedNodesOnly() {
        Set<Integer> connectedNodes = new HashSet<>();
        List<? extends Displayable> vertices = graph.getVertices();

        connectedNodes.add(startUserId);

        List<Integer> watchedMovies = getWatchedMovies(startUserId);
        List<Integer> connectedUsers = getConnectedUsers(startUserId);
        connectedNodes.addAll(watchedMovies);
        connectedNodes.addAll(connectedUsers);

        Set<Integer> layer3Set = new HashSet<>();

        for (int movieIdx : watchedMovies) {
            for (int neighbor : graph.getNeighbors(movieIdx)) {
                if (vertices.get(neighbor).getClass().getSimpleName().equals("Genre")) {
                    layer3Set.add(neighbor);
                }
            }
        }

        for (int userIdx : connectedUsers) {
            for (int neighbor : graph.getNeighbors(userIdx)) {
                if (vertices.get(neighbor).getClass().getSimpleName().equals("Movie")) {
                    layer3Set.add(neighbor);
                }
            }
        }
        connectedNodes.addAll(layer3Set);

        Set<Integer> layer4Set = new HashSet<>();
        for (int genreIdx : layer3Set) {
            if (vertices.get(genreIdx).getClass().getSimpleName().equals("Genre")) {
                for (int neighbor : graph.getNeighbors(genreIdx)) {
                    if (vertices.get(neighbor).getClass().getSimpleName().equals("Movie") &&
                            !watchedMovies.contains(neighbor)) {
                        layer4Set.add(neighbor);
                    }
                }
            }
        }
        connectedNodes.addAll(layer4Set);

        return connectedNodes;
    }

    private void calculateUnifiedLayout(int[] cx, int[] cy) {
        int canvasWidth = 750;
        int topMargin = 80;
        int levelSpacing = 90;

        List<? extends Displayable> vertices = graph.getVertices();

        List<Integer> selectedUser = new ArrayList<>();
        selectedUser.add(startUserId);
        positionNodesInLayer(cx, cy, selectedUser, canvasWidth, topMargin);

        List<Integer> watchedMovies = getWatchedMovies(startUserId);
        List<Integer> connectedUsers = getConnectedUsers(startUserId);

        List<Integer> layer2 = new ArrayList<>();
        layer2.addAll(watchedMovies);
        layer2.addAll(connectedUsers);
        positionNodesInLayer(cx, cy, layer2, canvasWidth, topMargin + levelSpacing);

        Set<Integer> layer3Set = new HashSet<>();

        for (int movieIdx : watchedMovies) {
            for (int neighbor : graph.getNeighbors(movieIdx)) {
                if (vertices.get(neighbor).getClass().getSimpleName().equals("Genre")) {
                    layer3Set.add(neighbor);
                }
            }
        }

        for (int userIdx : connectedUsers) {
            for (int neighbor : graph.getNeighbors(userIdx)) {
                if (vertices.get(neighbor).getClass().getSimpleName().equals("Movie")) {
                    layer3Set.add(neighbor);
                }
            }
        }

        List<Integer> layer3 = new ArrayList<>(layer3Set);
        positionNodesInLayer(cx, cy, layer3, canvasWidth, topMargin + 2 * levelSpacing);

        Set<Integer> layer4Set = new HashSet<>();

        for (int genreIdx : layer3Set) {
            if (vertices.get(genreIdx).getClass().getSimpleName().equals("Genre")) {
                for (int neighbor : graph.getNeighbors(genreIdx)) {
                    if (vertices.get(neighbor).getClass().getSimpleName().equals("Movie") &&
                            !watchedMovies.contains(neighbor)) {
                        layer4Set.add(neighbor);
                    }
                }
            }
        }

        List<Integer> layer4 = new ArrayList<>(layer4Set);
        positionNodesInLayer(cx, cy, layer4, canvasWidth, topMargin + 3 * levelSpacing);
    }

    private List<Integer> getWatchedMovies(int userId) {
        List<Integer> watchedMovies = new ArrayList<>();
        List<? extends Displayable> vertices = graph.getVertices();

        for (int neighbor : graph.getNeighbors(userId)) {
            if (vertices.get(neighbor).getClass().getSimpleName().equals("Movie")) {
                watchedMovies.add(neighbor);
            }
        }
        return watchedMovies;
    }

    private List<Integer> getConnectedUsers(int userId) {
        List<Integer> connectedUsers = new ArrayList<>();
        List<? extends Displayable> vertices = graph.getVertices();

        for (int neighbor : graph.getNeighbors(userId)) {
            if (vertices.get(neighbor).getClass().getSimpleName().equals("User") && neighbor != userId) {
                connectedUsers.add(neighbor);
            }
        }
        return connectedUsers;
    }

    private void positionNodesInLayer(int[] cx, int[] cy, List<Integer> nodes, int canvasWidth, int y) {
        if (nodes.isEmpty()) return;

        int leftMargin = 100;
        int rightMargin = 100;
        int availableWidth = canvasWidth - leftMargin - rightMargin;

        if (nodes.size() == 1) {
            cx[nodes.get(0)] = canvasWidth / 2;
            cy[nodes.get(0)] = y;
        } else {
            int minSpacing = 80;
            int idealSpacing = availableWidth / (nodes.size() + 1);
            int actualSpacing = Math.max(minSpacing, idealSpacing);

            int totalWidth = actualSpacing * (nodes.size() - 1);
            int startX = (canvasWidth - totalWidth) / 2;

            startX = Math.max(leftMargin, startX);
            startX = Math.min(startX, canvasWidth - rightMargin - totalWidth);

            for (int i = 0; i < nodes.size(); i++) {
                cx[nodes.get(i)] = startX + i * actualSpacing;
                cy[nodes.get(i)] = y;
            }
        }
    }

    private void drawOriginalEdges(int[] cx, int[] cy, Set<Integer> connectedNodes) {
        List<Integer> searchOrder = bfsTree.getSearchOrder();
        Set<Integer> bfsNodes = new HashSet<>(searchOrder);

        for(int i : bfsNodes) {
            if (!connectedNodes.contains(i)) continue;

            List<Integer> neighbors = graph.getNeighbors(i);
            int x1 = cx[i];
            int y1 = cy[i];

            for(int v : neighbors) {
                if (i < v && bfsNodes.contains(v) && connectedNodes.contains(v)) {
                    int x2 = cx[v];
                    int y2 = cy[v];
                    Line edge = new Line(x1, y1, x2, y2);
                    edge.setStroke(Color.LIGHTGRAY);
                    edge.setStrokeWidth(1);
                    edge.getStrokeDashArray().addAll(3d, 3d);
                    edge.setOpacity(0.6);
                    this.getChildren().add(edge);
                }
            }
        }
    }

    private void drawBFSTreeEdges(int[] cx, int[] cy, Set<Integer> connectedNodes) {
        int size = graph.getSize();
        for(int i = 0; i < size; i++) {
            if (!connectedNodes.contains(i)) continue;

            int parent = bfsTree.getParent(i);
            if(parent != -1 && connectedNodes.contains(parent)) {
                int x1 = cx[parent];
                int y1 = cy[parent];
                int x2 = cx[i];
                int y2 = cy[i];
                Line bfsEdge = new Line(x1, y1, x2, y2);
                bfsEdge.setStroke(Color.CRIMSON);
                bfsEdge.setStrokeWidth(3);
                this.getChildren().add(bfsEdge);
            }
        }
    }

    private void drawNodes(int[] cx, int[] cy, int nodeRadius, List<? extends Displayable> vertices, Set<Integer> connectedNodes) {
        int size = graph.getSize();
        List<Integer> searchOrder = bfsTree.getSearchOrder();
        List<Integer> watchedMovies = getWatchedMovies(startUserId);

        List<Integer> visibleSearchOrder = new ArrayList<>();
        for (int nodeIndex : searchOrder) {
            if (connectedNodes.contains(nodeIndex)) {
                visibleSearchOrder.add(nodeIndex);
            }
        }

        for(int i = 0; i < size; i++) {
            if (!connectedNodes.contains(i)) continue;

            String name = vertices.get(i).getName();
            int x = cx[i];
            int y = cy[i];

            Circle circle = new Circle(x, y, nodeRadius);
            Displayable vertex = vertices.get(i);

            if (i == bfsTree.getRoot()) {
                circle.setFill(Color.LIGHTGREEN);
                circle.setStroke(Color.DARKGREEN);
            } else if (vertex.getClass().getSimpleName().equals("User")) {
                circle.setFill(Color.LIGHTBLUE);
                circle.setStroke(Color.DARKBLUE);
            } else if (vertex.getClass().getSimpleName().equals("Genre")) {
                circle.setFill(Color.ORANGE);
                circle.setStroke(Color.DARKORANGE);
                circle.setRadius(nodeRadius + 3);
            } else if (vertex.getClass().getSimpleName().equals("Movie")) {
                if (watchedMovies.contains(i)) {
                    circle.setFill(Color.LIGHTGRAY);
                    circle.setStroke(Color.GRAY);
                    circle.setOpacity(0.6);
                } else {
                    circle.setFill(Color.LIGHTYELLOW);
                    circle.setStroke(Color.ORANGE);
                }
            }

            circle.setStrokeWidth(2);
            this.getChildren().add(circle);

            Text label = new Text(x - name.length() * 3, y - nodeRadius - 6, name);
            label.setFill(Color.BLACK);
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            if (vertex.getClass().getSimpleName().equals("Movie") && watchedMovies.contains(i)) {
                label.setOpacity(0.6);
            }
            this.getChildren().add(label);

            String typeIndicator;
            if (vertex.getClass().getSimpleName().equals("User")) {
                typeIndicator = "U";
            } else if (vertex.getClass().getSimpleName().equals("Genre")) {
                typeIndicator = "G";
            } else {
                typeIndicator = "M";
            }

            Text typeLabel = new Text(x - 4, y + 4, typeIndicator);
            typeLabel.setFill(Color.WHITE);
            typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            this.getChildren().add(typeLabel);

            if (visibleSearchOrder.contains(i)) {
                int orderIndex = visibleSearchOrder.indexOf(i) + 1;
                Circle orderCircle = new Circle(x + nodeRadius - 5, y - nodeRadius + 5, 8);
                orderCircle.setFill(Color.RED);
                orderCircle.setStroke(Color.DARKRED);
                this.getChildren().add(orderCircle);

                Text orderLabel = new Text(x + nodeRadius - 8, y - nodeRadius + 9, String.valueOf(orderIndex));
                orderLabel.setFill(Color.WHITE);
                orderLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
                this.getChildren().add(orderLabel);
            }

            if (vertex.getClass().getSimpleName().equals("Movie") && !watchedMovies.contains(i) && visibleSearchOrder.contains(i)) {
                Circle recCircle = new Circle(x - nodeRadius + 5, y - nodeRadius + 5, 6);
                recCircle.setFill(Color.GOLD);
                recCircle.setStroke(Color.ORANGE);
                this.getChildren().add(recCircle);

                Text recLabel = new Text(x - nodeRadius + 2, y - nodeRadius + 9, "★");
                recLabel.setFill(Color.DARKRED);
                recLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
                this.getChildren().add(recLabel);
            }
        }
    }

    private void drawBFSOrder(List<? extends Displayable> vertices) {
        List<Integer> searchOrder = bfsTree.getSearchOrder();
        Set<Integer> connectedNodes = getConnectedNodesOnly();

        List<Integer> visibleSearchOrder = new ArrayList<>();
        for (int nodeIndex : searchOrder) {
            if (connectedNodes.contains(nodeIndex)) {
                visibleSearchOrder.add(nodeIndex);
            }
        }

        StringBuilder orderText = new StringBuilder("BFS Traversal Order: ");
        for(int i = 0; i < visibleSearchOrder.size(); i++) {
            int idx = visibleSearchOrder.get(i);
            orderText.append(vertices.get(idx).getName());
            if(i < visibleSearchOrder.size() - 1) {
                orderText.append(" → ");
            }
        }

        Text bfsOrderLabel = new Text(10, 420, orderText.toString());
        bfsOrderLabel.setFill(Color.BLUE);
        bfsOrderLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        this.getChildren().add(bfsOrderLabel);
    }

    private void drawMethodInfo() {

        String layoutInfo = "Layout: Layer 1: Selected User | Layer 2: Watched Movies & Connected Users | Layer 3: Genres & Friend Movies | Layer 4: Recommended Movies";

        Text layoutLabel = new Text(10, 440, layoutInfo);
        layoutLabel.setFill(Color.DARKBLUE);
        layoutLabel.setStyle("-fx-font-size: 10px;");
        this.getChildren().add(layoutLabel);
    }

    private void addLegendCircleItem(int x, int y, double radius, Color fillColor, Color strokeColor,
                                     String text, double opacity) {
        Circle circle = new Circle(x, y, radius);
        circle.setFill(fillColor);
        circle.setStroke(strokeColor);
        circle.setStrokeWidth(2);
        if (opacity < 1.0) {
            circle.setOpacity(opacity);
        }
        this.getChildren().add(circle);

        Text label = new Text(x + 15, y + 4, text);
        label.setFill(Color.BLACK);
        label.setStyle("-fx-font-size: 11px;");
        this.getChildren().add(label);
    }

    private void addLegendCircleItem(int x, int y, double radius, Color fillColor, Color strokeColor, String text) {
        addLegendCircleItem(x, y, radius, fillColor, strokeColor, text, 1.0);
    }

    private void addLegendLineItem(int x, int y, Color strokeColor, double strokeWidth, String text, boolean dashed) {
        Line line = new Line(x, y, x + 10, y);
        line.setStroke(strokeColor);
        line.setStrokeWidth(strokeWidth);
        if (dashed) {
            line.getStrokeDashArray().addAll(3d, 3d);
            line.setOpacity(0.6);
        }
        this.getChildren().add(line);

        Text label = new Text(x + 15, y + 4, text);
        label.setFill(Color.BLACK);
        label.setStyle("-fx-font-size: 11px;");
        this.getChildren().add(label);
    }

    private void addLegendLineItem(int x, int y, Color strokeColor, double strokeWidth, String text) {
        addLegendLineItem(x, y, strokeColor, strokeWidth, text, false);
    }

    private void drawLegend() {
        int legendX = 550;
        int legendY = 20;
        int spacing = 18;
        int itemX = legendX + 10;

        Text legend = new Text(legendX, legendY, "Legend:");
        legend.setFill(Color.BLACK);
        legend.setStyle("-fx-font-weight: bold;");
        this.getChildren().add(legend);

        addLegendCircleItem(itemX, legendY + spacing, 8, Color.LIGHTGREEN, Color.DARKGREEN, "Selected User");
        addLegendCircleItem(itemX, legendY + spacing * 2, 8, Color.LIGHTBLUE, Color.DARKBLUE, "Connected Users (U)");
        addLegendCircleItem(itemX, legendY + spacing * 3, 10, Color.ORANGE, Color.DARKORANGE, "Genres (G)");
        addLegendCircleItem(itemX, legendY + spacing * 4, 8, Color.LIGHTYELLOW, Color.ORANGE, "Unwatched Movies");
        addLegendCircleItem(itemX, legendY + spacing * 5, 8, Color.LIGHTGRAY, Color.GRAY, "Watched Movies", 0.6);

        addLegendLineItem(itemX, legendY + spacing * 6, Color.CRIMSON, 3, "BFS Tree Edges");
        addLegendLineItem(itemX, legendY + spacing * 7, Color.LIGHTGRAY, 1, "Original Edges", true);

        addLegendCircleItem(itemX, legendY + spacing * 8, 6, Color.GOLD, Color.ORANGE, "★ Recommended");
        addLegendCircleItem(itemX, legendY + spacing * 9, 6, Color.RED, Color.DARKRED, "Traversal Order");
    }

    public List<Movie> getRecommendedMovies() {
        List<Movie> recommended = new ArrayList<>();
        List<Integer> watchedMovies = getWatchedMovies(startUserId);
        Set<Integer> connectedNodes = getConnectedNodesOnly();

        List<Integer> searchOrder = bfsTree.getSearchOrder();
        for (int vertexIndex : searchOrder) {
            if (!connectedNodes.contains(vertexIndex)) continue;

            Displayable node = graph.getVertex(vertexIndex);
            if (node instanceof Movie && !watchedMovies.contains(vertexIndex)) {
                recommended.add((Movie) node);
            }
        }

        return recommended;
    }


}