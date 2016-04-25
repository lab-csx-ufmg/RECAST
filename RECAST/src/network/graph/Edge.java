package network.graph;

import main.DB;

public class Edge {
	
	Integer nodeDest, nodeSrc;
	Double weight;
	int count;
	Long timeWillDie;
	

	int lastTimeStepSeen = -1;
	int numTimeSteps = 0;
	
	public Edge(Integer nodeSrc, Integer nodeDest, Double weight) {
		super();
		this.nodeSrc = nodeSrc;
		this.nodeDest = nodeDest;
		this.weight = weight;
		this.count = 1;
	}
	
	public Edge(Integer nodeSrc, Integer nodeDest, Double weight, Long timeToDie) {
		super();
		this.nodeSrc = nodeSrc;
		this.nodeDest = nodeDest;
		this.weight = weight;
		this.timeWillDie = timeToDie;
		this.count = 1;

		
		this.updateTimeStep(timeToDie, weight);
	}
	

	private void updateTimeStep(Long timeWillDie, double weight) {
		


		double duration = weight * DB.getHourSize();
		
		int t1 = (int)Math.ceil((timeWillDie-duration)/DB.getRecordInterval());
		int t2 = (int)Math.ceil((double)timeWillDie/DB.getRecordInterval());
		
		
		this.numTimeSteps+= t2 - Math.max(t1, this.lastTimeStepSeen+1) + 1;
		
		this.lastTimeStepSeen = t2;
	}
	
	public int getNumTimeSteps() {
		return this.numTimeSteps;
	}
	
	
	
	public Integer getNodeSrc() {
		return nodeSrc;
	}

	public void setNodeSrc(Integer nodeSrc) {
		this.nodeSrc = nodeSrc;
	}

	public Integer getNodeDest() {
		return nodeDest;
	}
	public void setNodeDest(Integer node) {
		this.nodeDest = node;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	public int getCount() {
		return count;
	}

	public void incrementEdge(Edge e) {
		if(e.getTimeWillDie() != this.timeWillDie) {
			this.weight += e.getWeight();
			this.count += e.getCount();
			this.timeWillDie = e.getTimeWillDie();
			
			this.updateTimeStep(e.getTimeWillDie(), e.getWeight());
		}
	}

	public Long getTimeWillDie() {
		return timeWillDie;
	}

	public void setTimeWillDie(Long timeToDie) {
		this.timeWillDie = timeToDie;
	}
	
	public String toString() {
		return "("+ this.nodeSrc + "," + this.nodeDest + ")";
	}
	
	
	

}
