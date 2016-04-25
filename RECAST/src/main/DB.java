package main;

import java.io.File;
import java.io.FileReader;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

import scheduler.Scheduler;
import scheduler.SchedulerOneFile;


public class DB {
	
	public final static int NUM_CLASSES = 4;
	
	public static String DB_FOLDER = ".\\traces\\";
	
	public static String DB_NAME;
		
	public static String DB_PATH;
			
	public static String DB_GRAPHS_PATH;
	
	public static String DB_FEATURES_PATH;
	
	public static int DB_RECORD_INTERVAL;
		
	public static float DB_HOUR_SIZE; 
	
	public static int PROPAGATION_START_TIME;
	
	public static int PROPAGATION_DURATION;
	
	public static double P_RANDOM = 0.01;
	
	public static String getDBName() {
		return DB_NAME;
	}
	
	public static long getPropagationMaxTimeStep() {
		return DB.PROPAGATION_DURATION + DB.PROPAGATION_START_TIME;
	}
	
	public static String getNodeTableFile() { 
		return DB_FOLDER + "\\" + DB.getDBName() + "_nodetable.dat";
	}
	
	public static String getFilePath() {
		return DB_PATH;
	}
	
	public static String getGraphsPath() {
		return DB_GRAPHS_PATH;
	}	
	
	
	public static Scheduler getScheduler() throws Exception {

		return new SchedulerOneFile(DB.getFilePath());
	}
	
	public static int getRecordInterval() {
		return DB_RECORD_INTERVAL;
	}
	
	public static float getHourSize() {
		return DB_HOUR_SIZE;
	}	
	   
	public static void readConfigFile() {
		
		Locale.setDefault(new Locale("en", "US"));
		
	    FileReader fin;
	    Scanner src;	
	    try {
	    	fin = new FileReader("./config.txt");
	    	src = new Scanner(fin);
	    	
	    	Pattern delimiters = Pattern.compile(System.getProperty("line.separator")+"|\\s|=");
	    	src.useDelimiter(delimiters);
	    	
	    	while(src.hasNext()) {
	    		String s1 = src.next();
	    		String s2;
	    		try {
	    			s2 = src.next();
	    		} catch(Exception e) {
	    			s2 = "";
	    		}
	    		
	    		if(s1.equals("path"))
	    			DB.DB_FOLDER = s2;
	    		else if (s1.equals("filename")) 
	    			DB.DB_PATH = DB.DB_FOLDER + s2;
	    		else if(s1.equals("name"))
	    			DB.DB_NAME = s2;
	    		else if(s1.equals("timestep_size"))
	    			DB.DB_RECORD_INTERVAL = Integer.parseInt(s2);
	    		else if(s1.equals("hour_size"))
	    			DB.DB_HOUR_SIZE = Float.parseFloat(s2);
	    		else if(s1.equals("final_time"))
	    			DB.PROPAGATION_START_TIME = Integer.parseInt(s2);	    
	    		else if(s1.equals("propagation_duration"))
	    			DB.PROPAGATION_DURATION = Integer.parseInt(s2);	 	  
	    		else if(s1.equals("p_rand"))
	    			DB.P_RANDOM = Double.parseDouble(s2);	 		    		
	    			
	    	}
	    	
	    	src.close();
	    	fin.close();
	    	
			if(RunConfig.EXECUTE_PROPAGATION)
				DB.DB_PATH = DB.DB_PATH + ".testset-" + DB.PROPAGATION_START_TIME + "_" + DB.PROPAGATION_DURATION;

	    	
	    	File theDir = new File("./Graphs_Data/");
	    	if (!theDir.exists()) {
	    		System.out.println("creating directory: ./Graphs_Data/");
	    		boolean result = theDir.mkdir();  
	    		if(result) {    
	    			System.out.println("DIR created");  
	    		}
	    		else
	    			throw new Exception("directory ./Graphs_Data/ could not be created!");
	    	}
	    	
	    	DB.DB_GRAPHS_PATH = "./Graphs_Data/" + DB.DB_NAME + "/";
	    	
	    	theDir = new File(DB.DB_GRAPHS_PATH);
	    	if (!theDir.exists()) {
	    		System.out.println("creating directory: " + DB.DB_GRAPHS_PATH);
	    		boolean result = theDir.mkdir();  
	    		if(result) {    
	    			System.out.println("DIR created");  
	    		}
	    		else
	    			throw new Exception("directory " + DB.DB_GRAPHS_PATH + "could not be created!");
	    	}
	    	
	    } catch(Exception e) {
	    	System.out.println(e.getMessage());
	    	e.printStackTrace();
	    	System.out.println("Error reading config.txt");
	    }
	}
	
	public static void main(String[] args) {
		DB.readConfigFile();
	}
	 

}
