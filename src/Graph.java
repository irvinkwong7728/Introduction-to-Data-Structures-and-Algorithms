import java.util.List;

public interface Graph<V> {
    int getSize();

    List<V> getVertices();

    V getVertex(int var1);

    int getIndex(V var1);

    List<Integer> getNeighbors(int var1);

    int getDegree(int var1);

    void printEdges();

    void clear();

    void addVertex(V var1);

    void addEdge(int var1, int var2);

    boolean removeEdge(int var1, int var2);

    boolean removeVertex(V var1);

    AbstractGraph<V>.Tree dfs(int var1);

    AbstractGraph<V>.Tree bfs(int var1);
}