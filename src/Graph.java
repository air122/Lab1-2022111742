import java.io.*;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;


public class Graph {
    Map<String, GraphNode> nodes;

    public Graph() {
        nodes = new HashMap<>();
    }

    // 功能1：读文件并生成有向图
    public void readFromFile(String filePath) {
        nodes.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<String> words = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^a-zA-Z\\s]", " ").toLowerCase(); // 非字母变空格
                String[] parts = line.trim().split("\\s+");
                words.addAll(Arrays.asList(parts));
            }
            for (int i = 0; i < words.size() - 1; i++) {
                if (words.get(i).isEmpty() || words.get(i + 1).isEmpty()) continue;
                GraphNode from = nodes.computeIfAbsent(words.get(i), GraphNode::new);
                GraphNode to = nodes.computeIfAbsent(words.get(i + 1), GraphNode::new);
                from.addNeighbor(to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 功能2：展示有向图
    public void showDirectedGraph() {
        System.out.println("Directed Graph:");
        for (GraphNode node : nodes.values()) {
            for (Map.Entry<GraphNode, Integer> entry : node.neighbors.entrySet()) {
                System.out.println(node.word + " -> " + entry.getKey().word + " [Weight: " + entry.getValue() + "]");
            }
        }
    }

    // 功能3：查询桥接词
    public String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!nodes.containsKey(word1) && !nodes.containsKey(word2)) {
            return "No " + word1 + " and " + word2 + " in the graph!";
        }
        if (!nodes.containsKey(word1) ) {
            return "No " + word1 + " in the graph!";
        }
        if (!nodes.containsKey(word2)) {
            return "No " + word2 + " in the graph!";
        }
        Set<String> bridgeWords = new HashSet<>();
        GraphNode node1 = nodes.get(word1);
        for (GraphNode neighbor : node1.neighbors.keySet()) {
            if (neighbor.neighbors.containsKey(nodes.get(word2))) {
                bridgeWords.add(neighbor.word);
            }
        }
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        }
        return "The bridge words from " + word1 + " to " + word2 + " is: " + String.join(", ", bridgeWords) + ".";
    }

    // 功能4：根据bridge word生成新文本
    public String generateNewText(String inputText) {
        inputText = inputText.replaceAll("[^a-zA-Z\\s]", " ").toLowerCase();
        String[] parts = inputText.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < parts.length - 1; i++) {
            sb.append(parts[i]).append(" ");
            String word1 = parts[i];
            String word2 = parts[i + 1];
            if (nodes.containsKey(word1)) {
                GraphNode node1 = nodes.get(word1);
                List<String> bridges = new ArrayList<>();
                for (GraphNode neighbor : node1.neighbors.keySet()) {
                    if (neighbor.neighbors.containsKey(nodes.get(word2))) {
                        bridges.add(neighbor.word);
                    }
                }
                if (!bridges.isEmpty()) {
                    String bridge = bridges.get(random.nextInt(bridges.size()));
                    sb.append(bridge).append(" ");
                }
            }
        }
        sb.append(parts[parts.length - 1]);
        return sb.toString();
    }

    // 功能5：计算最短路径
    public String calcShortestPath(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (word2 == null || word2.isEmpty()) {
            StringBuilder result = new StringBuilder();
            if (!nodes.containsKey(word1)) {
                return "No such word in the graph: " + word1;
            }

            // 遍历所有其他单词，计算并展示最短路径
            for (String otherWord : nodes.keySet()) {
                if (!otherWord.equals(word1)) {
                    String shortestPath = GraphUtils.dijkstra(this, word1, otherWord);
                    result.append("Shortest path from ").append(word1).append(" to ").append(otherWord).append(": ").append(shortestPath).append("\n");
                }
            }
            return result.toString();
        }
        if (!nodes.containsKey(word1) || !nodes.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }
        return GraphUtils.dijkstra(this, word1, word2);
    }

    // 功能6：计算PageRank
    public Double calPageRank(String word) {
        word = word.toLowerCase();
        if (!nodes.containsKey(word)) {
            return null;
        }
        // 计算TF和IDF
        Map<String, Double> tfMap = GraphUtils.calculateTF(this);
        Map<String, Double> idfMap = GraphUtils.calculateIDF(this);

        // 计算每个单词的初始PR值
        Map<String, Double> initialPR = new HashMap<>();
        for (String node : nodes.keySet()) {
            double tf = tfMap.getOrDefault(node, 0.0);
            double idf = idfMap.getOrDefault(node, 0.0);
            initialPR.put(node, tf * idf);  // 使用TF-IDF作为初始PR值
        }
        Map<String, Double> pageRanks = GraphUtils.pageRank(this, 0.85, 100);
        return pageRanks.getOrDefault(word, 0.0);
    }

    // 功能7：随机游走
    public String randomWalk() {
        return GraphUtils.randomWalk(this);
    }

    // 功能8：将有向图保存为图形文件
    public void saveGraphAsImage(String outputImagePath) {
        String dotFilePath = "graph.dot";  // 生成一个dot文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dotFilePath))) {
            writer.write("digraph G {\n");
            // 遍历节点及其邻居，写入到dot文件
            for (GraphNode node : nodes.values()) {
                for (Map.Entry<GraphNode, Integer> entry : node.neighbors.entrySet()) {
                    writer.write("    \"" + node.word + "\" -> \"" + entry.getKey().word + "\" [label=\"" + entry.getValue() + "\"];\n");
                }
            }
            writer.write("}\n");
        } catch (IOException e) {
            System.err.println("Error writing .dot file: " + e.getMessage());
            return;
        }

        // 调用Graphviz将dot转成jpg
        try {
            Process process = Runtime.getRuntime().exec("dot -Tjpg " + dotFilePath + " -o " + outputImagePath);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Graph image generated successfully: " + outputImagePath);
            } else {
                System.err.println("Graphviz dot command failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing Graphviz: " + e.getMessage());
        }
    }

    // 新增功能：计算一个单词到所有其他单词的最短路径
    public void calcAndDisplayShortestPathsFromWord(String word) {
        word = word.toLowerCase();
        if (!nodes.containsKey(word)) {
            System.out.println("No such word in the graph: " + word);
            return;
        }

        // 遍历所有其他节点，计算并展示最短路径
        for (String otherWord : nodes.keySet()) {
            if (!otherWord.equals(word)) {
                String shortestPath = calcShortestPath(word, otherWord);
                System.out.println("Shortest path from " + word + " to " + otherWord + ": " + shortestPath);
            }
        }
    }

}
