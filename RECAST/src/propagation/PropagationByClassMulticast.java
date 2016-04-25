package propagation;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import main.DB;
import main.RunConfig;

import org.apache.commons.collections15.keyvalue.MultiKey;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import network.graph.Edge;
import network.graph.MyGraph;

public class PropagationByClassMulticast extends PropagationRECAST {
	
	public final static int NO_CLASS = 0;
	
	HashSet<Integer> infected = new HashSet<Integer>();
	HashSet<Integer> infectedPerClass[] = new HashSet[DB.NUM_CLASSES+1];
	HashSet<Integer> nodesAround = new HashSet<Integer>();
	

	MyGraph G = new MyGraph("propG"); //cumulative graph
	HashMap<Integer,Double> delays = new HashMap<Integer,Double>();
	DescriptiveStatistics dsDelays = new DescriptiveStatistics();
	int countClassesTestSet[] = new int[DB.NUM_CLASSES+2];
	//class 0 = non-classified edges
	//class n+1 = non-classified edges that would be classified as 3
	
	HashMap<Integer,Integer> infectionPerHour[] = new HashMap[DB.NUM_CLASSES+1];
	HashMap<Integer,Integer> msgsPerHour[] = new HashMap[DB.NUM_CLASSES+1];
	HashMap<Integer,Double> tputPerHour[] = new HashMap[DB.NUM_CLASSES+1];
	
	int countNewEdgesHighTO = 0;
	int countNewEdgesLowTO = 0;
	HashSet<MultiKey<Integer>> newEdgesHighTO = new HashSet<MultiKey<Integer>>();
	
	
	HashMap<Integer,Integer> paths = new HashMap<Integer,Integer>(); //marks the predecessor
	
	int firstNode = 0;
	long timeToInfect = 1;
	int totalEncounters = 0, totalTransmissions = 0;


	long time, timeFirstInfection, timeLastInfection, TO;
	
	EdgesClassReader eclasses;
	
	int maxnode = -1;
	
	public PropagationByClassMulticast(int v0) {
		
		this.eclasses = new EdgesClassReader();
		
		for(int i=0; i<DB.NUM_CLASSES+1; i++) {
			this.countClassesTestSet[i] = 0;
			infectedPerClass[i] = new HashSet<Integer>();
			infectionPerHour[i] = new HashMap<Integer,Integer>();
			msgsPerHour[i] = new HashMap<Integer,Integer>();
			tputPerHour[i] = new HashMap<Integer,Double>();
		}
		
		infectFirstNode(v0);
		

	}
	
	
	public void finalizePropagation() {
		this.printDelays();
	}
	

	public void infectFirstNode(int v) {
		markInfected(v, -1);
	}
	
