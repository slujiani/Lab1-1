import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/*this is the first edit in Lab 3*/

public class Main extends JFrame {

  JButton readFileButton;
  JButton buildButton;
  JButton randomShiftButton;
  JButton spButton;
  JButton bridgeWordButton;
  JButton updateGraphButton;
  JButton resetButton;
  JButton geneNewTextButton;
  JTextField sourceText;
  JTextField destText;
  JTextField textFilePathText;
  JTextField resultText;
  JLabel imageLabel = new JLabel();
  public static int randomWalkDelayTime = 500; // Unit: ms
  Font font = new Font("榛戜綋", Font.PLAIN, 12);

  public static boolean ranFlag = false;

  public static class Graph {
    /*
     * Edge Class:store edges in the graph paramaters: int weightIndex: the
     * number of the edge boolean walked: a flag to judge in the Random Walk
     * process int from: the number of start word in the edge int to: the number
     * of destination in the edge
     */
    class Edge {
      int weightIndex;
      boolean walked;
      int from = 0;
      int to = 0;

      public Edge(int initWeight, int initFrom, int initTo) {
        weightIndex = edgeNum;
        edgeNum += 1;
        weight[weightIndex] = initWeight;
        from = initFrom;
        to = initTo;
      }

      public void addWeight() {
        weight[weightIndex] += 1;
      }

      public int getWeight() {
        return weight[weightIndex];
      }

      public int getGlobalIndex() {
        return weightIndex;
      }

      public int getFrom() {
        return from;
      }

      public int getTo() {
        return to;
      }
    }

    class Vertex {
      int index;
      String content;
      ArrayList<Edge> edges = null;

      public Vertex(String initContent) {
    
        content = initContent;
        edges = new ArrayList<Edge>();
        index = 0;
      }

      public int getEdges() {
        if (edges != null) {
          return edges.size();
        } else {
          return 0;
        }
      }

      public void addEdge(int nextIndex) {
        Edge current = getEdge(nextIndex);
        if (current == null) {
          current = new Edge(1, index, nextIndex);
          edges.add(current);
        } else {
          current.addWeight();
        }
      }

      public void setIndex(int index) {
        this.index = index;
      }

      public void setContent(String newContent) {
        
        content = newContent;
      }

      public String getContent() {
        return content;
      }

      public int getIndex() {
        return index;
      }

      public Edge getEdge(int nextIndex) {
        for (int i = 0; i < edges.size(); i++) {
          Edge currentEdge = edges.get(i);
          if (currentEdge.getTo() == nextIndex) {
            return currentEdge;
          }
        }
        return null;
      }
    }

    ArrayList<Vertex> vertexList;
    int[] weight;
    int edgeNum;
    int[] distance;
    boolean[] visited;
    boolean[] walked;
    ArrayList<Edge>[] intestEdge;
    HashMap<String, Vertex> contentMap;
    public ArrayList<String> sequence;

    /** new a graph. */
    public Graph() {
      int maxWeightListSize = 2000000;
      weight = new int[maxWeightListSize];
      edgeNum = 0;
      contentMap = new HashMap<String, Vertex>();
      sequence = new ArrayList<String>();
      vertexList = new ArrayList<Vertex>();
    }

    Vertex addVertex(String word) {
      int index = vertexList.size();
      Vertex current = new Vertex(word);

      current.setIndex(index);
      vertexList.add(current);

      contentMap.put(word, vertexList.get(vertexList.size() - 1));
      return current;
    }

