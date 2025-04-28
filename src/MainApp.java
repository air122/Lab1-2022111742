import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MainApp {
    static Graph graph = new Graph();

    public static void main(String[] args) {
        // 如果有启动参数，直接读取文件
        if (args.length > 0) {
            graph.readFromFile(args[0]);
        }

        // 创建简单GUI
        JFrame frame = new JFrame("Graph Text Processor");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JButton loadButton = new JButton("Load Text File");
        JButton showGraphButton = new JButton("Show Directed Graph");
        JButton queryBridgeButton = new JButton("Query Bridge Words");
        JButton generateTextButton = new JButton("Generate New Text");
        JButton shortestPathButton = new JButton("Calculate Shortest Path");
        JButton pageRankButton = new JButton("Calculate PageRank");
        JButton randomWalkButton = new JButton("Random Walk");

        frame.add(loadButton);
        frame.add(showGraphButton);
        frame.add(queryBridgeButton);
        frame.add(generateTextButton);
        frame.add(shortestPathButton);
        frame.add(pageRankButton);
        frame.add(randomWalkButton);

        // 加载文件按钮事件
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                graph.readFromFile(file.getAbsolutePath());
                JOptionPane.showMessageDialog(frame, "File loaded successfully!");
            }
        });

        // 展示图
        showGraphButton.addActionListener(e -> {
            graph.showDirectedGraph();
            graph.saveGraphAsImage("output.jpg");

        });

        // 查询桥接词
        queryBridgeButton.addActionListener(e -> {
            String word1 = JOptionPane.showInputDialog(frame, "Enter word1:");
            String word2 = JOptionPane.showInputDialog(frame, "Enter word2:");
            String result = graph.queryBridgeWords(word1, word2);
            JOptionPane.showMessageDialog(frame, result);
        });

        // 生成新文本
        generateTextButton.addActionListener(e -> {
            String inputText = JOptionPane.showInputDialog(frame, "Enter new text:");
            String result = graph.generateNewText(inputText);
            JOptionPane.showMessageDialog(frame, result);
        });

        // 最短路径
        shortestPathButton.addActionListener(e -> {
            String word1 = JOptionPane.showInputDialog(frame, "Enter start word:");
            String word2 = JOptionPane.showInputDialog(frame, "Enter end word:");
            String result = graph.calcShortestPath(word1, word2);
            JOptionPane.showMessageDialog(frame, result);
        });

        // PageRank
        pageRankButton.addActionListener(e -> {
            String word = JOptionPane.showInputDialog(frame, "Enter word:");
            Double result = graph.calPageRank(word);
            if (result != null) {
                JOptionPane.showMessageDialog(frame, "PageRank of " + word + ": " + result);
            } else {
                JOptionPane.showMessageDialog(frame, "Word not found in graph.");
            }
        });

        // 随机游走
        randomWalkButton.addActionListener(e -> {
            String result = graph.randomWalk();
            JOptionPane.showMessageDialog(frame, result);
        });

        frame.setVisible(true);
    }
}
