import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static Graph<Node> graph = new UnweightedGraph<>();
    private static Main instance;
    private Stage primaryStage;

    public Main() {
        instance = this;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Movie Network BFS Visualization");
        primaryStage.hide();

        Thread consoleThread = new Thread(() -> {
            new ConsoleMenu(graph, this).mainMenu();
        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    public static void main(String[] args) {
        DataLoader.loadDefaultData(graph);
        launch(args); // no console thread here
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Main getInstance() {
        return instance;
    }
}
