import java.util.HashMap;
import java.util.Map;

public class GraphNode {
    String word;
    Map<GraphNode, Integer> neighbors; // 邻居节点及边权重

    public GraphNode(String word) {
        this.word = word.toLowerCase(); // 统一小写
        neighbors = new HashMap<>();
    }

    public void addNeighbor(GraphNode neighbor) {
        neighbors.put(neighbor, neighbors.getOrDefault(neighbor, 0) + 1);
    }
}
