import java.util.List;
import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class GraphView extends Pane {
    private Graph<? extends Displayable> graph;

    @SuppressWarnings("unchecked")
    public GraphView(Graph<? extends Displayable> graph) {
        this.graph = graph;

        int nodeRadius = 25;
        int size = graph.getSize();
        int[] cx = new int[size];
        int[] cy = new int[size];

        List<? extends Displayable> vertices = graph.getVertices();

        calculateLayeredLayout(cx, cy, vertices);

        drawEdges(cx, cy);

        drawNodes(cx, cy, nodeRadius, vertices);

        drawLegend();

        drawStatistics();
    }

    private void calculateLayeredLayout(int[] cx, int[] cy, List<? extends Displayable> vertices) {
        int canvasWidth = 750;
        int canvasHeight = 350;
        int topMargin = 60;
        int levelSpacing = 70;

        List<Integer> users = new ArrayList<>();
        List<Integer> watchedMovies = new ArrayList<>();
        List<Integer> genres = new ArrayList<>();
        List<Integer> unwatchedMovies = new ArrayList<>();

        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).getClass().getSimpleName().equals("User")) {
                users.add(i);
            } else if (vertices.get(i).getClass().getSimpleName().equals("Genre")) {
                genres.add(i);
            }
        }

        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).getClass().getSimpleName().equals("Movie")) {
                boolean isWatched = false;
                for (int userIdx : users) {
                    if (graph.getNeighbors(userIdx).contains(i)) {
                        isWatched = true;
                        break;
                    }
                }
                if (isWatched) {
                    watchedMovies.add(i);
                } else {
                    unwatchedMovies.add(i);
                }
            }
        }


        positionNodesInLayer(cx, cy, users, canvasWidth, topMargin, 0);

        positionNodesInLayer(cx, cy, watchedMovies, canvasWidth, topMargin + levelSpacing, 0);

        positionNodesInLayer(cx, cy, genres, canvasWidth, topMargin + 2 * levelSpacing, 0);

        positionNodesInLayer(cx, cy, unwatchedMovies, canvasWidth, topMargin + 3 * levelSpacing, 0);
    }

    private void positionNodesInLayer(int[] cx, int[] cy, List<Integer> nodes, int canvasWidth, int y, int offset) {
        if (nodes.isEmpty()) return;

        int leftMargin = 100;
        int rightMargin = 100;
        int availableWidth = canvasWidth - leftMargin - rightMargin;

        if (nodes.size() == 1) {
            cx[nodes.get(0)] = canvasWidth / 2;
            cy[nodes.get(0)] = y;
        } else {
            int spacing = availableWidth / (nodes.size() + 1);
            int startX = leftMargin + spacing;

            for (int i = 0; i < nodes.size(); i++) {
                cx[nodes.get(i)] = startX + i * spacing;
                cy[nodes.get(i)] = y;
            }
        }
    }

    private void drawEdges(int[] cx, int[] cy) {
        int size = graph.getSize();
        List<? extends Displayable> vertices = graph.getVertices();

        for(int i = 0; i < size; i++) {
            List<Integer> neighbors = graph.getNeighbors(i);
            int x1 = cx[i];
            int y1 = cy[i];

            for(int v : neighbors) {
                if (i < v) {
                    int x2 = cx[v];
                    int y2 = cy[v];
                    Line edge = new Line(x1, y1, x2, y2);

                    Displayable node1 = vertices.get(i);
                    Displayable node2 = vertices.get(v);
                    String type1 = node1.getClass().getSimpleName();
                    String type2 = node2.getClass().getSimpleName();

                    if (type1.equals("User") && type2.equals("User")) {
                        edge.setStroke(Color.BLUE);
                        edge.setStrokeWidth(2);
                    } else if (type1.equals("Genre") && type2.equals("Movie") ||
                            type1.equals("Movie") && type2.equals("Genre")) {
                        edge.setStroke(Color.GREEN);
                        edge.setStrokeWidth(2);
                    } else {
                        edge.setStroke(Color.GRAY);
                        edge.setStrokeWidth(1.5);
                    }

                    this.getChildren().add(edge);
                }
            }
        }
    }

    private void drawNodes(int[] cx, int[] cy, int nodeRadius, List<? extends Displayable> vertices) {
        int size = graph.getSize();

        for(int i = 0; i < size; i++) {
            String name = vertices.get(i).getName();
            int x = cx[i];
            int y = cy[i];

            Circle circle = new Circle(x, y, nodeRadius);
            Displayable vertex = vertices.get(i);

            if (vertex.getClass().getSimpleName().equals("User")) {
                circle.setFill(Color.LIGHTBLUE);
                circle.setStroke(Color.DARKBLUE);
            } else if (vertex.getClass().getSimpleName().equals("Genre")) {
                circle.setFill(Color.ORANGE);
                circle.setStroke(Color.DARKORANGE);
                circle.setRadius(nodeRadius + 5);
            } else {
                circle.setFill(Color.LIGHTYELLOW);
                circle.setStroke(Color.ORANGE);
            }

            circle.setStrokeWidth(2);
            this.getChildren().add(circle);

            Text label = new Text(x - name.length() * 4, y - nodeRadius - 5, name);
            label.setFill(Color.BLACK);
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
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
            typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            this.getChildren().add(typeLabel);

        }
    }

    private void drawStatistics() {
        int userCount = 0;
        int movieCount = 0;
        int genreCount = 0;
        int userUserEdges = 0;
        int userMovieEdges = 0;

        List<? extends Displayable> vertices = graph.getVertices();

        for (int i = 0; i < vertices.size(); i++) {
            Displayable vertex = vertices.get(i);
            if (vertex.getClass().getSimpleName().equals("User")) {
                userCount++;
                for (int neighbor : graph.getNeighbors(i)) {
                    if (vertices.get(neighbor).getClass().getSimpleName().equals("User") && i < neighbor) {
                        userUserEdges++;
                    } else if (vertices.get(neighbor).getClass().getSimpleName().equals("Movie")) {
                        userMovieEdges++;
                    }
                }
            } else if (vertex.getClass().getSimpleName().equals("Movie")) {
                movieCount++;
            } else if (vertex.getClass().getSimpleName().equals("Genre")) {
                genreCount++;
            }
        }

        String stats = String.format("Graph Statistics: %d Users, %d Movies, %d Genres, %d User-User connections, %d User-Movie connections",
                userCount, movieCount, genreCount, userUserEdges, userMovieEdges);
        Text statsLabel = new Text(10, 420, stats);
        statsLabel.setFill(Color.DARKGREEN);
        statsLabel.setStyle("-fx-font-weight: bold;");
        this.getChildren().add(statsLabel);

        Text layerInfo = new Text(10, 440, "Layout: Layer 1: Users | Layer 2: Watched Movies | Layer 3: Genres | Layer 4: Unwatched Movies");
        layerInfo.setFill(Color.DARKBLUE);
        layerInfo.setStyle("-fx-font-size: 10px;");
        this.getChildren().add(layerInfo);
    }

    private void drawLegend() {
        int legendX = 10;
        int legendY = 20;
        int spacing = 20;

        Text legend = new Text(legendX, legendY, "Legend:");
        legend.setFill(Color.BLACK);
        legend.setStyle("-fx-font-weight: bold;");
        this.getChildren().add(legend);

        Circle userNode = new Circle(legendX + 10, legendY + spacing, 8);
        userNode.setFill(Color.LIGHTBLUE);
        userNode.setStroke(Color.DARKBLUE);
        Text userText = new Text(legendX + 25, legendY + spacing + 4, "Users (U)");
        userText.setFill(Color.BLACK);
        this.getChildren().add(userNode);
        this.getChildren().add(userText);

        Circle genreNode = new Circle(legendX + 10, legendY + spacing * 2, 10);
        genreNode.setFill(Color.ORANGE);
        genreNode.setStroke(Color.DARKORANGE);
        Text genreText = new Text(legendX + 25, legendY + spacing * 2 + 4, "Genres (G)");
        genreText.setFill(Color.BLACK);
        this.getChildren().add(genreNode);
        this.getChildren().add(genreText);

        Circle movieNode = new Circle(legendX + 10, legendY + spacing * 3, 8);
        movieNode.setFill(Color.LIGHTYELLOW);
        movieNode.setStroke(Color.ORANGE);
        Text movieText = new Text(legendX + 25, legendY + spacing * 3 + 4, "Movies (M)");
        movieText.setFill(Color.BLACK);
        this.getChildren().add(movieNode);
        this.getChildren().add(movieText);

        Line userEdge = new Line(legendX + 10, legendY + spacing * 4, legendX + 20, legendY + spacing * 4);
        userEdge.setStroke(Color.BLUE);
        userEdge.setStrokeWidth(2);
        Text userEdgeText = new Text(legendX + 25, legendY + spacing * 4 + 4, "User-User");
        userEdgeText.setFill(Color.BLACK);
        this.getChildren().add(userEdge);
        this.getChildren().add(userEdgeText);

        Line genreMovieEdge = new Line(legendX + 10, legendY + spacing * 5, legendX + 20, legendY + spacing * 5);
        genreMovieEdge.setStroke(Color.GREEN);
        genreMovieEdge.setStrokeWidth(2);
        Text genreMovieText = new Text(legendX + 25, legendY + spacing * 5 + 4, "Genre-Movie");
        genreMovieText.setFill(Color.BLACK);
        this.getChildren().add(genreMovieEdge);
        this.getChildren().add(genreMovieText);

        Line movieEdge = new Line(legendX + 10, legendY + spacing * 6, legendX + 20, legendY + spacing * 6);
        movieEdge.setStroke(Color.GRAY);
        movieEdge.setStrokeWidth(1.5);
        Text movieEdgeText = new Text(legendX + 25, legendY + spacing * 6 + 4, "User-Movie");
        movieEdgeText.setFill(Color.BLACK);
        this.getChildren().add(movieEdge);
        this.getChildren().add(movieEdgeText);
    }
}