import javafx.application.Platform;
import javafx.scene.Scene;

public class VisualizationManager {
    private final Graph<Node> graph;
    private final Main app;

    public VisualizationManager(Graph<Node> graph, Main app) {
        this.graph = graph;
        this.app = app;
    }

    public void showGraphVisualization() {
        Platform.runLater(() -> {
            Scene scene = new Scene(new GraphView(graph), 750, 450);
            app.getPrimaryStage().setTitle("Movie Network - Graph Visualization");
            app.getPrimaryStage().setScene(scene);
            app.getPrimaryStage().show();
        });
    }

    public void showMovieGraph(AbstractGraph<Node>.Tree bfsTree, int userId) {
        Platform.runLater(() -> {
            Scene scene = new Scene(new BFSGraphView(graph, bfsTree, userId), 750, 450);
            app.getPrimaryStage().setTitle("Movie Network - BFS Visualization");
            app.getPrimaryStage().setScene(scene);
            app.getPrimaryStage().show();
        });
    }
}
