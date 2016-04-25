package scheduler;

import network.graph.EdgeList;



public class NodeEvents implements Comparable<NodeEvents>{
	
	public int id;
	public EdgeList cList; //connection list
	public EdgeList dList; //disconnection list
	public boolean highPriority = false;
	
	public NodeEvents(int id) {
		this.id = id;
		this.cList = new EdgeList(id);
		this.dList = new EdgeList(id);
	}
	
	public int getDegree(){
		return cList.size() + dList.size();
	}
	

	public boolean isHighPriority() {
		return highPriority;
	}

	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

	public int compareTo(NodeEvents o) {
		if(this.highPriority)
			return -1;
		if(o.highPriority)
			return 1;
		return (o.getDegree() - this.getDegree());
	}
	
	public String toString() {
		return "(" + this.id + "," + this.getDegree() + ")"; 
	}
	
	public int getNumberOfChanges() {
		
		return this.cList.size() + this.dList.size();
		
	}
	
	
	

}
