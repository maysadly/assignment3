import java.util.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class WeightedGraph<V> {
    private Map<V, Vertex<V>> vertices;

    public WeightedGraph() {
        this.vertices = new HashMap<>();
    }

    public void addVertex(V data) {
        if (!vertices.containsKey(data)) {
            vertices.put(data, new Vertex<>(data));
        }
    }

    public Vertex<V> getVertex(V data) {
        return vertices.get(data);
    }

    public void addEdge(V source, V destination, double weight) {
        Vertex<V> sourceVertex = vertices.get(source);
        Vertex<V> destinationVertex = vertices.get(destination);
        if (sourceVertex != null && destinationVertex != null) {
            sourceVertex.addAdjacentVertex(destinationVertex, weight);
        }
    }

    public Map<V, Vertex<V>> getVertices() {
        return vertices;
    }
}


public class Vertex<V> {
    private V data;
    private Map<Vertex<V>, Double> adjacentVertices;

    public Vertex(V data) {
        this.data = data;
        this.adjacentVertices = new HashMap<>();
    }

    public V getData() {
        return data;
    }

    public void addAdjacentVertex(Vertex<V> destination, double weight) {
        adjacentVertices.put(destination, weight);
    }

    public Map<Vertex<V>, Double> getAdjacentVertices() {
        return adjacentVertices;
    }

    @Override
    public String toString() {
        return "Vertex{" + "data=" + data + ", adjacentVertices=" + adjacentVertices.keySet() + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>) obj;
        return Objects.equals(data, vertex.data);
    }
}


public interface Search<V> {
    List<V> getPath(V source, V destination);
}


public class DijkstraSearch<V> implements Search<V> {
    private WeightedGraph<V> graph;

    public DijkstraSearch(WeightedGraph<V> graph) {
        this.graph = graph;
    }

    @Override
    public List<V> getPath(V source, V destination) {
        Map<Vertex<V>, Double> distances = new HashMap<>();
        Map<Vertex<V>, Vertex<V>> previous = new HashMap<>();
        PriorityQueue<Vertex<V>> queue = new PriorityQueue<>(Comparator.comparing(distances::get));
        Vertex<V> sourceVertex = graph.getVertex(source);
        Vertex<V> destinationVertex = graph.getVertex(destination);

        if (sourceVertex == null || destinationVertex == null) {
            return Collections.emptyList();
        }

        distances.put(sourceVertex, 0.0);
        for (Vertex<V> vertex : graph.getVertices().values()) {
            if (!vertex.equals(sourceVertex)) {
                distances.put(vertex, Double.MAX_VALUE);
            }
            queue.add(vertex);
        }

        while (!queue.isEmpty()) {
            Vertex<V> current = queue.poll();

            if (current.equals(destinationVertex)) {
                return buildPath(previous, destinationVertex);
            }

            for (Map.Entry<Vertex<V>, Double> neighborEntry : current.getAdjacentVertices().entrySet()) {
                Vertex<V> neighbor = neighborEntry.getKey();
                double weight = neighborEntry.getValue();

                double alternativeRoute = distances.get(current) + weight;
                if (alternativeRoute < distances.get(neighbor)) {
                    distances.put(neighbor, alternativeRoute);
                    previous.put(neighbor, current);
                    queue.remove(neighbor); // Remove and re-add to update its priority
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<V> buildPath(Map<Vertex<V>, Vertex<V>> previous, Vertex<V> destination) {
        LinkedList<V> path = new LinkedList<>();
        for (Vertex<V> at = destination; at != null; at = previous.get(at)) {
            path.addFirst(at.getData());
        }
        return path;
    }
}


public class BreadthFirstSearch<V> implements Search<V> {
    private WeightedGraph<V> graph;

    public BreadthFirstSearch(WeightedGraph<V> graph) {
        this.graph = graph;
    }

    @Override
    public List<V> getPath(V source, V destination) {
        Queue<Vertex<V>> queue = new LinkedList<>();
        Map<Vertex<V>, Vertex<V>> visited = new HashMap<>();
        Vertex<V> sourceVertex = graph.getVertex(source);
        Vertex<V> destinationVertex = graph.getVertex(destination);

        if (sourceVertex == null || destinationVertex == null) {
            return Collections.emptyList();
        }

        queue.add(sourceVertex);
        visited.put(sourceVertex, null);

        while (!queue.isEmpty()) {
            Vertex<V> current = queue.poll();

            if (current.equals(destinationVertex)) {
                return buildPath(visited, destinationVertex);
            }

            for (Vertex<V> neighbor : current.getAdjacentVertices().keySet()) {
                if (!visited.containsKey(neighbor)) {
                    queue.add(neighbor);
                    visited.put(neighbor, current);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<V> buildPath(Map<Vertex<V>, Vertex<V>> visited, Vertex<V> destination) {
        LinkedList<V> path = new LinkedList<>();
        for (Vertex<V> at = destination; at != null; at = visited.get(at)) {
            path.addFirst(at.getData());
        }
        return path;
    }
}


public class Main {
    public static void main(String[] args) {
        WeightedGraph<String> graph = new WeightedGraph<>();

        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");

        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 4);
        graph.addEdge("B", "C", 2);
        graph.addEdge("B", "D", 5);
        graph.addEdge("C", "D", 1);

        BreadthFirstSearch<String> bfs = new BreadthFirstSearch<>(graph);
        DijkstraSearch<String> dijkstra = new DijkstraSearch<>(graph);

        System.out.println("BFS Path from A to D: " + bfs.getPath("A", "D"));
        System.out.println("Dijkstra Path from A to D: " + dijkstra.getPath("A", "D"));
    }
}
