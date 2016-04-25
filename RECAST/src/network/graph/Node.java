package network.graph;

public class Node {
	
	int id;
	int componentLabel;
	
	

	public Node(int id) {
		super();
		this.id = id;
		this.componentLabel = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getComponentLabel() {
		return componentLabel;
	}

	public void setComponentLabel(int componentLabel) {
		this.componentLabel = componentLabel;
	}
	
	

}
