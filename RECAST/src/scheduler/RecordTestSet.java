package scheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import main.DB;
import main.RunConfig;
import network.graph.Edge;

public class RecordTestSet {
	
	PrintWriter out = null;

	
	public RecordTestSet(String fileName) {
		File file = new File(fileName);
		try {
			FileWriter writer = new FileWriter(file);
			out = new PrintWriter(writer);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void recordTestSet() {
		try {
			RunConfig.EXECUTE_PROPAGATION = false;
			DB.readConfigFile();
			Scheduler s = DB.getScheduler();
			int count = 0;
			long T0 = 0;
			while(s.hasNextEvent()) {
				Event e = s.getNextEvent();
				count ++;
				if(count == 1) {
					T0 = e.getTime();
				}				
				int timestep = (int)Math.round((e.getTime()-T0)/DB.getRecordInterval())+1;
				
				if(timestep > DB.PROPAGATION_START_TIME && timestep <= DB.getPropagationMaxTimeStep()) {
					while(e.hasNext()) {
						NodeEvents ne = e.getNext();
						ArrayList<Edge> clist = ne.cList.getEdgesL();
						for(int i=0;i<clist.size(); i++) {
							Edge edge = clist.get(i);
							int v1 = edge.getNodeSrc();
							int v2 = edge.getNodeDest();
							long tFin =  e.getTime() + Math.round(edge.getWeight()*DB.DB_HOUR_SIZE);
							out.println(s.nodeTable.getNodeString(v1) + " " + s.nodeTable.getNodeString(v2) + " " + e.getTime() + " " + tFin);
						}
					}
				}
			}
			RunConfig.EXECUTE_PROPAGATION = true;
			DB.readConfigFile();
			out.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}

}
