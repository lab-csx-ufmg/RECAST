package scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;


public class Event implements Comparable<Event> {
	
	
	Long time;
	PriorityQueue<NodeEvents> vertexList = new PriorityQueue<NodeEvents>();
	HashMap<Integer,NodeEvents> vertices = new HashMap<Integer,NodeEvents>(); 
	ArrayList<Integer> nodesCompleted = new ArrayList<Integer>();
	
	public Event(Long t) {
		this.time = t;
	}

	public Long getTime() {
		return time;
	}
	
	public void addToCompletedList(int nodeid) {
		nodesCompleted.add(nodeid);
	}
	
	public PriorityQueue<NodeEvents> updateListOrder() {
		PriorityQueue<NodeEvents> vaux = this.vertexList;
		this.vertexList = new PriorityQueue<NodeEvents>();
		while(!vaux.isEmpty()) 
			this.vertexList.add(vaux.remove());
		
		return this.vertexList;
	}
	
	
	
	public ArrayList<Integer> getNodesCompleted() {
		return nodesCompleted;
	}

	public NodeEvents getNodeEvents(int id) {
		if(!vertices.containsKey(id)) {
			NodeEvents v = new NodeEvents(id);
			vertices.put(id,v);
			vertexList.add(v);
			return v;
		}
		return vertices.get(id);
		
	}
	
	public void addConnection(int id1, int id2, Double weight, Long time) {
		
		NodeEvents v1 = getNodeEvents(id1);
		NodeEvents v2 = getNodeEvents(id2);
		
		v1.cList.add(id2, weight, (long)(time + weight));
		v2.cList.add(id1, weight,(long)(time + weight));
		
	}
	
	public void addDisconnection(int id1, int id2) {
		NodeEvents v1 = getNodeEvents(id1);
		NodeEvents v2 = getNodeEvents(id2);
		
		v1.dList.add(id2, 0.0, (long)0);
		v2.dList.add(id1, 0.0, (long)0);
		
	}
	
	
	public PriorityQueue<NodeEvents> getVertexList() {
		return vertexList;
	}
	
	public boolean hasNext() {
		return !this.vertexList.isEmpty();
	}
	
	public NodeEvents getNext() {
		return this.vertexList.remove();
	}

	public int compareTo(Event e) {
		if(this.getTime() < e.getTime())
			return -1;
		return 1;

	}
	

	
	public String toString() {
		return "" + this.time + "," + this.getVertexList();
	}
	
	
	
	

}
