package network.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class EdgeList extends HashMap<Integer, Edge> {

	private static final long serialVersionUID = 1L;

	int nodeId;
	
	public EdgeList(int nodeId) {
		this.nodeId = nodeId;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public Set<Integer> getNodeSet() {
		return this.keySet();
	}

	public ArrayList<Edge> getEdgesL() {
		ArrayList<Edge> edgesL = new ArrayList<Edge>(this.values());
		return edgesL;
	}
	
	public Edge[] toArray() {
		return this.values().toArray(new Edge[0]);
	}
	
	public void add(int nodeid) {
		this.add(new Edge(this.nodeId, nodeid,1.0));
	}
	
	public boolean add(int nodeid, Double weight, Long timeWillDie) {
		Edge e = new Edge(this.nodeId,nodeid,weight,timeWillDie);
		return this.add(e);
	}
	
	public boolean add(Edge e) {//false if the edge already existed
		if(!this.containsKey(e.getNodeDest())) {
			this.put(e.getNodeDest(), e);
			return true;
		}
		this.get(e.getNodeDest()).incrementEdge(e);		
		return false;
	}	
	
	

}
