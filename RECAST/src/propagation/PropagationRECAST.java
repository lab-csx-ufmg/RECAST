package propagation;

import network.graph.MyGraph;

public class PropagationRECAST {
	
	protected MyGraph g; //event graph
	
	public void processContact(int v1, int v2, long time) {} ;
	public void finalizePropagation() {};
	
	public void setEventGraph(MyGraph g) {
		this.g = g;
	}
	
	

}
