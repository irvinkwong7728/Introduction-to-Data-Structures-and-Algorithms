public class Movie extends Node {
    private final String genre;

    public Movie(String name, String genre, int x, int y) {
        super(name, x, y);
        this.genre = genre;
    }

    public String getGenre() { return genre; }
}
