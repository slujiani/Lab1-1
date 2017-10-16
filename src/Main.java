import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/*this is the first edit in Lab 3*/

/* test for task4*/

public class Main extends JFrame{

    JButton readFileButton,buildButton,randomShiftButton,SPButton,bridgeWordButton,updateGraphButton,resetButton,geneNewTextButton;
    JTextField sourceText,destText,textFilePathText,resultText;
    JLabel ImageLabel=new JLabel();
    public static int randomWalkDelayTime = 500; // Unit: ms
    Font font=new Font("黑体",Font.PLAIN,12);
    
    public static boolean ranFlag=false;

    public static class Graph {
    	/*
         * Edge Class:store edges in the graph
         * paramaters:
         *     int weightIndex: the number of the edge
         *     boolean walked: a flag to judge in the Random Walk process
         *     int from: the number of start word in the edge
         *     int to: the number of destination in the edge
         *     */
        class Edge {
            int weightIndex;
            boolean walked;
            int from, to;
            
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
            ArrayList<Edge> edges=null;

            public Vertex(String initContent) {
                content = new String();
                content = initContent;
                edges = new ArrayList<Edge>();
                index = 0;
            }
            public int getEdges(){
                if(edges!=null)return edges.size();
                else return 0;
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
                content = new String();
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
        int [] weight;
        int edgeNum;
        int [] distance;
        boolean [] visited;
        boolean [] walked;
        ArrayList<Edge> [] intestEdge;
        HashMap<String, Vertex> contentMap;
        public ArrayList<String> Sequence;

        public Graph() {
            int maxWeightListSize = 2000000;
            weight = new int[maxWeightListSize];
            edgeNum = 0;
            contentMap = new HashMap<String, Vertex>();
            Sequence = new ArrayList<String>();
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

        public void fileRead(String filename) {
            Sequence=new ArrayList<String>();
            File file=new File(filename);
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(reader);
                String line = "";
                try {
                    line = br.readLine();
                    while (line != null) {
                        String piece = "";
                        for (int i = 0; i < line.length(); i++) {
                            if ((line.charAt(i) >= 'A' && line.charAt(i) <= 'Z') ||
                                    (line.charAt(i) >= 'a' && line.charAt(i) <= 'z')) {
                                piece += line.charAt(i);
                            }
                            else {
                                if(piece!="")Sequence.add(piece);
                                piece = "";
                            }
                        }
                        if (piece != "") {
                            Sequence.add(piece);
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

        public void addPath() {
            String headContent = Sequence.get(0);
            Vertex current = contentMap.get(headContent);

            if (current == null) {
                current = addVertex(headContent);
            }

            for (int i = 1; i < Sequence.size(); i++) {
                String nextContent = Sequence.get(i);
                Vertex nextVertex = contentMap.get(nextContent);

                if (nextVertex == null) {
                    nextVertex = addVertex(nextContent);
                }
                int nextIndex = nextVertex.getIndex();
                current.addEdge(nextIndex);
                current = nextVertex;

            }
            for(int i=0;i<vertexList.size();i++){
                Vertex v=vertexList.get(i);
                System.out.println(v.content+" "+v.getEdges());
            }
        }

        class ComparisonPairs {
            int key, value;
            public ComparisonPairs(int initKey, int initValue) {
                key = initKey;
                value = initValue;
            }
        }

        public void getshortestPath(int sourceVertexIndex) {
            Vertex sourceVertex = vertexList.get(sourceVertexIndex);

            distance = new int[vertexList.size()];
            intestEdge = new ArrayList [vertexList.size()];
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
                    }
            );

            ComparisonPairs headPair = new ComparisonPairs(distance[sourceVertexIndex], sourceVertexIndex);
            heap.add(headPair);

            while (!heap.isEmpty()) {
                while (!heap.isEmpty() && visited[heap.peek().value]) {
                    heap.poll();
                }
                if (heap.isEmpty()) break;

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
                        ComparisonPairs nextPair = new ComparisonPairs(distance[currentEdge.getTo()], currentEdge.getTo());
                        heap.add(nextPair);
                    } else if (newDistance == prevDistance) {
                        intestEdge[currentEdge.getTo()].add(currentEdge);
                    }
                }
            }
        }

        public void cleanVisited() {
            visited = new boolean[vertexList.size()];
            for (int i = 0; i < vertexList.size(); i++) {
                visited[i] = false;
            }
        }

        int shortestPath(String sourceString, String targetString) {
            Vertex sourceVertex = contentMap.get(sourceString);
            Vertex targetVertex = contentMap.get(targetString);
            if(sourceVertex==null)return -1;
            int sourceVertexIndex = sourceVertex.getIndex();
            getshortestPath(sourceVertexIndex);
            if(targetVertex==null)return -2;
            int targetVertexIndex = targetVertex.index;
            System.out.println(targetVertexIndex);
            if(sourceVertex==null||targetVertex==null)return 0x7fffffff;
            for (int i = 0; i < vertexList.size(); i++) 
            		System.out.println("The i th "+vertexList.get(i).content+" is " + i + " : " + distance[i]);
            return distance[targetVertexIndex];
        }

        public boolean breakWalkingFlag=false;

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

        public String randomWalking(int delayTime) {
            Random random = new Random();
            int currentIndex = random.nextInt(vertexList.size() - 1);
            String text = "";
            Vertex current = vertexList.get(currentIndex);
            cleanWalkedEdge();
            text += current.getContent();

            while (!breakWalkingFlag && hasUnwalkedEdge(current)&&ranFlag) {
                try {
                    Thread.currentThread().sleep(delayTime);
                } catch (Exception TimeDelayError) {}

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
            if(breakWalkingFlag==false)System.out.println("false");
            breakWalkingFlag=false;
            ranFlag=false;
            if(breakWalkingFlag==false)System.out.println("reset complete");
            return text;
        }

        public void cleanWalkedEdge() {
            walked = new boolean[edgeNum];
            for (int i = 0; i < edgeNum; i++) {
                walked[i] = false;
            }
        }

        public ArrayList<Vertex> getBridgeWords(Vertex vertex1, Vertex vertex2) {
            ArrayList<Vertex> bridgeVertex = new ArrayList<>();
            cleanVisited();
            for (int i = 0; i < vertex1.edges.size(); i++) {
                Edge edge1 = vertex1.edges.get(i);
                Vertex nextVertex=vertexList.get(edge1.to);
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

        public String getBridgeWords(String str1, String str2) {
            String answer = new String();
            Vertex vertex1 = contentMap.get(str1), vertex2 = contentMap.get(str2);
            //System.out.println(vertex1.content+"+"+vertex2.content);
            if (vertex1 == null || vertex2 == null) {
                answer = "No word1 or word2 in the graph!";
            } else {
                ArrayList<Vertex> bridgeVertex = getBridgeWords(vertex1, vertex2);
                if (bridgeVertex.size() == 0) {
                    answer = "No bridge words from word1 and word2";
                } else {
                    answer = "The bridge words from word1 to word2 are: ";
                    if (bridgeVertex.size() == 1) {
                        answer = answer + bridgeVertex.get(0).getContent() + ".";
                    } else {
                        for (int i = 0; i < bridgeVertex.size() - 1; i++) {
                            answer = answer + bridgeVertex.get(i).getContent() + ", ";
                        }
                        answer = answer + "and " + bridgeVertex.get(bridgeVertex.size() - 1).getContent() + ".";
                    }
                }
            }
            return answer;
        }

        boolean isLetter(char x) {
            return ('a' <= x && x <= 'z') || ('A' <= x && x <= 'Z');
        }

        public String generateNewText(String originalText) {
            int idx = 0;
            String newText = "";
            String current = "";
            String temp = new String();
            while (idx < originalText.length()&&!isLetter(originalText.charAt(idx))) {
                newText += originalText.charAt(idx);
                idx += 1;
            }
            while (idx < originalText.length()&&isLetter(originalText.charAt(idx))) {
                newText += originalText.charAt(idx);
                current += originalText.charAt(idx);
                idx += 1;
            }

            while (idx < originalText.length()&&idx < originalText.length()) {
                temp = new String("");
                String nextWord = new String("");

                while (idx < originalText.length()&&!isLetter(originalText.charAt(idx))) {
                    temp += originalText.charAt(idx);
                    idx += 1;
                }

                while (idx < originalText.length()&&isLetter(originalText.charAt(idx))) {
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
        public ArrayList< ArrayList<String> > getFullyPaths(Vertex currentVertex, Vertex targetVertex) {
            ArrayList< ArrayList<String> > result = new ArrayList< ArrayList<String> >();

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
                        ArrayList< ArrayList<String> > nextResult = getFullyPaths(nextVertex, targetVertex);

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
    
    public static Graph g=new Graph();
    public static GraphViz gv;
    /*–
    main(String[] args)
    ：主程序入口，接收用户输入文件，生成图，并允许用户选择后续各项功能；
    –
    type createDirectedGraph(String filename)
    ：生成有向图
    –
    void showDirectedGraph(Graph g)
    ：展示有向图
    –
*/
    Graph createDirectedGraph(String filename) {
        Graph current = new Graph();
        current.fileRead(filename);
        current.addPath();
        return current;
    }
    void showDirectedGraph(Graph g) {
        gv=new GraphViz();
        gv.addln(gv.start_graph());
        for(int i=0;i<g.vertexList.size();i++) {
            String s=g.vertexList.get(i).content;
            for(int j=0;j<g.vertexList.get(i).getEdges();j++) {
                int t_num=g.vertexList.get(i).edges.get(j).to;
                String t=g.vertexList.get(t_num).content;
                gv.addln(s+" -> "+t+" [ "+"label=\""+g.vertexList.get(i).edges.get(j).getWeight()+"\" ]"+";");
            }
        }
        gv.addln(gv.end_graph());
        System.out.println(gv.getDotSource());
        String type = "png";
        File out=new File("result."+type);
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(),type), out);

        ImageIcon image = new ImageIcon("result.png");
        double width=(double)image.getIconWidth();
        double height=(double)image.getIconHeight();
        System.out.println(width+" "+height);
        double x1=width/480;
        double x2=height/750;
        double x=max(x1,x2);
        image.setImage(image.getImage().getScaledInstance((int)(width/x),(int)(height/x),Image.SCALE_DEFAULT));
        ImageLabel.setIcon(image);
    }
    /*
    String queryBridgeWords(type G, String word1, String word2)
    ：
    查询桥接词
    –
    String generateNewText(type G, String inputText)
    ：根据bridge word 生成新文本
    –
    String calcShortestPath(type G, String word1, String word2)
    ：
    计算两个单词之间的最短路径
    –
    String randomWalk(type G)
    ：随机游走
    */

    String queryBridgeWords(Graph G, String word1, String word2) {
        return G.getBridgeWords(word1, word2);
    }

    String generateNewText(Graph G, String inputText) {
        return G.generateNewText(inputText);
    }

    String calcShortestPath(Graph G, String word1, String word2) {
        int result=G.shortestPath(word1, word2);
        String eririStr=" ";
        if(result==-1) {
        		eririStr+="source word not found";
        }
        else if(result==-2) {
        		eririStr+="dest word not found";
        }
        else {
        System.out.println(word1+"->"+word2+":"+result);
        ArrayList< ArrayList<String> > paths=new ArrayList();
        if(result!=0x7fffffff)paths=g.getFullyPaths(g.contentMap.get(word2),g.contentMap.get(word1));
        gv=new GraphViz();
        HashMap temp=new HashMap();
        gv.addln(gv.start_graph());
        for(int i=0;i<g.vertexList.size();i++) {
            String s=g.vertexList.get(i).content;
            for(int j=0;j<g.vertexList.get(i).getEdges();j++) {
                int t_num=g.vertexList.get(i).edges.get(j).to;
                String t=g.vertexList.get(t_num).content;
                gv.addln(s+" -> "+t+" [ "+"label=\""+g.vertexList.get(i).edges.get(j).getWeight()+"\" ]"+";");
            }
        }
        for(int i=0;i<paths.size();i++) {
        		ArrayList<String> currentPath=paths.get(i);
        		Random rand=new Random();
        		float r=rand.nextFloat();
        		float g=rand.nextFloat();
        		float b=rand.nextFloat();
        		Color randomColor=new Color(r,g,b);
        		String strColor="#"+Integer.toHexString(randomColor.getRed())+Integer.toHexString(randomColor.getGreen())+Integer.toHexString(randomColor.getBlue());
        		System.out.println(strColor);
        		for(int j=0;j<currentPath.size()-1;j++) {
        			String s=currentPath.get(j);
        			String t=currentPath.get(j+1);
        			gv.addln(s+" -> "+t+" [ "+" color="+"\""+strColor+"\""+" ]"+";");
        		}
        	}
        gv.addln(gv.end_graph());
        //System.out.println(gv.getDotSource());
        String type = "png";
        File out=new File("result."+type);
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(),type), out);
        ImageIcon image = new ImageIcon("result.png");
        double width=(double)image.getIconWidth();
        double height=(double)image.getIconHeight();
        System.out.println(width+" "+height);
        double x1=width/480;
        double x2=height/750;
        double x=max(x1,x2);
        image.setImage(image.getImage().getScaledInstance((int)(width/x),(int)(height/x),Image.SCALE_DEFAULT));
        ImageLabel.setIcon(image);
        }
        //System.out.println(word1+"->"+word2+"="+result);
        return ""+result+eririStr;
    }
    String randomWalk(Graph G) {
        String result= G.randomWalking(randomWalkDelayTime);
        resultText.setText(result);
        return result;
    }
    /*
     * RandomWalk Class: used to execute Random Walk process with multi threads
     * paramaters:
     *     Thread t: a new thread
     *     String name: the name of this thread
     *     start(): the function to create a new thread
     *     run(): the function to run Random Wlak process, executed automatically
     **/
    class RandomWalk implements Runnable {
    	   private Thread t;
    	   private String threadName;
    	   RandomWalk(String name) {
    	      threadName = name;
    	      System.out.println("Creating " +  threadName );
    	   }
    	   public void run() {
    	      System.out.println("Running " +  threadName );
    	      randomWalk(g);
    	      System.out.println("Thread " +  threadName + " exiting.");
    	   }
    	   
    	   public void start () {
    	      System.out.println("Starting " +  threadName );
    	      if (t == null) {
    	         t = new Thread (this, threadName);
    	         t.start ();
    	      }
    	   }
    	}
    
    /*
     * Main Class: Create GUI & set ActionListeners
     * readFileButton: JButton of Reading the source file
     * buildButton: JButton of create a graph of text
     * SPButton: JButton of calculate the shortest path of two words, then generate a new graph automatically
     * randomShiftButton: JButton of start or stop random walk process
     * bridgeWordButton: JButton of find bridge word
     * resetButton: re-generate a new graph with no colored edges
     * geneNewTextButton: JButton of generate new text using bridge word
     * sourceText: JTextField of input the source word or the source text(generate new text)
     * dest text: JTextField of input the dest word
     * textFilePathText: JTextField of input the path of the source file
     * resultText: JTextField to display the result
     * ImageLabel: display the image generated by GraphViz*/
    
    public Main() {
        setLayout(null);
        setSize(800,800);

        readFileButton=new JButton("Read file");
        buildButton=new JButton("Build graph");
        randomShiftButton=new JButton("Random shift");
        SPButton=new JButton("Shortest path");
        bridgeWordButton=new JButton("Bridge word");
        updateGraphButton=new JButton("Update graph");
        resetButton=new JButton("Reset graph");
        geneNewTextButton=new JButton("Generate new text");
        sourceText=new JTextField();
        sourceText.setFont(font);
        destText=new JTextField();
        destText.setFont(font);
        textFilePathText=new JTextField();
        textFilePathText.setFont(font);
        textFilePathText.setBounds(5, 5, 240, 30);
        sourceText=new JTextField();
        sourceText.setFont(font);
        sourceText.setBounds(5, 125, 240, 30);
        destText=new JTextField();
        destText.setFont(font);
        destText.setBounds(5, 165, 240, 30);
        resultText=new JTextField();
        resultText.setFont(font);
        resultText.setBounds(5, 405, 240, 60);
        

        textFilePathText.setText("input the text file path");
        sourceText.setText("input the source word");
        destText.setText("input the destination word");


        ImageLabel.setSize(480, 750);
        ImageLabel.setLocation(300, 10);
//		ImageIcon image = new ImageIcon("/Users/DongSky/result.png");
//		double width=(double)image.getIconWidth();
//		double height=(double)image.getIconHeight();
//		System.out.println(width+" "+height);
//		double x1=width/400;
//		double x2=height/550;
//		double x=max(x1,x2);
//		image.setImage(image.getImage().getScaledInstance((int)(width/x),(int)(height/x),Image.SCALE_DEFAULT));
//		ImageLabel.setIcon(image);
        readFileButton.setSize(240, 30);
        readFileButton.setLocation(5, 45);
        buildButton.setSize(240,30);
        buildButton.setLocation(5, 85);
        SPButton.setSize(240,30);
        SPButton.setLocation(5, 205);
        bridgeWordButton.setSize(240,30);
        bridgeWordButton.setLocation(5, 245);
        randomShiftButton.setSize(240,30);
        randomShiftButton.setLocation(5, 285);
        resetButton.setSize(240,30);
        resetButton.setLocation(5,325);
        geneNewTextButton.setSize(240,30);
        geneNewTextButton.setLocation(5, 365);
        readFileButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String path=textFilePathText.getText();
                g=createDirectedGraph(path);
            }
        });
        buildButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            		showDirectedGraph(g);
            }
        });
        SPButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourceWord=sourceText.getText();
                String destWord=destText.getText();
                String result="";
                try {
                		result=calcShortestPath(g, sourceWord,destWord);
                }catch(Exception e1) {
                		e1.printStackTrace();
                		result="not found";
                }
                System.out.println(sourceWord+"->"+destWord+":"+result);
                resultText.setText(sourceWord+"->"+destWord+":"+result);
                ArrayList<String> path=new ArrayList<String>();
/*                int 
                for(int i=0;i<g.intestEdge.length;i++) {
                		if(i==0) {
                			path.add(g.intestEdge[i].to);
                		}else {
                			
                		}
                }
            
            */
            }
        });
        bridgeWordButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourceWord=sourceText.getText();
                String destWord=destText.getText();
                String result=queryBridgeWords(g,sourceWord,destWord);
                System.out.println(sourceWord+"->"+destWord+":"+result);
                resultText.setText("Bridge of "+sourceWord+"->"+destWord+":"+result);
            }
        });
        randomShiftButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            		if(ranFlag==false) {
            			RandomWalk R1=new RandomWalk("ran1");
            			R1.start();
            			ranFlag=true;
            		}
            		else {
            			ranFlag=false;
            			g.breakWalking();
            		}
            }
        });
        resetButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            		showDirectedGraph(g);
            }
        });
        geneNewTextButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            	String inputText=sourceText.getText();
            	String result=generateNewText(g,inputText);
            	resultText.setText(result);
            }
        });

        add(ImageLabel);
        add(textFilePathText);
        add(sourceText);
        add(destText);
        add(resultText);
        add(readFileButton);
        add(buildButton);
        add(SPButton);
        add(bridgeWordButton);
        add(randomShiftButton);
        add(resetButton);
        add(geneNewTextButton);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    double max(double x1, double x2) {
        // TODO Auto-generated method stub
        if(x1>x2)return x1;
        return x2;
    }
    public static void main(String[] args) {
        new Main();
    }
}
