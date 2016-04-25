package main;

import propagation.EdgesClassReader;
import propagation.PropagationByClassMulticast;


 

public class RunScripts {
	
	public static void runGenerateGraphs() throws Exception {
		
		DB.readConfigFile();
		
		RunConfig.LOAD_EVENT_G = true;
		RunConfig.LOAD_WINDOWED_G = true;
		RunConfig.LOAD_FULL_G = true;
		RunConfig.SAVE_GRAPH_FILE = true;
		
		Run net = new Run(DB.getScheduler());
		net.loadEncounters();
	}
	
	public static void runMulticastPropagation() throws Exception {
		
		RunConfig.EXECUTE_PROPAGATION = true;
		RunConfig.LOAD_EVENT_G = true;
		
		DB.readConfigFile();

		EdgesClassReader ecreader = new EdgesClassReader();
		Integer nodes[] = ecreader.getActiveNodesTestSet();
		Run net = null;
		

		for(int i=0; i<nodes.length; i++) { 
			int node = nodes[i];
			System.out.println("multicast from node: " + node + " i: " + i);
			net = new Run(DB.getScheduler(), new PropagationByClassMulticast(node));
			RunConfig.START_TIME_INFECTION = -1;
			net.loadEncounters();
		}		
	}	
	
		
}
