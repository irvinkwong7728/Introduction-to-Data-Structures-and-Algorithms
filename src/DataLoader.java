public class DataLoader {

    public static void loadDefaultData(Graph<Node> graph) {
        loadDefaultGenres(graph);
        loadDefaultMovies(graph);
        loadDefaultUsers(graph);
        loadDefaultUserMovieEdges(graph);
        loadDefaultUserUserEdges(graph);
        loadGenreMovieConnections(graph);
    }

    private static void loadDefaultGenres(Graph<Node> graph) {
        String[] genres = {"Sci-Fi", "Action", "Crime", "Drama"};

        int totalWidth = 600;
        int startX = (750 - totalWidth) / 2;
        int spacing = totalWidth / (genres.length - 1);

        for (int i = 0; i < genres.length; i++) {
            String genreName = genres[i];
            int x = startX + i * spacing;
            int y = 60;

            Genre genre = new Genre(genreName, x, y);
            graph.addVertex(genre);
        }
    }

    private static void loadDefaultMovies(Graph<Node> graph) {
        String[][] movies = {
                {"Inception", "Sci-Fi"},
                {"The Matrix", "Sci-Fi"},
                {"The Dark Knight", "Action"},
                {"Pulp Fiction", "Crime"},
                {"Forrest Gump", "Drama"},
                {"Mission Impossible","Action"},
                {"Men in Black","Action"}
        };

        java.util.Map<String, Integer> genreCount = new java.util.HashMap<>();
        java.util.Map<String, Integer> genreIndex = new java.util.HashMap<>();

        for (String[] movieData : movies) {
            String genre = movieData[1];
            genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
            genreIndex.put(genre, 0);
        }

        for (String[] movieData : movies) {
            String movieName = movieData[0];
            String genreType = movieData[1];

            int genreColumn = getGenreColumn(genreType);
            int currentIndex = genreIndex.get(genreType);
            int moviesInGenre = genreCount.get(genreType);

            int totalWidth = 600;
            int startX = (750 - totalWidth) / 2;
            int spacing = totalWidth / 3;

            int genreX = startX + genreColumn * spacing;

            int movieSpacing = 80;
            int movieStartX = genreX - (moviesInGenre - 1) * movieSpacing / 2;
            int x = movieStartX + currentIndex * movieSpacing;
            int y = 140;

            Movie movie = new Movie(movieName, genreType, x, y);
            graph.addVertex(movie);

            genreIndex.put(genreType, currentIndex + 1);
        }
    }


    private static void loadDefaultUsers(Graph<Node> graph) {
        String[] users = {"Alice", "Bob", "Charlie", "Diana", "Eve"};

        int centerX = 375;
        int centerY = 280;
        int radius = 180;

        for (int i = 0; i < users.length; i++) {
            String userName = users[i];

            double angle = Math.PI - (Math.PI * i / (users.length - 1));
            int x = (int)(centerX + radius * Math.cos(angle));
            int y = (int)(centerY + Math.abs(radius * Math.sin(angle)) * 0.5);

            User user = new User(userName, x, y);
            graph.addVertex(user);
        }
    }

    private static void loadGenreMovieConnections(Graph<Node> graph) {
        for (int i = 0; i < graph.getSize(); i++) {
            Node node1 = graph.getVertex(i);
            if (node1 instanceof Genre) {
                Genre genre = (Genre) node1;

                for (int j = 0; j < graph.getSize(); j++) {
                    Node node2 = graph.getVertex(j);
                    if (node2 instanceof Movie) {
                        Movie movie = (Movie) node2;

                        if (genre.getName().equals(movie.getGenre())) {
                            graph.addEdge(i, j);
                        }
                    }
                }
            }
        }
    }


    private static void loadDefaultUserMovieEdges(Graph<Node> graph) {
        String[][] userMovieConnections = {
                {"Alice", "Inception"},
                {"Alice", "The Matrix"},
                {"Bob", "The Dark Knight"},
                {"Charlie", "Pulp Fiction"},
                {"Diana", "Forrest Gump"},
                {"Eve", "Pulp Fiction"}
        };

        for (String[] connection : userMovieConnections) {
            String userName = connection[0];
            String movieName = connection[1];

            int userIndex = findNodeIndex(graph, userName);
            int movieIndex = findNodeIndex(graph, movieName);

            if (userIndex >= 0 && movieIndex >= 0) {
                Node user = graph.getVertex(userIndex);
                Node movie = graph.getVertex(movieIndex);

                if (user instanceof User && movie instanceof Movie) {
                    graph.addEdge(userIndex, movieIndex);
                }
            }
        }
    }

    private static void loadDefaultUserUserEdges(Graph<Node> graph) {
        String[][] userUserConnections = {
                {"Alice", "Bob"},
                {"Alice", "Eve"},
                {"Bob", "Charlie"},
                {"Charlie", "Diana"}
        };

        for (String[] connection : userUserConnections) {
            String user1Name = connection[0];
            String user2Name = connection[1];

            int user1Index = findNodeIndex(graph, user1Name);
            int user2Index = findNodeIndex(graph, user2Name);

            if (user1Index >= 0 && user2Index >= 0) {
                Node user1 = graph.getVertex(user1Index);
                Node user2 = graph.getVertex(user2Index);

                if (user1 instanceof User && user2 instanceof User) {
                    graph.addEdge(user1Index, user2Index);
                }
            }
        }
    }

    private static int findNodeIndex(Graph<Node> graph, String name) {
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

    private static int getGenreColumn(String genre) {
        switch (genre) {
            case "Sci-Fi": return 0;
            case "Action": return 1;
            case "Crime": return 2;
            case "Drama": return 3;
            default: return 0;
        }
    }
}