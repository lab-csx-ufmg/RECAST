package network.graph;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import main.DB;
import network.functions.IndividualFunctions;

public class MyGraph {
	
	HashMap<Integer,EdgeList> edges = new HashMap<Integer,EdgeList>();
	
	HashSet<Edge> edgeSet = new HashSet<Edge>();
	
	HashMap<Integer,Node> nodes = new HashMap<Integer,Node>();
	
	String name;
	
		
	private int maxId = 0;
	private int numEdges = 0;
	private int time;
	
	UndirectedSparseGraph<Integer,Edge> gJung = new UndirectedSparseGraph<Integer,Edge>();
	BetweennessCentrality<Integer, Edge> bRanker;
	
	public MyGraph(String name) {
	
		this.name = name;
		bRanker = new BetweennessCentrality<Integer, Edge>(gJung);
		bRanker.setNormalizeRankings(true);
		bRanker.setRemoveRankScoresOnFinalize(false);
	

	}
	
	
	
	public String getName() {
		return name;
	}



	public UndirectedSparseGraph<Integer,Edge> getGraphJung() {
		return gJung;
	}
	
	public void clear() {
		this.nodes.clear();
		this.edges.clear();
		this.edgeSet.clear();
		this.gJung = new UndirectedSparseGraph<Integer,Edge>();
		this.numEdges = 0;
	}

	
	
	public void removeDeadEdges(long deltaTime) {
		
		Iterator<Edge> iEdges = this.edgeSet.iterator();
		while(iEdges.hasNext()) {
			Edge e = iEdges.next();
			if(this.getTime() - e.timeWillDie > deltaTime) {
				this.removeEdge(e.getNodeSrc(), e.getNodeDest());
				this.removeEdge(e.getNodeDest(), e.getNodeSrc());
			}
		}
	}

	
	
	public int getNumEdges() {
		return numEdges;
	}

	public void setNumEdges(int numEdges) {
		this.numEdges = numEdges;
	}

	public int getMaxId() {
		return maxId;
	}
	
	

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	
	public boolean addNode(int nodeid) {
		if(!edges.containsKey(nodeid)) {
			edges.put(nodeid, new EdgeList(nodeid));
			Node node = new Node(nodeid);
			nodes.put(nodeid, node);
			
			if(nodeid > this.maxId)
				this.maxId = nodeid;
			
			gJung.addVertex(nodeid);
			
			return true;
		}
		return false;
		
	}
	
	public boolean isEmpty() {
		return this.nodes.size() == 0;
	}
	
	public int getNumNodes() {
		return this.nodes.size();
	}
	
	public Node getNode (int id) {
		return nodes.get(id);
	}
	
	public boolean removeNode(int node) {
		if(edges.containsKey(node)) {
			edges.remove(node);
			gJung.removeVertex(node);
			return true;
		}
		return false;		
	}
	
	public boolean addEdge(int node1, int node2) {
		boolean newEdge = this.addEdge(node1, new Edge(node1, node2, 1.0, (long)0));
		this.addEdge(node2, new Edge(node2, node1, 1.0, (long)0));
		return newEdge;
				
	}
	
	public void addEdge(int node1, int node2, double weight) {
		this.addEdge(node1, new Edge(node1, node2, weight, (long)0));
		this.addEdge(node2, new Edge(node2, node1, weight, (long)0));
				
	}

	public void addEdge(int node1, int node2, double weight, long timeWillDie) {
		this.addEdge(node1, new Edge(node1, node2, weight, timeWillDie));
		this.addEdge(node2, new Edge(node2, node1, weight, timeWillDie));
				
	}
	
	public boolean addEdge(int node1, Edge e) { //false if the edge already existed
		
		
		if(!edges.containsKey(node1)) {
			this.addNode(node1);
		}
		if(!edges.containsKey(e.getNodeDest())) {
			this.addNode(e.getNodeDest());
		}		
		

		if (!edges.get(node1).add(e)) { //if edge is old
			return false;
		}
		
		
		gJung.addEdge(e, node1, e.getNodeDest());
		edgeSet.add(e);
		
		this.numEdges++;
		return true;
		
	}	
	
	public void removeEdge(int node1, int node2) {
		if(this.edges.containsKey(node1)) {
			Edge e = this.edges.get(node1).remove(node2);
			this.numEdges--;
			
			gJung.removeEdge(e);
			edgeSet.remove(e);
			
		}
		if(this.edges.containsKey(node2)) {
			Edge e = this.edges.get(node2).remove(node1);
			this.numEdges--;
			
			gJung.removeEdge(e);
			edgeSet.remove(e);
		}
		
	}
	
	public Edge removeRandomEdge() {
		Edge[] edgeArray = edgeSet.toArray(new Edge[0]);
		Random random = new Random();
		int r = random.nextInt(edgeArray.length);
		Edge e = edgeArray[r];
		this.removeEdge(e.nodeSrc, e.nodeDest);
		return e;
	}
		
	public Integer[] getNodes() {
		return this.edges.keySet().toArray(new Integer[0]);
	}
	
