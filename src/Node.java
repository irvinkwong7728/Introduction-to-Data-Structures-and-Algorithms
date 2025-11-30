public abstract class Node implements Displayable {
    protected int x, y;
    protected String name;

    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
}
