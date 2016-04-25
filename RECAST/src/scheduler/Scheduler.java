package scheduler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.PriorityQueue;

import main.DB;
import main.RunConfig;


public class Scheduler implements SchedulerInterface {
	
	PriorityQueue<Event> schedule;
	HashMap<Long,Event> events;
    NodeTable nodeTable;
    
    PrintWriter outTest = null;
    
    
    
    public Scheduler(String fileName) throws IOException  {
		schedule = new PriorityQueue<Event>();
	    events = new HashMap<Long,Event>();
	    nodeTable = new NodeTable();
	    
	    if(RunConfig.EXECUTE_PROPAGATION) {
	    	File testFile = new File(fileName);
	    	if(!testFile.exists()) {
	    		System.out.println("Test file does not exist! I will create it for you...\nThis may take several minutes.");
	    		RecordTestSet recTS = new RecordTestSet(DB.getFilePath());
	    		recTS.recordTestSet();
	    	}
	    }
    
	}
    
    public void clearSchedule() {
    	this.schedule.clear();
    }
    
    protected void addConnection(long time, String id1, String id2, double duration) {
    	
   	
    	int hashid1 = nodeTable.getNode(id1);
    	int hashid2 = nodeTable.getNode(id2);
    	this.addConnection(time, hashid1, hashid2, duration);
    	
    }
    
    protected void addConnection(long time, int id1, int id2, double duration) {
    	
    	Event e = events.get(time);
    	
    	if(e == null) {
    		e = new Event(time);
    		events.put(time, e);
    		schedule.add(e);
    	}
    	
    	e.addConnection(id1, id2, duration, time);    	
    	
    }
    
    protected void addDisconnection(long time, String id1, String id2) {
    	int hashid1 = nodeTable.getNode(id1);
    	int hashid2 = nodeTable.getNode(id2);
    	this.addDisconnection(time, hashid1, hashid2);
    }
    
    protected void addDisconnection(long time, int id1, int id2) {
    	Event e = events.get(time);
    	
    	if(e == null) {
    		e = new Event(time);
    		events.put(time, e);
    		schedule.add(e);
    	}
    	
    	e.addDisconnection(id1, id2);
    }


	
	public Event getNextEvent() throws Exception {
		Event e = schedule.poll();
		events.remove(e.time);
		return e;
	}
	
	public boolean hasNextEvent() {
		if (!schedule.isEmpty())
			return true;
		nodeTable.recordNodeTable();
		return false;
	}
	
	public void stop() {
		this.schedule.clear();
	}
	
	
	


}
