package main;


import java.util.ArrayList;

import propagation.PropagationByClassMulticast;
import propagation.PropagationRECAST;
import network.graph.Edge;
import network.graph.MyGraph;
import scheduler.Event;
import scheduler.NodeEvents;
import scheduler.Scheduler;


public class Run {
	
	MyGraph g = new MyGraph("Dynamic"); //time evolving graph
	MyGraph G = new MyGraph("Aggregated"); //aggregated graph
	MyGraph Gt = new MyGraph("Windowed"); //aggregated graph per time step
	
		
	String name;
	
	public int AGI_NUM_HOURS;
	
	private long saveInfoTime = 0;
	
	long T0 = 0;
	private int timestep;
	private int lastTimestepRecorded=0;
	
	Integer auxInt=0;
	long numChanges=0;
	long numChangesRand=0;
	
	long numEncounters = 0;
	
	Scheduler scheduler;
	
	PropagationRECAST propagation = null;
	

	
	public Run(Scheduler s) throws Exception {
		
		this.AGI_NUM_HOURS = (int)Math.ceil((double)(DB.getRecordInterval()) / DB.getHourSize());

		this.name = DB.getDBName();
        this.scheduler = s;
         
	}
	
	public Run(Scheduler s, PropagationRECAST p) throws Exception {
		this.propagation = p;
		this.propagation.setEventGraph(g);
		this.AGI_NUM_HOURS = (int)Math.ceil((double)(DB.getRecordInterval()) / DB.getHourSize());
		this.name = DB.getDBName();
        this.scheduler = s;		

	}
	
	
	
	public void takeSnapshot(long time, int timestep) throws Exception {
		timestep = timestep - 1;
		if(RunConfig.SAVE_GRAPH_FILE) { 
			g.saveGraphToFile(g.getName() + AGI_NUM_HOURS + "-" + timestep + ".graph");
			//saving all aggregated graphs that I did not recorded because of sparseness  
			if(!Gt.isEmpty()) {
				for(int ts = this.lastTimestepRecorded+1; ts<=timestep; ts++ ) 
					G.saveEdgesFeaturesToFile(G.getName() + AGI_NUM_HOURS + "-" + ts + ".graph");
				Gt.saveGraphToFile(Gt.getName() + AGI_NUM_HOURS + "-" + timestep + ".graph");
				this.lastTimestepRecorded = timestep;
			}
			
		}

}


	
	
	
	private void updateGraph(long time) throws Exception {

		if( (time - this.saveInfoTime) >= DB.getRecordInterval()) {
			
			Gt.clear();

			this.saveInfoTime = time;
		}
	}

	
	
	
	
	public void saveInfo(long time) throws Exception {

		G.setTime(timestep);
		Gt.setTime(timestep);
		g.setTime(timestep);
		
		if( (time - this.saveInfoTime) >= DB.getRecordInterval()) {
			
			System.out.println("Timestep: " + timestep);
			takeSnapshot(time,timestep);
			updateGraph(time);

		}		
	}
	
	public void loadEvent(Event e) throws Exception{

		MyGraph gEvents = new MyGraph("gEvents");
		while(e.hasNext()) { 
			NodeEvents ne = e.getNext();
			
			if(RunConfig.LOAD_EVENT_G) {
				ArrayList<Edge> dlist = ne.dList.getEdgesL();
				for(int i=0;i<dlist.size(); i++)
					g.removeEdge(ne.id, dlist.get(i).getNodeDest());
			}
			
			ArrayList<Edge> clist = ne.cList.getEdgesL();
			for(int i=0;i<clist.size(); i++) {
				this.numEncounters++;
				
				Edge edge = clist.get(i);
				
				if(RunConfig.LOAD_EVENT_G)
					g.addEdge(ne.id, clist.get(i));
				if(RunConfig.LOAD_WINDOWED_G)
					Gt.addEdge(ne.id, clist.get(i));
				if(RunConfig.LOAD_FULL_G)
					this.numChanges += G.addEdge(ne.id, edge) == true ? 1 : 0;
				if(RunConfig.EXECUTE_PROPAGATION)
					propagation.processContact(edge.getNodeSrc(), edge.getNodeDest(), e.getTime());				
					
			}
			
		}
		
		gEvents.clear();
		
	}	
	
	
	
	public void loadEncounters() {
		try {
			int count = 0;
			while(this.scheduler.hasNextEvent()) {
				count++;
				Event e = this.scheduler.getNextEvent();
				
				if(count == 1) {
					this.T0 = e.getTime();
				}
				
				this.timestep = (int)Math.round((e.getTime()-this.T0)/DB.getRecordInterval())+1;
				
				this.loadEvent(e);
				this.saveInfo(e.getTime());

			}
			
			if(RunConfig.EXECUTE_PROPAGATION)
				propagation.finalizePropagation();

			System.out.println("# of encounters: " + this.numEncounters/2);
			System.out.println("Avg # of encounters per day: " + this.numEncounters/(2.0*this.timestep));
			
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
		}	
	}

	
	public static void main(String[] args) {
		
		try {
			
			System.out.println("starting...");
			
			RunScripts.runGenerateGraphs();
			//RunScripts.runMulticastPropagation();
			
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}


	}

}