    /** read file. */
    public void fileRead(String filename) {
      sequence = new ArrayList<String>();
      File file = new File(filename);
      try {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        try {
          line = br.readLine();
          while (line != null) {
            String piece = "";
            for (int i = 0; i < line.length(); i++) {
              if ((line.charAt(i) >= 'A' && line.charAt(i) <= 'Z')
                  || (line.charAt(i) >= 'a' && line.charAt(i) <= 'z')) {
                piece += line.charAt(i);
              } else {
                if (!piece.equals("")) {
                  sequence.add(piece.toLowerCase());
                }
                piece = "";
              }
            }
            if (!piece.equals("")) {
              sequence.add(piece.toLowerCase());
            }
            line = br.readLine();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    /** add path. */
    public void addPath() {
      String headContent = sequence.get(0);
      Vertex current = contentMap.get(headContent);

      if (current == null) {
        current = addVertex(headContent);
      }

      for (int i = 1; i < sequence.size(); i++) {
        String nextContent = sequence.get(i);
        Vertex nextVertex = contentMap.get(nextContent);

        if (nextVertex == null) {
          nextVertex = addVertex(nextContent);
        }
        int nextIndex = nextVertex.getIndex();
        current.addEdge(nextIndex);
        current = nextVertex;

      }
      for (int i = 0; i < vertexList.size(); i++) {
        Vertex v = vertexList.get(i);
        //System.out.println(v.content + " " + v.getEdges());
      }
    }

    class ComparisonPairs {
      int key;
      int value;

      public ComparisonPairs(int initKey, int initValue) {
        key = initKey;
        value = initValue;
      }
    }

    /** get shortest path orz. */
    public void getshortestPath(int sourceVertexIndex) {
      Vertex sourceVertex = vertexList.get(sourceVertexIndex);

      distance = new int[vertexList.size()];
      intestEdge = new ArrayList[vertexList.size()];
      cleanVisited();
      for (int i = 0; i < vertexList.size(); i++) {
        distance[i] = 0x7fffffff;
      }

      distance[sourceVertexIndex] = 0;

      PriorityQueue<ComparisonPairs> heap = new PriorityQueue<ComparisonPairs>(
          new Comparator<ComparisonPairs>() {
          public int compare(ComparisonPairs p1, ComparisonPairs p2) {
            return p1.key - p2.key;
          }
        });

      ComparisonPairs headPair = new ComparisonPairs(
          distance[sourceVertexIndex], sourceVertexIndex
        );
      heap.add(headPair);

      while (!heap.isEmpty()) {
        while (!heap.isEmpty() && visited[heap.peek().value]) {
          heap.poll();
        }
        if (heap.isEmpty()) {
          break;
        }
        int current = heap.poll().value;
        Vertex currentVertex = vertexList.get(current);
        visited[current] = true;
        for (int i = 0; i < currentVertex.edges.size(); i++) {
          Edge currentEdge = currentVertex.edges.get(i);
          int newDistance = distance[currentEdge.getFrom()] + currentEdge.getWeight();
          int prevDistance = distance[currentEdge.getTo()];

          if (newDistance < prevDistance) {
            distance[currentEdge.getTo()] = newDistance;
            ArrayList<Edge> newEdgeList = new ArrayList<Edge>();
            newEdgeList.add(currentEdge);
            intestEdge[currentEdge.getTo()] = newEdgeList;
            ComparisonPairs nextPair = new ComparisonPairs(
                distance[currentEdge.getTo()], currentEdge.getTo()
              );
            heap.add(nextPair);
          } else if (newDistance == prevDistance) {
            intestEdge[currentEdge.getTo()].add(currentEdge);
          }
        }
      }
    }

    /** clean visited . */
    public void cleanVisited() {
      visited = new boolean[vertexList.size()];
      for (int i = 0; i < vertexList.size(); i++) {
        visited[i] = false;
      }
    }

    int shortestPath(final String sourceString, final String targetString) {
      Vertex sourceVertex = contentMap.get(sourceString);
      Vertex targetVertex = contentMap.get(targetString);
      if (sourceVertex == null) {
        return -1;
      }
      int sourceVertexIndex = sourceVertex.getIndex();
      getshortestPath(sourceVertexIndex);
      if (targetVertex == null) {
        return -2;
      }
      int targetVertexIndex = targetVertex.index;
      //System.out.println(targetVertexIndex);
      if (sourceVertex == null || targetVertex == null) {
        return 0x7fffffff;
      }
      for (int i = 0; i < vertexList.size(); i++) {
        String byCheck = "The i th " + vertexList.get(i).content + " is " + i + " : " + distance[i];
        //System.out.println(byCheck);
      }
      return distance[targetVertexIndex];
    }

    public boolean breakWalkingFlag = false;

    public void breakWalking() {
      this.breakWalkingFlag = true;
    }

    private boolean hasUnwalkedEdge(Vertex current) {
      if (current.edges.size() == 0) {
        return false;
      } else {
        boolean hasNextUnwalkedEdge = false;
        for (int i = 0; i < current.edges.size(); i++) {
          if (!walked[current.edges.get(i).getGlobalIndex()]) {
            hasNextUnwalkedEdge = true;
            break;
          }
        }
        if (hasNextUnwalkedEdge) {
          return true;
        } else {
          return false;
        }
      }
    }

    /** rrandom walk. */
    public String randomWalking(int delayTime) {
      Random random = new Random();
      int currentIndex = random.nextInt(vertexList.size() - 1);
      String text = "";
      Vertex current = vertexList.get(currentIndex);
      cleanWalkedEdge();
      text += current.getContent();

      while (!breakWalkingFlag && hasUnwalkedEdge(current) && ranFlag) {
        try {
          Thread.currentThread().sleep(delayTime);
        } catch (Exception timeDelayError) {
          timeDelayError.printStackTrace();
        }

        int nextEdgeIndex;
        nextEdgeIndex = random.nextInt(current.edges.size());

        Edge currentEdge = current.edges.get(nextEdgeIndex);
        if (walked[currentEdge.getGlobalIndex()]) {
          break;
        }
        walked[currentEdge.getGlobalIndex()] = true;
        currentIndex = currentEdge.getTo();
        current = vertexList.get(currentIndex);
        text += " -> " + current.getContent();
      }
      if (breakWalkingFlag == false) {
        //System.out.println("false");
      }
      breakWalkingFlag = false;
      ranFlag = false;
      if (breakWalkingFlag == false) {
        //System.out.println("reset complete");
      }
      return text;
    }

    /** clean walk edge. */
    public void cleanWalkedEdge() {
      walked = new boolean[edgeNum];
      for (int i = 0; i < edgeNum; i++) {
        walked[i] = false;
      }
    }

    /** get bridge words by vertex. */
    public ArrayList<Vertex> getBridgeWords(Vertex vertex1, Vertex vertex2) {
      ArrayList<Vertex> bridgeVertex = new ArrayList<>();
      cleanVisited();
      for (int i = 0; i < vertex1.edges.size(); i++) {
        Edge edge1 = vertex1.edges.get(i);
        Vertex nextVertex = vertexList.get(edge1.to);
        for (int j = 0; j < nextVertex.edges.size(); j++) {
          Edge edge2 = nextVertex.edges.get(j);
          if (edge2.getTo() == vertex2.index) {
            if (!visited[edge1.getTo()]) {
              bridgeVertex.add(vertexList.get(edge1.getTo()));
              visited[edge1.getTo()] = true;
            }
          }
        }
      }
      return bridgeVertex;
    }

    /** get bridge words by string. */
    public String getBridgeWords(String str1, String str2) {
      StringBuffer answer = new StringBuffer("");
      Vertex vertex1 = contentMap.get(str1);
      Vertex vertex2 = contentMap.get(str2);
      // System.out.println(vertex1.content+"+"+vertex2.content);
      if (vertex1 == null || vertex2 == null) {
        answer.append("No word1 or word2 in the graph!") ;
      } else {
        ArrayList<Vertex> bridgeVertex = getBridgeWords(vertex1, vertex2);
        if (bridgeVertex.size() == 0) {
          answer.append("No bridge words from word1 and word2");
        } else {
          answer.append("The bridge words from word1 to word2 are: ");
          if (bridgeVertex.size() == 1) {
            answer.append(bridgeVertex.get(0).getContent() + ".");
          } else {
            for (int i = 0; i < bridgeVertex.size() - 1; i++) {
              answer.append(bridgeVertex.get(i).getContent() + ", ");
            }
            answer.append("and " + bridgeVertex.get(bridgeVertex.size() - 1).getContent() + ".");
          }
        }
      }
      return answer.toString();
    }

    boolean isLetter(char x) {
      return ('a' <= x && x <= 'z') || ('A' <= x && x <= 'Z');
    }

    /** generate new text. */
    public String generateNewText(String originalText) {
      int idx = 0;
      String newText = "";
      String current = "";
      String temp ;
      while (idx < originalText.length() && !isLetter(originalText.charAt(idx))) {
        newText += originalText.charAt(idx);
        idx += 1;
      }
      while (idx < originalText.length() && isLetter(originalText.charAt(idx))) {
        newText += originalText.charAt(idx);
        current += originalText.charAt(idx);
        idx += 1;
      }

      while (idx < originalText.length() ) {
        temp = "";
        String nextWord = "";

        while (idx < originalText.length() && !isLetter(originalText.charAt(idx))) {
          temp += originalText.charAt(idx);
          idx += 1;
        }

        while (idx < originalText.length() && isLetter(originalText.charAt(idx))) {
          nextWord += originalText.charAt(idx);
          idx += 1;
        }

        Vertex vertex1 = contentMap.get(current);
        Vertex vertex2 = contentMap.get(nextWord);

        if (vertex1 != null && vertex2 != null) {
          ArrayList<Vertex> bridgeWords = getBridgeWords(vertex1, vertex2);
          if (bridgeWords.size() != 0) {
            Random random = new Random();
            Vertex bridge = bridgeWords.get(random.nextInt(bridgeWords.size()));
            newText += " " + bridge.getContent();
          }
        }
        newText += temp + nextWord;
        current = nextWord;
      }
      return newText;
    }

    /** get fully paths. */
    public ArrayList<ArrayList<String>> getFullyPaths(Vertex currentVertex, Vertex targetVertex) {
      ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

      if (currentVertex == targetVertex) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add(targetVertex.content);
        result.add(ret);
        return result;
      } else {
        int currentIndex = currentVertex.getIndex();
        for (int i = 0; i < intestEdge[currentIndex].size(); i++) {
          Edge nextEdge = intestEdge[currentIndex].get(i);
          int nextIndex = nextEdge.getFrom();
          Vertex nextVertex = vertexList.get(nextIndex);

          if (distance[nextIndex] + nextEdge.getWeight() == distance[currentIndex]) {
            ArrayList<ArrayList<String>> nextResult = getFullyPaths(nextVertex, targetVertex);

            for (int j = 0; j < nextResult.size(); j++) {
              ArrayList<String> currentPath = nextResult.get(j);
              currentPath.add(currentVertex.content);

              result.add(currentPath);
            }
          }
        }
      }

      return result;
    }
  }

  public static Graph g = new Graph();
  public  GraphViz gv;

  /*
   * 鈥� main(String[] args) 锛氫富绋嬪簭鍏ュ彛锛屾帴鏀剁敤鎴疯緭鍏ユ枃浠讹紝鐢熸垚鍥撅紝骞跺厑璁哥敤鎴烽�夋嫨鍚庣画鍚勯」鍔熻兘锛�
   * 鈥� type createDirectedGraph(String filename) 锛氱敓鎴愭湁鍚戝浘 鈥� void
   * showDirectedGraph(Graph g) 锛氬睍绀烘湁鍚戝浘 鈥�
   */
  Graph createDirectedGraph(String filename) {
    Graph current = new Graph();
    current.fileRead(filename);
    current.addPath();
    return current;
  }

  void showDirectedGraph(Graph g) {
    gv = new GraphViz();
    gv.addln(gv.start_graph());
    for (int i = 0; i < g.vertexList.size(); i++) {
      String s = g.vertexList.get(i).content;
      for (int j = 0; j < g.vertexList.get(i).getEdges(); j++) {
        int ttNum = g.vertexList.get(i).edges.get(j).to;
        String t = g.vertexList.get(ttNum).content;
        String byCheck = s + " -> " + t + " [ " + "label=\"";
        byCheck += g.vertexList.get(i).edges.get(j).getWeight() + "\" ]" + ";";
        gv.addln(byCheck);
      }
    }
    gv.addln(gv.end_graph());
    //System.out.println(gv.getDotSource());
    String type = "png";
    File out = new File("result." + type);
    gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);

    ImageIcon image = new ImageIcon("result.png");
    double width = (double) image.getIconWidth();
    double height = (double) image.getIconHeight();
    //System.out.println(width + " " + height);
    double x1 = width / 480;
    double x2 = height / 750;
    double x = max(x1, x2);
    image.setImage(
        image.getImage().getScaledInstance(
            (int) (width / x), (int) (height / x), Image.SCALE_DEFAULT
            )
    );
    imageLabel.setIcon(image);
  }
  /*
   * String queryBridgeWords(type G, String word1, String word2) 锛� 鏌ヨ妗ユ帴璇� 鈥�
   * String generateNewText(type G, String inputText) 锛氭牴鎹産ridge word 鐢熸垚鏂版枃鏈�
   * 鈥� String calcShortestPath(type G, String word1, String word2) 锛�
   * 璁＄畻涓や釜鍗曡瘝涔嬮棿鐨勬渶鐭矾寰� 鈥� String randomWalk(type G) 锛氶殢鏈烘父璧�
   */

  String queryBridgeWords(Graph graph, String word1, String word2) {
    return graph.getBridgeWords(word1, word2);
  }

  String generateNewText(Graph graph, String inputText) {
    return graph.generateNewText(inputText);
  }

  String calcShortestPath(Graph graph, String word1, String word2) {
    int result = graph.shortestPath(word1, word2);
    String eririStr = " ";
    if (result == -1) {
      eririStr += "source word not found";
    } else if (result == -2) {
      eririStr += "dest word not found";
    } else {
      //System.out.println(word1 + "->" + word2 + ":" + result);
      ArrayList<ArrayList<String>> paths = new ArrayList();
      if (result != 0x7fffffff) {
        paths = g.getFullyPaths(g.contentMap.get(word2), g.contentMap.get(word1));
      }
      gv = new GraphViz();
      //HashMap temp = new HashMap();
      gv.addln(gv.start_graph());
      for (int i = 0; i < g.vertexList.size(); i++) {
        String s = g.vertexList.get(i).content;
        for (int j = 0; j < g.vertexList.get(i).getEdges(); j++) {
          int ttNum = g.vertexList.get(i).edges.get(j).to;
          String t = g.vertexList.get(ttNum).content;
          gv.addln(s + " -> " + t + " [ " 
              + "label=\"" + g.vertexList.get(i).edges.get(j).getWeight() 
              + "\" ]" + ";");
        }
      }
      for (int i = 0; i < paths.size(); i++) {
        ArrayList<String> currentPath = paths.get(i);
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        Color randomColor = new Color(r, g, b);
        String strColor = "#" 
            + Integer.toHexString(randomColor.getRed()) 
            + Integer.toHexString(randomColor.getGreen())
            + Integer.toHexString(randomColor.getBlue());
        //System.out.println(strColor);
        for (int j = 0; j < currentPath.size() - 1; j++) {
          String s = currentPath.get(j);
          String t = currentPath.get(j + 1);
          gv.addln(s + " -> " + t + " [ " + " color=" + "\"" + strColor + "\"" + " ]" + ";");
        }
      }
      gv.addln(gv.end_graph());
      // System.out.println(gv.getDotSource());
      String type = "png";
      File out = new File("result." + type);
      gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
      ImageIcon image = new ImageIcon("result.png");
      double width = (double) image.getIconWidth();
      double height = (double) image.getIconHeight();
      //System.out.println(width + " " + height);
      double x1 = width / 480;
      double x2 = height / 750;
      double x = max(x1, x2);
      image.setImage(
          image.getImage().getScaledInstance(
              (int) (width / x), (int) (height / x), Image.SCALE_DEFAULT
            )
      );
      imageLabel.setIcon(image);
    }
    // System.out.println(word1+"->"+word2+"="+result);
    return "" + result + eririStr;
  }

  String randomWalk(Graph graph) {
    String result = graph.randomWalking(randomWalkDelayTime);
    resultText.setText(result);
    return result;
  }

  /*
   * RandomWalk Class: used to execute Random Walk process with multi threads
   * paramaters: Thread t: a new thread String name: the name of this thread
   * start(): the function to create a new thread run(): the function to run
   * Random Wlak process, executed automatically
   **/
  class RandomWalk implements Runnable {
    private Thread t;
    private String threadName;

    RandomWalk(String name) {
      threadName = name;
      //System.out.println("Creating " + threadName);
    }

    public void run() {
      //System.out.println("Running " + threadName);
      randomWalk(g);
      //System.out.println("Thread " + threadName + " exiting.");
    }

    public void start() {
      //System.out.println("Starting " + threadName);
      if (t == null) {
        t = new Thread(this, threadName);
        t.start();
      }
    }
  }

  /*
   * Main Class: Create GUI & set ActionListeners readFileButton: JButton of
   * Reading the source file buildButton: JButton of create a graph of text
   * spButton: JButton of calculate the shortest path of two words, then
   * generate a new graph automatically randomShiftButton: JButton of start or
   * stop random walk process bridgeWordButton: JButton of find bridge word
   * resetButton: re-generate a new graph with no colored edges
   * geneNewTextButton: JButton of generate new text using bridge word
   * sourceText: JTextField of input the source word or the source text(generate
   * new text) dest text: JTextField of input the dest word textFilePathText:
   * JTextField of input the path of the source file resultText: JTextField to
   * display the result imageLabel: display the image generated by GraphViz
   */
  /** main construction. */
  public Main() {
    setLayout(null);
    setSize(800, 800);

    readFileButton = new JButton("Read file");
    buildButton = new JButton("Build graph");
    randomShiftButton = new JButton("Random shift");
    spButton = new JButton("Shortest path");
    bridgeWordButton = new JButton("Bridge word");
    updateGraphButton = new JButton("Update graph");
    resetButton = new JButton("Reset graph");
    geneNewTextButton = new JButton("Generate new text");
    sourceText = new JTextField();
    sourceText.setFont(font);
    destText = new JTextField();
    destText.setFont(font);
    textFilePathText = new JTextField();
    textFilePathText.setFont(font);
    textFilePathText.setBounds(5, 5, 240, 30);
    sourceText = new JTextField();
    sourceText.setFont(font);
    sourceText.setBounds(5, 125, 240, 30);
    destText = new JTextField();
    destText.setFont(font);
    destText.setBounds(5, 165, 240, 30);
    resultText = new JTextField();

    JScrollPane resultTextWithScroll = new JScrollPane(resultText);
    resultTextWithScroll.setFont(font);
    resultTextWithScroll.setBounds(5, 405, 240, 60);

    textFilePathText.setText("input the text file path");
    sourceText.setText("input the source word");
    destText.setText("input the destination word");

    imageLabel.setSize(480, 750);
    imageLabel.setLocation(300, 10);
    // ImageIcon image = new ImageIcon("/Users/DongSky/result.png");
    // double width=(double)image.getIconWidth();
    // double height=(double)image.getIconHeight();
    // System.out.println(width+" "+height);
    // double x1=width/400;
    // double x2=height/550;
    // double x=max(x1,x2);
    // image.setImage(
    // image.getImage().getScaledInstance((int)(width/x),(int)(height/x),Image.SCALE_DEFAULT)
    // );
    // imageLabel.setIcon(image);
    readFileButton.setSize(240, 30);
    readFileButton.setLocation(5, 45);
    buildButton.setSize(240, 30);
    buildButton.setLocation(5, 85);
    spButton.setSize(240, 30);
    spButton.setLocation(5, 205);
    bridgeWordButton.setSize(240, 30);
    bridgeWordButton.setLocation(5, 245);
    randomShiftButton.setSize(240, 30);
    randomShiftButton.setLocation(5, 285);
    resetButton.setSize(240, 30);
    resetButton.setLocation(5, 325);
    geneNewTextButton.setSize(240, 30);
    geneNewTextButton.setLocation(5, 365);
    readFileButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String path = textFilePathText.getText();
        g = createDirectedGraph(path);
      }
    });
    buildButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showDirectedGraph(g);
      }
    });
    spButton.addActionListener(new ActionListener() {
      @Override
      /**shortest path action*/
      public void actionPerformed(ActionEvent e) {
        String sourceWord = sourceText.getText();
        String destWord = destText.getText();
        String result = "";
        try {
          result = calcShortestPath(g, sourceWord, destWord);
        } catch (Exception e1) {
          e1.printStackTrace();
          result = "not found";
        }
        //System.out.println(sourceWord + "->" + destWord + ":" + result);
        resultText.setText(sourceWord + "->" + destWord + ":" + result);
        //ArrayList<String> path = new ArrayList<String>();
        /*
         * int for(int i=0;i<g.intestEdge.length;i++) { if(i==0) {
         * path.add(g.intestEdge[i].to); }else {
         * 
         * } }
         * 
         */
      }
    });
    bridgeWordButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String sourceWord = sourceText.getText();
        String destWord = destText.getText();
        String result = queryBridgeWords(g, sourceWord, destWord);
        //System.out.println(sourceWord + "->" + destWord + ":" + result);
        resultText.setText("Bridge of " + sourceWord + "->" + destWord + ":" + result);
      }
    });
    randomShiftButton.addActionListener(new ActionListener() {
      @Override
      /** random walk action. */
      public void actionPerformed(ActionEvent e) {
        if (ranFlag == false) {
          RandomWalk r1 = new RandomWalk("ran1");
          r1.start();
          ranFlag = true;
        } else {
          ranFlag = false;
          g.breakWalking();
        }
      }
    });
    resetButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showDirectedGraph(g);
      }
    });
    geneNewTextButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String inputText = sourceText.getText();
        String result = generateNewText(g, inputText);
        resultText.setText(result);
      }
    });

    add(imageLabel);
    add(textFilePathText);
    add(sourceText);
    add(destText);
    add(resultTextWithScroll);
    add(readFileButton);
    add(buildButton);
    add(spButton);
    add(bridgeWordButton);
    add(randomShiftButton);
    add(resetButton);
    add(geneNewTextButton);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  double max(/*default*/double x1, /*default*/double x2) {
    // TODO Auto-generated method stub
    if (x1 > x2) {
      return x1;
    }
    return x2;
  }

  public static void main(String[] args) {
    new Main();
  }
}
