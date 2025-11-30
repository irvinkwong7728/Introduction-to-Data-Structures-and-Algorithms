import java.util.*;

public class RecommendationHandler {
    private final Graph<Node> graph;
    private final Main app;
    private final Scanner sc;

    public RecommendationHandler(Graph<Node> graph, Main app, Scanner sc) {
        this.graph = graph;
        this.app = app;
        this.sc = sc;
    }

    public void handleRecommendations() {
        System.out.println("\n=== List of Users ===");
        List<Integer> userVertexIndices = new ArrayList<>();

        int displayId = 1;
        for (int i = 0; i < graph.getSize(); i++) {
            Node node = graph.getVertex(i);
            if (node instanceof User) {
                System.out.println(displayId + ". " + node.getName());
                userVertexIndices.add(i);
                displayId++;
            }
        }

        if (userVertexIndices.isEmpty()) {
            System.out.println("No users available in the system.");
            return;
        }

        int choice = -1;
        while (true) {
            System.out.print("\nEnter the number to get movie recommendations: ");
            String input = sc.nextLine();

            try {
                choice = Integer.parseInt(input);
                if (choice < 1 || choice > userVertexIndices.size()) {
                    System.out.println("Invalid choice. Please choose from the list above.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }

        int userVertexIndex = userVertexIndices.get(choice - 1);

        AbstractGraph<Node>.Tree bfsTree = graph.bfs(userVertexIndex);
        VisualizationManager visualizationManager = new VisualizationManager(graph, app);
        visualizationManager.showMovieGraph(bfsTree, userVertexIndex);

        BFSGraphView graphView = new BFSGraphView(graph, bfsTree, userVertexIndex);
        List<Movie> recommendedMovies = graphView.getRecommendedMovies();

        System.out.println("\nRecommended Movies for " + graph.getVertex(userVertexIndex).getName() + ":");
        if (recommendedMovies.isEmpty()) {
            System.out.println("No recommendations available.");
        } else {
            for (Movie movie : recommendedMovies) {
                System.out.println(" - " + movie.getName() + " (" + movie.getGenre() + ")");
            }
        }
    }
}