	public HashSet<Integer> getNodeSet() {
		return new HashSet<Integer>(this.edges.keySet());
	}
	
	public Edge[] getEdges(int nodeId) {
		if(!this.edges.containsKey(nodeId))
			return null;
		return this.edges.get(nodeId).toArray();
	}
	
	public Integer[] getNeighs(int nodeId) {
		return this.edges.get(nodeId).keySet().toArray(new Integer[0]);
	}	
	
	public Set<Integer> getNeighsSet(int nodeId) {
		
		
		if(this.edges.containsKey(nodeId))
			return this.edges.get(nodeId).keySet();
		return new HashSet<Integer>();
	}		
	
	
	
	public HashSet<Edge> getEdgeSet() {
		return edgeSet;
	}
	

	public int getDegree(int nodeId) {
		if(this.edges.containsKey(nodeId))
			return this.edges.get(nodeId).size();
		return 0;
	}
	
	public boolean existsEdge(int node1, int node2) {
		if(edges.containsKey(node1) && edges.containsKey(node2))
			return this.edges.get(node1).containsKey(node2);
		return false;
	}
	
	public int aggregateGraph(MyGraph g) {
		Integer newNodes[] = g.getNodes();
		int numChanges = 0;
		for(int i=0; i<newNodes.length; i++) {
			if(!this.edges.containsKey(newNodes[i])) {
				this.addNode(newNodes[i]);
			}
			Edge newEdges[] = g.getEdges(newNodes[i]);
			for(int j=0; j<newEdges.length; j++) {
				numChanges += this.addEdge(newNodes[i], newEdges[j]) == true ? 1 : 0;
			}
		}
		return numChanges;
	}
	
	
	public String toString() {
		
		return this.nodes.size() + "," + this.numEdges;
	}
	
	public void loadGraphFromFile(String fileName) throws Exception {
	    FileReader fin = new FileReader(fileName);
	    Scanner src = new Scanner(fin);
	    
	    while(src.hasNext()) {
	    	int v1 = src.nextInt();
	    	int v2 = src.nextInt();
	    	this.addEdge(v1, v2);
	    	src.nextLine();
	    }
	    
	    src.close();
	    fin.close();

	}
	
	public void saveGraphToFile(String filename) throws Exception {
		
		if(!this.isEmpty()) {
		
			File file = new File(DB.getGraphsPath() + filename);
			FileWriter writer = new FileWriter(file);
			PrintWriter out = new PrintWriter(writer);  
	
			Iterator<Integer> inodes = this.nodes.keySet().iterator();
			while(inodes.hasNext()) {
				int nodeid = inodes.next();
				ArrayList<Edge> elist = edges.get(nodeid).getEdgesL();
				for(int j=0; j<elist.size(); j++) {
					if(nodeid<elist.get(j).getNodeDest())
						out.println(nodeid + " " + elist.get(j).getNodeDest() + " " + elist.get(j).getWeight());
				}
			}
			
			out.close();
			writer.close();
		}
		
		
	}
	
	
	
	public void saveEdgesFeaturesToFile(String filename) throws Exception {
		
		if(!this.isEmpty()) {
			

			File file = new File(DB.getGraphsPath() + filename);
			FileWriter writer = new FileWriter(file);
			PrintWriter out = new PrintWriter(writer);  
		 
			Iterator<Integer> inodes = this.nodes.keySet().iterator();
			while(inodes.hasNext()) {
				int nodeid = inodes.next();
				ArrayList<Edge> elist = edges.get(nodeid).getEdgesL();
				for(int j=0; j<elist.size(); j++) {
					if(nodeid<elist.get(j).getNodeDest()) {
						Edge edge = elist.get(j);
						
						DecimalFormat df = new DecimalFormat("0.00");
						
						double totalTS = Math.ceil((double)this.getTime())-1; //-1 because it goes here after the first event of the next timestep!
						double edgePersistency = Math.min(edge.getNumTimeSteps()/totalTS, 1.0);
						int to = IndividualFunctions.getTopologicalOverlap(this, edge);
						int degreeSrc = this.getDegree(edge.getNodeSrc());
						int degreeDest = this.getDegree(edge.getNodeDest());

						try {
							float topct =  degreeSrc-1 + degreeDest-1 - to;
							if(topct > 0)
								topct = to/topct;
							out.println(nodeid + " " + edge.getNodeDest() + " " + df.format(edgePersistency) + " " + df.format(edge.getWeight()) + " " +
									df.format(topct) + " " + to + " " + degreeSrc + " " + degreeDest+ " " + edge.getCount());
						} catch(ArithmeticException e) { //division by 0: degrees are 1
							out.println(nodeid + " " + edge.getNodeDest() + " " + df.format(edgePersistency) + " " + df.format(edge.getWeight()) + " " +
									0 + " " + to + " " + degreeSrc + " " + degreeDest + " " + edge.getCount());

						}
					}
				}
			}
			
			out.close();
			writer.close();		
		}
		

	}
	

}
