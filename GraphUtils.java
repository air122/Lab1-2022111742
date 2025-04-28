import java.io.FileWriter;
import java.io.*;
import java.util.Scanner;
import java.util.*;

public class GraphUtils {
    // 计算每个单词的TF值（Term Frequency）
    public static Map<String, Double> calculateTF(Graph graph) {
        Map<String, Double> tfMap = new HashMap<>();
        int totalWords = 0;

        // 计算每个单词的频率
        for (GraphNode node : graph.nodes.values()) {
            for (GraphNode neighbor : node.neighbors.keySet()) {
                totalWords++;
                tfMap.put(node.word, tfMap.getOrDefault(node.word, 0.0) + 1);
            }
        }

        // 归一化每个单词的TF值
        for (Map.Entry<String, Double> entry : tfMap.entrySet()) {
            tfMap.put(entry.getKey(), entry.getValue() / totalWords);
        }

        return tfMap;
    }

    // 计算IDF（Inverse Document Frequency）
    public static Map<String, Double> calculateIDF(Graph graph) {
        Map<String, Double> idfMap = new HashMap<>();
        int totalNodes = graph.nodes.size();

        // 计算每个单词在图中出现的文档数
        for (GraphNode node : graph.nodes.values()) {
            Set<String> uniqueWords = new HashSet<>();
            for (GraphNode neighbor : node.neighbors.keySet()) {
                uniqueWords.add(neighbor.word);
            }
            for (String word : uniqueWords) {
                idfMap.put(word, idfMap.getOrDefault(word, 0.0) + 1);
            }
        }

        // 计算IDF值
        for (Map.Entry<String, Double> entry : idfMap.entrySet()) {
            idfMap.put(entry.getKey(), Math.log((double) totalNodes / (entry.getValue() + 1)) + 1);
        }

        return idfMap;
    }

    // Dijkstra算法，返回路径字符串
    public static String dijkstra(Graph graph, String start, String end) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String node : graph.nodes.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(end)) break;
            visited.add(current);

            GraphNode currentNode = graph.nodes.get(current);
            for (GraphNode neighbor : currentNode.neighbors.keySet()) {
                if (visited.contains(neighbor.word)) continue;
                int weight = currentNode.neighbors.get(neighbor);
                int newDist = distances.get(current) + weight;
                if (newDist < distances.get(neighbor.word)) {
                    distances.put(neighbor.word, newDist);
                    previous.put(neighbor.word, current);
                    queue.add(neighbor.word);
                }
            }
        }

        if (!previous.containsKey(end)) {
            return "No path from " + start + " to " + end + ".";
        }

        LinkedList<String> path = new LinkedList<>();
        String current = end;
        while (current != null) {
            path.addFirst(current);
            current = previous.get(current);
        }

        return "Shortest path: " + String.join(" -> ", path) + "\nPath length: " + distances.get(end);
    }

    // PageRank算法

    public static Map<String, Double> pageRank(Graph graph, double d, int iterations) {
        Map<String, Double> pr = new HashMap<>();
        int N = graph.nodes.size();

        // 初始时每个节点的PR值平均
        for (String node : graph.nodes.keySet()) {
            pr.put(node, 1.0 / N);
        }

        for (int i = 0; i < iterations; i++) {
            Map<String, Double> newPr = new HashMap<>();

            // 计算所有出度为0的节点的PR之和
            double danglingSum = 0.0;
            for (GraphNode node : graph.nodes.values()) {
                if (node.neighbors.isEmpty()) {
                    danglingSum += pr.get(node.word);
                }
            }

            for (String node : graph.nodes.keySet()) {
                double sum = 0.0;
                for (GraphNode other : graph.nodes.values()) {
                    if (other.neighbors.containsKey(graph.nodes.get(node))) {
                        sum += pr.get(other.word) / other.neighbors.size();
                    }
                }
                // 公式：普通贡献 + 死节点贡献 + 随机跳转
                double prValue = (1 - d) / N
                        + d * (sum + danglingSum / N);
                newPr.put(node, prValue);
            }

            pr = newPr;
        }

        return pr;
    }
//    public static Map<String, Double> pageRank(Graph graph, double d, int iterations, Map<String, Double> initialPR) {
//        Map<String, Double> pr = new HashMap<>();
//
//        // 用 initialPR 初始化
//        if (initialPR != null && !initialPR.isEmpty()) {
//            double sum = initialPR.values().stream().mapToDouble(Double::doubleValue).sum();
//            for (String node : graph.nodes.keySet()) {
//                pr.put(node, initialPR.getOrDefault(node, 0.0) / sum); // 归一化初始PR
//            }
//        } else {
//            int N = graph.nodes.size();
//            for (String node : graph.nodes.keySet()) {
//                pr.put(node, 1.0 / N);
//            }
//        }
//
//        int N = graph.nodes.size();
//
//        for (int i = 0; i < iterations; i++) {
//            Map<String, Double> newPr = new HashMap<>();
//
//            // 处理出度为0的节点
//            double danglingSum = 0.0;
//            for (GraphNode node : graph.nodes.values()) {
//                if (node.neighbors.isEmpty()) {
//                    danglingSum += pr.get(node.word);
//                }
//            }
//
//            for (String node : graph.nodes.keySet()) {
//                double sum = 0.0;
//                for (GraphNode other : graph.nodes.values()) {
//                    if (other.neighbors.containsKey(graph.nodes.get(node))) {
//                        sum += pr.get(other.word) / other.neighbors.size();
//                    }
//                }
//                double prValue = (1 - d) / N + d * (sum + danglingSum / N);
//                newPr.put(node, prValue);
//            }
//
//            pr = newPr;
//        }
//
//        return pr;
//    }



    // 随机游走
    public static String randomWalk(Graph graph) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        List<GraphNode> nodeList = new ArrayList<>(graph.nodes.values());
        if (nodeList.isEmpty()) return "Graph is empty.";

        GraphNode current = nodeList.get(random.nextInt(nodeList.size()));
        Set<String> visitedEdges = new HashSet<>();
        sb.append(current.word);
        //Scanner scanner = new Scanner(System.in);  // 用于读取用户输入

        while (true) {
            List<GraphNode> neighbors = new ArrayList<>(current.neighbors.keySet());
            if (neighbors.isEmpty()) break;

            GraphNode next = neighbors.get(random.nextInt(neighbors.size()));
            String edge = current.word + "->" + next.word;
            if (visitedEdges.contains(edge)) {
                break;
            }
            visitedEdges.add(edge);
            sb.append(" -> ").append(next.word);
            current = next;
            // 提示用户是否继续
//            System.out.print("Current walk: " + sb.toString() + "\nDo you want to continue? (y/n): ");
//            String userInput = scanner.nextLine().toLowerCase();
//            if (userInput.equals("n")) {
//                break;  // 用户选择停止
//            }

        }

        // 可选：写入文件
        try (PrintWriter writer = new PrintWriter(new FileWriter("random_walk.txt"))) {
            writer.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
