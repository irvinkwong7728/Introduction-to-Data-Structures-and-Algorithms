import java.util.Scanner;

public class ConsoleMenu {
    private final Graph<Node> graph;
    private final Main app;
    private final Scanner sc = new Scanner(System.in);

    public ConsoleMenu(Graph<Node> graph, Main app) {
        this.graph = graph;
        this.app = app;
    }

    public void mainMenu() {
        while (true) {
            System.out.println();
            System.out.println("------------------------------------------------------");
            System.out.println("Welcome to Movie Recommendation System");
            System.out.println("------------------------------------------------------");
            System.out.println("Main Menu");
            System.out.println("---------");
            System.out.println("1. Create Graph (Add Vertices/Edges)");
            System.out.println("2. Search for a movie");
            System.out.println("3. Display graph");
            System.out.println("4. BFS Traversal Recommendations");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int opt = sc.nextInt();
            sc.nextLine();

            switch (opt) {
                case 1 -> new GraphManager(graph, sc).createGraph();
                case 2 -> new GraphManager(graph, sc).handleSearchMovie();
                case 3 -> new VisualizationManager(graph, app).showGraphVisualization();
                case 4 -> new RecommendationHandler(graph, app, sc).handleRecommendations();
                case 5 -> {
                    System.out.println("Thank you for using our system!");
                    javafx.application.Platform.exit();
                    System.exit(0);
                }
                default -> System.out.println("Invalid option");
            }
        }
    }
}
