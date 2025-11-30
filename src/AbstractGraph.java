import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractGraph<V> implements Graph<V> {
    protected List<V> vertices = new ArrayList<>();
    protected List<List<Edge>> neighbors = new ArrayList<>();

    protected AbstractGraph() {
    }

    protected AbstractGraph(V[] vertices, int[][] edges) {
        for(int i = 0; i < vertices.length; ++i) {
            this.addVertex(vertices[i]);
        }
        this.createAdjacencyLists(edges, vertices.length);
    }

    protected AbstractGraph(List<V> vertices, List<Edge> edges) {
        for(int i = 0; i < vertices.size(); ++i) {
            this.addVertex(vertices.get(i));
        }
        this.createAdjacencyLists(edges, vertices.size());
    }

    @SuppressWarnings("unchecked")
    protected AbstractGraph(List<Edge> edges, int numberOfVertices) {
        for(int i = 0; i < numberOfVertices; ++i) {
            this.addVertex((V) Integer.valueOf(i));
        }
        this.createAdjacencyLists(edges, numberOfVertices);
    }

    @SuppressWarnings("unchecked")
    protected AbstractGraph(int[][] edges, int numberOfVertices) {
        for(int i = 0; i < numberOfVertices; ++i) {
            this.addVertex((V) Integer.valueOf(i));
        }
        this.createAdjacencyLists(edges, numberOfVertices);
    }

    private void createAdjacencyLists(int[][] edges, int numberOfVertices) {
        for(int i = 0; i < edges.length; ++i) {
            this.addEdge(edges[i][0], edges[i][1]);
        }
    }

    private void createAdjacencyLists(List<Edge> edges, int numberOfVertices) {
        for(Edge edge : edges) {
            this.addEdge(edge.u, edge.v);
        }
    }

    public int getSize() {
        return this.vertices.size();
    }

    public List<V> getVertices() {
        return this.vertices;
    }

    public V getVertex(int index) {
        return this.vertices.get(index);
    }

    public int getIndex(V v) {
        return this.vertices.indexOf(v);
    }

    public List<Integer> getNeighbors(int index) {
        List<Integer> result = new ArrayList<>();
        List<Edge> edgeList = this.neighbors.get(index);
        for(Edge e : edgeList) {
            result.add(e.v);
        }
        return result;
    }

    public int getDegree(int v) {
        return this.neighbors.get(v).size();
    }

    public void printEdges() {
        for(int u = 0; u < this.neighbors.size(); ++u) {
            System.out.print(this.getVertex(u) + " (" + u + "): ");

            List<Edge> edgeList = this.neighbors.get(u);
            for(Edge e : edgeList) {
                System.out.print("(" + this.getVertex(e.u) + ", " + this.getVertex(e.v) + ") ");
            }
            System.out.println();
        }
    }

    public void clear() {
        this.vertices.clear();
        this.neighbors.clear();
    }

    public void addVertex(V vertex) {
        this.vertices.add(vertex);
        this.neighbors.add(new ArrayList<>());
    }

    protected boolean addEdge(Edge e) {
        if (e.u >= 0 && e.u <= this.getSize() - 1) {
            if (e.v >= 0 && e.v <= this.getSize() - 1) {
                List<Edge> edgeList = this.neighbors.get(e.u);
                if (!edgeList.contains(e)) {
                    edgeList.add(e);
                    return true;
                } else {
                    return false;
                }
            } else {
                throw new IllegalArgumentException("No such index: " + e.v);
            }
        } else {
            throw new IllegalArgumentException("No such index: " + e.u);
        }
    }

    public void addEdge(int u, int v) {
        this.addEdge(new Edge(u, v));
        if (u != v) {
            this.addEdge(new Edge(v, u));
        }
    }

    public boolean removeEdge(int u, int v) {
        if (u >= 0 && u <= this.getSize() - 1) {
            if (v >= 0 && v <= this.getSize() - 1) {
                boolean removed = this.neighbors.get(u).remove(new Edge(u, v));
                if (u != v) {
                    removed = this.neighbors.get(v).remove(new Edge(v, u)) || removed;
                }
                return removed;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean removeVertex(V vertex) {
        int idx = this.getIndex(vertex);
        if (idx < 0) {
            return false;
        } else {
            this.vertices.remove(idx);
            this.neighbors.remove(idx);

            for(List<Edge> adj : this.neighbors) {
                adj.removeIf((ex) -> ex.v == idx);

                for(Edge e : adj) {
                    if (e.u > idx) {
                        --e.u;
                    }
                    if (e.v > idx) {
                        --e.v;
                    }
                }
            }
            return true;
        }
    }

    public AbstractGraph<V>.Tree dfs(int v) {
        List<Integer> searchOrder = new ArrayList<>();
        int[] parent = new int[this.vertices.size()];

        for(int i = 0; i < parent.length; ++i) {
            parent[i] = -1;
        }

        boolean[] isVisited = new boolean[this.vertices.size()];
        this.dfs(v, parent, searchOrder, isVisited);
        return new Tree(v, parent, searchOrder);
    }

    private void dfs(int u, int[] parent, List<Integer> searchOrder, boolean[] isVisited) {
        searchOrder.add(u);
        isVisited[u] = true;

        List<Edge> edgeList = this.neighbors.get(u);
        for(Edge e : edgeList) {
            if (!isVisited[e.v]) {
                parent[e.v] = u;
                this.dfs(e.v, parent, searchOrder, isVisited);
            }
        }
    }

    public AbstractGraph<V>.Tree bfs(int v) {
        List<Integer> searchOrder = new ArrayList<>();
        int[] parent = new int[this.vertices.size()];
        int[] levels = new int[this.vertices.size()];

        for(int i = 0; i < parent.length; ++i) {
            parent[i] = -1;
            levels[i] = -1;
        }

        LinkedList<Integer> queue = new LinkedList<>();
        boolean[] isVisited = new boolean[this.vertices.size()];
        queue.offer(v);
        isVisited[v] = true;
        levels[v] = 0;

        while(!queue.isEmpty()) {
            int u = queue.poll();
            searchOrder.add(u);

            List<Edge> edgeList = this.neighbors.get(u);
            for(Edge e : edgeList) {
                if (!isVisited[e.v]) {
                    queue.offer(e.v);
                    parent[e.v] = u;
                    levels[e.v] = levels[u] + 1;
                    isVisited[e.v] = true;
                }
            }
        }

        return new Tree(v, parent, searchOrder, levels);
    }

    public static class Edge {
        public int u;
        public int v;

        public Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return this.u == edge.u && this.v == edge.v;
        }

        @Override
        public int hashCode() {
            return u * 31 + v;
        }
    }

    public class Tree {
        private int root;
        private int[] parent;
        private List<Integer> searchOrder;
        private int[] levels;

        public Tree(int root, int[] parent, List<Integer> searchOrder) {
            this.root = root;
            this.parent = parent;
            this.searchOrder = searchOrder;
            this.levels = new int[parent.length];
            calculateLevels();
        }

        public Tree(int root, int[] parent, List<Integer> searchOrder, int[] levels) {
            this.root = root;
            this.parent = parent;
            this.searchOrder = searchOrder;
            this.levels = levels;
        }

        private void calculateLevels() {
            for (int i = 0; i < levels.length; i++) {
                levels[i] = getLevel(i);
            }
        }

        private int getLevel(int node) {
            if (node == root) return 0;
            if (parent[node] == -1) return -1;
            return getLevel(parent[node]) + 1;
        }

        public int getRoot() {
            return this.root;
        }

        public int getParent(int v) {
            return this.parent[v];
        }

        public List<Integer> getSearchOrder() {
            return this.searchOrder;
        }


        public int getMaxLevel() {
            int max = 0;
            for (int level : levels) {
                if (level > max) max = level;
            }
            return max;
        }

        public List<Integer> getNodesAtLevel(int level) {
            List<Integer> nodesAtLevel = new ArrayList<>();
            for (int i = 0; i < levels.length; i++) {
                if (levels[i] == level) {
                    nodesAtLevel.add(i);
                }
            }
            return nodesAtLevel;
        }

        public int getNumberOfVerticesFound() {
            return this.searchOrder.size();
        }

        public List<V> getPath(int index) {
            ArrayList<V> path = new ArrayList<>();

            do {
                path.add(AbstractGraph.this.vertices.get(index));
                index = this.parent[index];
            } while(index != -1);

            return path;
        }

        public void printPath(int index) {
            List<V> path = this.getPath(index);
            System.out.print("A path from " + AbstractGraph.this.vertices.get(this.root) +
                    " to " + AbstractGraph.this.vertices.get(index) + ": ");

            for(int i = path.size() - 1; i >= 0; --i) {
                System.out.print(path.get(i) + " ");
            }
            System.out.println();
        }

        public void printTree() {
            System.out.println("Root is: " + AbstractGraph.this.vertices.get(this.root));
            System.out.print("Edges: ");

            for(int i = 0; i < this.parent.length; ++i) {
                if (this.parent[i] != -1) {
                    System.out.print("(" + AbstractGraph.this.vertices.get(this.parent[i]) +
                            ", " + AbstractGraph.this.vertices.get(i) + ") ");
                }
            }
            System.out.println();
        }
    }
}
