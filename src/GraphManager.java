import java.util.*;

public class GraphManager {
    private final Graph<Node> graph;
    private final Scanner sc;

    public GraphManager(Graph<Node> graph, Scanner sc) {
        this.graph = graph;
        this.sc = sc;
    }

    // -----------------------------
    // Main createGraph menu
    // -----------------------------
    public void createGraph() {
        boolean continueCreating = true;
        while (continueCreating) {
            System.out.println();
            System.out.println("Create Graph Menu");
            System.out.println("--------------------------------------------------------");
            System.out.println("1. Add a vertex (Movie/User)");
            System.out.println("2. Remove a vertex");
            System.out.println("3. Add an edge");
            System.out.println("4. Remove an edge");
            System.out.println("5. Return to the main menu");
            System.out.print("Enter your choice: ");

            int optGraph = sc.nextInt();
            sc.nextLine();

            switch (optGraph) {
                case 1:
                    handleAddVertex();
                    break;
                case 2:
                    handleRemoveVertex();
                    break;
                case 3:
                    handleAddEdge();
                    break;
                case 4:
                    handleRemoveEdge();
                    break;
                case 5:
                    continueCreating = false;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    // -----------------------------
    // Vertex operations
    // -----------------------------
    public void handleAddVertex() {
        String optCont;
        do {
            System.out.println();
            System.out.println("Add Vertex");
            System.out.println("-------------------------");
            System.out.println("1. Add Movie");
            System.out.println("2. Add User");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                addMovie();
            } else if (choice == 2) {
                addUser();
            } else {
                System.out.println("Invalid choice.");
            }

            System.out.print("Continue to add new vertex? (Y/N): ");
            optCont = sc.nextLine();
        } while (optCont.equalsIgnoreCase("Y"));
    }

    private void addMovie() {
        System.out.print("Enter the name of the movie: ");
        String movieName = sc.nextLine();

        int existingMovieIndex = findMovieIndex(movieName);
        if (existingMovieIndex >= 0) {
            System.out.println("Movie '" + movieName + "' already exists! Cannot add duplicate movie.");
            return;
        }

        System.out.println("Select movie genre:");
        System.out.println("1. Horror");
        System.out.println("2. Comedy");
        System.out.println("3. Action");
        System.out.println("4. Romance");
        System.out.println("5. Drama");
        System.out.println("6. Sci-Fi");
        System.out.print("Enter your choice (1-6): ");

        int genreChoice = sc.nextInt();
        sc.nextLine();

        String[] genres = {"Horror", "Comedy", "Action", "Romance", "Drama", "Sci-Fi"};
        String selectedGenre = (genreChoice >= 1 && genreChoice <= 6) ? genres[genreChoice - 1] : "Unknown";

        int n = graph.getSize();
        int radius = 200;
        int centerX = 375;
        int centerY = 225;
        double angle = (Math.PI * 2.0) * (double)n / Math.max(1, (double)(n + 1));
        int x = (int)((double)centerX + (double)radius * Math.cos(angle));
        int y = (int)((double)centerY + (double)radius * Math.sin(angle));

        Movie newMovie = new Movie(movieName, selectedGenre, x, y);
        graph.addVertex(newMovie);
        int movieIndex = graph.getSize() - 1;

        int genreIndex = findNodeIndex(selectedGenre);
        if (genreIndex < 0) {
            int genreN = graph.getSize();
            int genreRadius = 250;
            double genreAngle = (Math.PI * 2.0) * (double)genreN / Math.max(1, (double)(genreN + 1));
            int genreX = (int)((double)centerX + (double)genreRadius * Math.cos(genreAngle));
            int genreY = (int)((double)centerY + (double)genreRadius * Math.sin(genreAngle));

            Genre newGenre = new Genre(selectedGenre, genreX, genreY);
            graph.addVertex(newGenre);
            genreIndex = graph.getSize() - 1;
            System.out.println("Created new genre: " + selectedGenre);
        }

        graph.addEdge(movieIndex, genreIndex);
        System.out.println("Movie '" + movieName + "' [" + selectedGenre + "] added and connected to genre successfully.");
    }

    private void addUser() {
        System.out.print("Enter the name of the user: ");
        String userName = sc.nextLine();

        int existingUserIndex = findUserIndex(userName);
        if (existingUserIndex >= 0) {
            System.out.println("User '" + userName + "' already exists! Cannot add duplicate user.");
            return;
        }

        int n = graph.getSize();
        int radius = 150;
        int centerX = 375;
        int centerY = 225;
        double angle = (Math.PI * 2.0) * (double)n / Math.max(1, (double)(n + 1));
        int x = (int)((double)centerX + (double)radius * Math.cos(angle));
        int y = (int)((double)centerY + (double)radius * Math.sin(angle));

        User newUser = new User(userName, x, y);
        graph.addVertex(newUser);
        System.out.println("User '" + userName + "' added successfully.");
    }

    public void handleRemoveVertex() {
        System.out.println();
        System.out.println("Remove Vertex");
        System.out.println("---------------------------");
        System.out.println("1. Remove Movie");
        System.out.println("2. Remove User");
        System.out.print("Enter your choice: ");

        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            removeMovie();
        } else if (choice == 2) {
            removeUser();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void removeMovie() {
        System.out.print("Enter the movie name to remove: ");
        String movieName = sc.nextLine();

        int idx = findMovieIndex(movieName);

        if (idx >= 0) {
            Node node = graph.getVertex(idx);
            boolean removed = graph.removeVertex(node);
            if (removed) {
                System.out.println("Movie '" + movieName + "' removed successfully.");
            } else {
                System.out.println("Failed to remove movie '" + movieName + "'.");
            }
        } else {
            System.out.println("Movie '" + movieName + "' not found.");
        }
    }

    private void removeUser() {
        System.out.print("Enter the user name to remove: ");
        String userName = sc.nextLine();

        int idx = findUserIndex(userName);

        if (idx >= 0) {
            Node node = graph.getVertex(idx);
            boolean removed = graph.removeVertex(node);
            if (removed) {
                System.out.println("User '" + userName + "' removed successfully.");
            } else {
                System.out.println("Failed to remove user '" + userName + "'.");
            }
        } else {
            System.out.println("User '" + userName + "' not found.");
        }
    }

    // -----------------------------
    // Edge operations
    // -----------------------------
    public void handleAddEdge() {
        String optCont;
        do {
            System.out.println();
            System.out.println("Add Edge");
            System.out.println("-------------------------");
            System.out.println("1. Connect User to Movie (User watched Movie)");
            System.out.println("2. Connect User to User (Users know each other)");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                connectUserToMovie();
            } else if (choice == 2) {
                connectUserToUser();
            } else {
                System.out.println("Invalid choice.");
            }

            System.out.print("Continue to add new edge? (Y/N): ");
            optCont = sc.nextLine();
        } while (optCont.equalsIgnoreCase("Y"));
    }

    private void connectUserToMovie() {
        System.out.print("Enter user name: ");
        String userName = sc.nextLine();
        System.out.print("Enter movie name: ");
        String movieName = sc.nextLine();

        int userIdx = findUserIndex(userName);
        int movieIdx = findMovieIndex(movieName);

        if (userIdx >= 0 && movieIdx >= 0) {
            graph.addEdge(userIdx, movieIdx);
            System.out.println("Connected: " + userName + " watched " + movieName);
        } else {
            if (userIdx < 0) {
                System.out.println("User '" + userName + "' not found. Please add the user first.");
            }
            if (movieIdx < 0) {
                System.out.println("Movie '" + movieName + "' not found. Please add the movie first.");
            }
        }
    }

    private void connectUserToUser() {
        System.out.print("Enter first user name: ");
        String user1Name = sc.nextLine();
        System.out.print("Enter second user name: ");
        String user2Name = sc.nextLine();

        int user1Idx = findUserIndex(user1Name);
        int user2Idx = findUserIndex(user2Name);

        if (user1Idx >= 0 && user2Idx >= 0) {
            graph.addEdge(user1Idx, user2Idx);
            System.out.println("Connected: " + user1Name + " knows " + user2Name);
        } else {
            if (user1Idx < 0) {
                System.out.println("User '" + user1Name + "' not found. Please add the user first.");
            }
            if (user2Idx < 0) {
                System.out.println("User '" + user2Name + "' not found. Please add the user first.");
            }
        }
    }

    public void handleRemoveEdge() {
        System.out.println();
        System.out.println("Remove Edge");
        System.out.println("---------------------------");
        System.out.println("1. Remove User-Movie edge");
        System.out.println("2. Remove User-User edge");
        System.out.println("3. Remove any edge (by names)");
        System.out.print("Enter your choice: ");

        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                removeUserMovieEdge();
                break;
            case 2:
                removeUserUserEdge();
                break;
            case 3:
                removeEdgeByNames();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void removeUserMovieEdge() {
        System.out.print("Enter user name: ");
        String userName = sc.nextLine();
        System.out.print("Enter movie name: ");
        String movieName = sc.nextLine();

        int userIdx = findUserIndex(userName);
        int movieIdx = findMovieIndex(movieName);

        if (userIdx >= 0 && movieIdx >= 0) {
            boolean ok = graph.removeEdge(userIdx, movieIdx);
            if (ok) {
                System.out.println("Edge removed: " + userName + " (User) -- " + movieName + " (Movie)");
            } else {
                System.out.println("Edge not found between " + userName + " and " + movieName + ".");
            }
        } else {
            if (userIdx < 0) {
                System.out.println("User '" + userName + "' not found.");
            }
            if (movieIdx < 0) {
                System.out.println("Movie '" + movieName + "' not found.");
            }
        }
    }

    private void removeUserUserEdge() {
        System.out.print("Enter first user name: ");
        String user1Name = sc.nextLine();
        System.out.print("Enter second user name: ");
        String user2Name = sc.nextLine();

        int user1Idx = findUserIndex(user1Name);
        int user2Idx = findUserIndex(user2Name);

        if (user1Idx >= 0 && user2Idx >= 0) {
            boolean ok = graph.removeEdge(user1Idx, user2Idx);
            if (ok) {
                System.out.println("Edge removed: " + user1Name + " (User) -- " + user2Name + " (User)");
            } else {
                System.out.println("Edge not found between " + user1Name + " and " + user2Name + ".");
            }
        } else {
            if (user1Idx < 0) {
                System.out.println("User '" + user1Name + "' not found.");
            }
            if (user2Idx < 0) {
                System.out.println("User '" + user2Name + "' not found.");
            }
        }
    }

    private void removeEdgeByNames() {
        System.out.print("Enter the first name: ");
        String name1 = sc.nextLine();
        System.out.print("Enter the second name: ");
        String name2 = sc.nextLine();

        int u = findNodeIndex(name1);
        int v = findNodeIndex(name2);

        if (u >= 0 && v >= 0) {
            boolean ok = graph.removeEdge(u, v);
            if (ok) {
                Node node1 = graph.getVertex(u);
                Node node2 = graph.getVertex(v);
                String type1 = getNodeType(node1);
                String type2 = getNodeType(node2);
                System.out.println("Edge removed: " + name1 + " (" + type1 + ") -- " + name2 + " (" + type2 + ")");
            } else {
                System.out.println("Edge not found.");
            }
        } else {
            System.out.println("One or both nodes not found.");
        }
    }

    private String getNodeType(Node node) {
        if (node instanceof User) return "User";
        if (node instanceof Movie) return "Movie";
        if (node instanceof Genre) return "Genre";
        return "Unknown";
    }

    // -----------------------------
    // Search operations
    // -----------------------------
    public void handleSearchMovie() {
        System.out.println();
        System.out.println("Search for a movie");
        System.out.println("------------------------------");
        System.out.print("Enter the movie name: ");
        String movie = sc.nextLine();

        int index = findMovieIndex(movie);

        if (index < 0) {
            System.out.println("Movie not found.");
        } else {
            Movie foundMovie = (Movie) graph.getVertex(index);
            System.out.println("Movie found: " + foundMovie.getName() );
            System.out.print("Connected to: ");

            List<Integer> neighbors = graph.getNeighbors(index);
            if (neighbors.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < neighbors.size(); i++) {
                    int idx = neighbors.get(i);
                    Node node = graph.getVertex(idx);
                    System.out.print(node.getName());
                    if (node instanceof User) {
                        System.out.print(" (User)");
                    } else if (node instanceof Genre) {
                        System.out.print(" (Genre)");
                    }
                    if (i < neighbors.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        }
    }

    // -----------------------------
    // Utility
    // -----------------------------

    private int findNodeIndex(String name) {
        if (name == null) {
            return -1;
        }

        String targetName = name.trim();
        for (int i = 0; i < graph.getSize(); i++) {
            Node node = graph.getVertex(i);
            if (node.getName() != null && node.getName().trim().equalsIgnoreCase(targetName)) {
                return i;
            }
        }
        return -1;
    }

    private int findMovieIndex(String movieName) {
        if (movieName == null) {
            return -1;
        }

        String targetName = movieName.trim();
        for (int i = 0; i < graph.getSize(); i++) {
            Node node = graph.getVertex(i);
            if (node instanceof Movie && node.getName() != null &&
                    node.getName().trim().equalsIgnoreCase(targetName)) {
                return i;
            }
        }
        return -1;
    }

    private int findUserIndex(String userName) {
        if (userName == null) {
            return -1;
        }

        String targetName = userName.trim();
        for (int i = 0; i < graph.getSize(); i++) {
            Node node = graph.getVertex(i);
            if (node instanceof User && node.getName() != null &&
                    node.getName().trim().equalsIgnoreCase(targetName)) {
                return i;
            }
        }
        return -1;
    }
}
