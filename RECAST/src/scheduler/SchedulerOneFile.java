package scheduler;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import main.DB;


public class SchedulerOneFile extends Scheduler {
	
    FileReader fin;
    Scanner src;	
    
    //Trace files available
    private final Integer BUFFER_SIZE = 50000;

    public SchedulerOneFile(String fileName) throws IOException  {
    	super(fileName);
	    fin = new FileReader(fileName);
	    src = new Scanner(fin);
	    loadContactTrace();
	}

	public void loadContactTrace() throws IOException {
	    String id1, id2;
	    Long tIni, tFin;

	    int count = 1;
	    while (src.hasNext() && count < BUFFER_SIZE) {
	    	count++;
	    	
	    	id1 = src.next();
	    	id2 = src.next(); 
	    		
	    	tIni = src.nextLong();
	    	tFin = src.nextLong();
	    	Double weight = (double)(tFin-tIni)/DB.getHourSize();
	    	
	    	super.addConnection(tIni, id1, id2, weight);
	    	super.addDisconnection(tFin, id1, id2);
	    	
	    	try {
	    		src.nextLine();
	    	} catch(java.util.NoSuchElementException nsee) {
	    		System.out.println("End of file...");
	    	}
	    }
	    	    
	    if(!src.hasNext()) {
	    	//src.close();
	    	fin.close();
	    }

    
	}
	
	public Event getNextEvent() throws Exception {
		if(schedule.size() < BUFFER_SIZE/10 && src.hasNext())
			loadContactTrace();
		return super.getNextEvent();
	}
	

}