	private int[][] countEdgeClassesInPath() throws Exception {
		
		//save file with the path lengths
		FileWriter writer[] = new FileWriter[5];
		PrintWriter out[] = new PrintWriter[5];

		
		for(int eclass=0; eclass<=4; eclass++) {
			String fileName = DB.getGraphsPath() + "propagation/pathLenghts" + DB.getDBName() + "_class" + eclass + "_threshold" + DB.P_RANDOM + ".dat";
			File file = new File(fileName);
			writer[eclass] = new FileWriter(file, true);
			out[eclass] = new PrintWriter(writer[eclass]);
		}
		
		Iterator<Integer> nodesIt = paths.keySet().iterator();
		int countClassesHops[][] = new int[5][5];
		while(nodesIt.hasNext()) {
			int node = nodesIt.next();
			//System.out.println("Counting EdgeClassesInPath for node " + node);
			int nodeSrc = paths.get(node);
			int eclassMain = this.eclasses.getEdgeClass(this.firstNode, node);
			int pathLength = 0;
			while(nodeSrc != -1) {
				int eclassHop = this.eclasses.getEdgeClass(node, nodeSrc);
				countClassesHops[eclassMain][eclassHop]++;
				node = nodeSrc;
				nodeSrc = paths.get(nodeSrc);
				pathLength++;
			}
			if(eclassMain > 0)
				out[eclassMain].println(pathLength);
		}
		for(int eclass=0; eclass<=4; eclass++) {
			writer[eclass].close();
			out[eclass].close();
		}
		return countClassesHops;
	}
	
	
	private void printDelays() {
		try {
			
			//result per hour
			int countClassesHops[][] = countEdgeClassesInPath();
			String fileName2 = DB.getGraphsPath() + "propagation/MulticastPropagationClassesUsed" + DB.getDBName() + "_threshold" + DB.P_RANDOM + "_firstNode" + this.firstNode + "_start" + this.timeFirstInfection +  ".dat";
			File file2 = new File(fileName2);
			FileWriter writer2 = new FileWriter(file2);
			PrintWriter out2 = new PrintWriter(writer2);
			
			for(int eclass=1; eclass<=DB.NUM_CLASSES; eclass++) {
				String fileName = DB.getGraphsPath() + "propagation/MulticastPropagationPerHour" + DB.getDBName() + "_eclass" + eclass + "_threshold" + DB.P_RANDOM + "_firstNode" + this.firstNode + "_start" + this.timeFirstInfection +  ".dat";
				File file = new File(fileName);
				FileWriter writer = new FileWriter(file);
				PrintWriter out = new PrintWriter(writer);

				
				Iterator<Integer> hoursIt = infectionPerHour[eclass].keySet().iterator();
				int hour = 0;
				double numEdgesPerClass = (double)this.eclasses.getNumEdgesPerClassPerNode(firstNode, eclass);
				while(hoursIt.hasNext()) {
					hour = hoursIt.next();
					
					out.println(hour + " " + infectionPerHour[eclass].get(hour)/(numEdgesPerClass)+ " " + this.msgsPerHour[eclass].get(hour) + " " + this.tputPerHour[eclass].get(hour) + " " + numEdgesPerClass);
				}
				
				out.close();
				writer.close();
			
				double totalHops = countClassesHops[eclass][1] + countClassesHops[eclass][2] + countClassesHops[eclass][3] + countClassesHops[eclass][4] + countClassesHops[eclass][0];
				out2.println(infectedPerClass[eclass].size() + " " + numEdgesPerClass + " " + 
						countClassesHops[eclass][1] + " " + countClassesHops[eclass][2] + " " + countClassesHops[eclass][3] + " " + countClassesHops[eclass][4] + " " + countClassesHops[eclass][0] +
						" " + countClassesHops[eclass][1]/totalHops + " " + countClassesHops[eclass][2]/totalHops + " " + countClassesHops[eclass][3]/totalHops + " " + countClassesHops[eclass][4]/totalHops + " " + countClassesHops[eclass][0]/totalHops);
				
			}
			
			out2.close();
			writer2.close();
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
		
	
	

	//infect all neighbors
	private void infectNeighbors(int v) {
		Edge edges[] = g.getEdges(v);
		if(edges != null) {
			this.time = this.time + this.timeToInfect;
			for(int i=0; i<edges.length; i++) {
				int vDest = edges[i].getNodeDest();
				if(!this.infected.contains(vDest) && this.allowInfection(vDest,v,(long)time)) {
					markInfected(vDest,v);
				}
			}
			for(int i=0; i<edges.length; i++) {
				int vDest = edges[i].getNodeDest();
				propagateInfection(vDest);
			}
		}
	}
	
	//propagate the infection through neighbors when allowed
	private void propagateInfection(int v) {
		Edge edges[] = g.getEdges(v);
		if(edges != null) {
			this.time = this.time + this.timeToInfect;
			for(int i=0; i<edges.length; i++) {
				if(this.allowInfection(edges[i].getNodeDest(),v, (long)time) && !this.infected.contains(edges[i].getNodeDest()))
					this.transmitInfection(edges[i].getNodeDest(),v);
			}
		}
	}	
	


	private void markInfected(int v, int vsource) {
		if(this.infected.isEmpty())
			this.firstNode = v;			
		this.infected.add(v);
		
		paths.put(v, vsource);
		
		if(vsource >= 0) {
			
			if(this.infected.size() == 2)
				this.timeFirstInfection = this.time;
			
			double timeInfection = this.time - this.timeFirstInfection;
			delays.put(v,timeInfection);
			dsDelays.addValue(timeInfection);
			int eclass = this.eclasses.getEdgeClass(this.firstNode, v);
			infectedPerClass[eclass].add(v);
			
			
			int numHours = (int)Math.floor((time - this.timeFirstInfection)/DB.DB_HOUR_SIZE);
			
			//if(!this.infectionPerHour[eclass].containsKey(numHours)) {
				this.infectionPerHour[eclass].put(numHours, this.infectedPerClass[eclass].size());
				this.msgsPerHour[eclass].put(numHours, this.totalTransmissions);
				this.tputPerHour[eclass].put(numHours, this.totalTransmissions/(double)(this.infectedPerClass[eclass].size()));
			//}				
		}
			
	}
	
	
	public void transmitInfection(int v, int vsource) {
			
		totalTransmissions++;
		markInfected(v,vsource);
		this.infectNeighbors(vsource);		
		this.propagateInfection(v);

	}		
	
	
	private boolean allowInfection(int v1, int v2, long time) {
		
		int eclass = this.eclasses.getEdgeClass(v1, v2);
		
		//if (v1,v2) has no class and none is infected or both are infected
		if( eclass == 0 || ((!infected.contains(v1) && !infected.contains(v2)) || (infected.contains(v1) && infected.contains(v2))) )
			return false;
		
		if(time >= this.timeFirstInfection && time <= this.timeLastInfection) {
			
			this.totalEncounters++;
			this.nodesAround.add(v1);
			this.nodesAround.add(v2);			
			this.countClassesTestSet[eclass]++;	
			
			return true;
			
		}
			
		return false;		

	}
	
	public void processContact(int v1, int v2, long time) {
	
		this.time = time;
		
		if(this.totalEncounters == 0) {
			//select a random time to start the infection
			this.TO = time;
			int duration = DB.PROPAGATION_DURATION * DB.DB_RECORD_INTERVAL; 
			
			if(RunConfig.START_TIME_INFECTION <= 0) {
				Random random = new Random();
				this.timeFirstInfection = this.TO + random.nextInt(24*3600);
				RunConfig.START_TIME_INFECTION = this.timeFirstInfection;
			}
			else
				this.timeFirstInfection = RunConfig.START_TIME_INFECTION;
			
			this.timeLastInfection = this.timeFirstInfection + duration;
			
		}			


		if(allowInfection(v1, v2, time)) {
			if(infected.contains(v1)) {
				transmitInfection(v2,v1);
			}
			else if(infected.contains(v2)) {
				transmitInfection(v1,v2);
			}
		}
		
	}
	
	

}
