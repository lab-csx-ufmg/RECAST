package propagation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.HashSet;

import main.DB;

import org.apache.commons.collections15.keyvalue.MultiKey;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import scheduler.SchedulerTestSet;

public class EdgesClassReader {
	
	
	String fileName = DB.getGraphsPath() + "classified\\classified" + DB.getDBName() + "_class0_threshold" + DB.P_RANDOM + "_time" + DB.PROPAGATION_START_TIME; //RunConfig.TIME_TEST_SET;
	HashMap<MultiKey<Integer>, Integer> edgeClasses = new HashMap<MultiKey<Integer>, Integer>();
	int countClassesTestSet[] = new int[DB.NUM_CLASSES+1];
	
	int countClassesPerNode[][] = new int[6000][DB.NUM_CLASSES+1];

	HashSet<Integer> nodes = new HashSet<Integer>();
	
	DescriptiveStatistics dsTO = new DescriptiveStatistics();
	DescriptiveStatistics dsPer = new DescriptiveStatistics();
	
	HashSet<Integer> activeNodesTestSet = new HashSet<Integer>();
	
	

	public EdgesClassReader() {
		this.readEdgesClasses();
	}
	
	public Integer[] getNodes() {
		return nodes.toArray(new Integer[0]);
	}
	
	public Integer[] getActiveNodesTestSet() {
		return activeNodesTestSet.toArray(new Integer[0]);
	}	
	
	public int getTotalPossibleEdges() {
		int n = nodes.size();
		return (n*(n-1))/2;
	}
	
	public int getNumClassifiedEdges() {
		return this.edgeClasses.size();
	}
	
	public int getNumEdgesPerClass(int classEdge) {
		if(classEdge == 0) {
			int sum = 0;
			for (int i=1; i<DB.NUM_CLASSES+1; i++)
				sum = sum + this.countClassesTestSet[i];
			return getTotalPossibleEdges() - sum;

		}
			
		return this.countClassesTestSet[classEdge];
	}
	
	public int getNumEdgesPerClassPerNode(int node, int classEdge) {
		if(classEdge > 0 || (classEdge == 0 && this.countClassesPerNode[node][classEdge] > 0) )
			return this.countClassesPerNode[node][classEdge];
		int sum = 0;
		for(int i=1; i<DB.NUM_CLASSES+1; i++) {
			sum = sum + this.countClassesPerNode[node][i];
		}
		int n = nodes.size();
		int class0count = (n*(n-1))/2 - sum;
		this.countClassesPerNode[node][classEdge] = class0count;
		return class0count;
	}
	
	public double getMinTONonRandomValue() {
		return dsTO.getMin();
	}
	
	public double getMinPerNonRandomValue() {
		return dsPer.getMin();
	}
	
	public double getExpectedEncountersByClass(int classEdge) {
		//expected % of encounters per class considering random encounter model
		return (double)getNumEdgesPerClass(classEdge)/getTotalPossibleEdges();
	}
	
	
	
	public int getEdgeClass(int v1, int v2) {
		Integer eclass = this.edgeClasses.get(new MultiKey<Integer>(v1, v2));
		if(eclass == null)
			return 0;
		return eclass;
	}
	
	private HashSet<Integer> readActiveNodesTestSet() throws Exception{
		SchedulerTestSet sts = new SchedulerTestSet(DB.getFilePath());
		return sts.getActiveNodes();
	}
	
	public void readEdgesClasses() {
		try {
			//consider only active nodes in the test set
			activeNodesTestSet = readActiveNodesTestSet();

			File file = new File(fileName);

            FileReader reader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(reader);
            StreamTokenizer st = new StreamTokenizer(bReader);   

            //configura o StreamTokenizer
            st.resetSyntax();
            st.whitespaceChars(0,' ');
            st.wordChars(' '+1,255);
           
            //manda o StreamTokenizer ler a primeira palavra do arquivo
            st.nextToken();
            //pega a linha atual do arquivo

            while(st.ttype!=StreamTokenizer.TT_EOF){
            	
                int v1 = Integer.parseInt(st.sval);
                st.nextToken();
                int v2 = Integer.parseInt(st.sval);                
                st.nextToken();
                
                if(activeNodesTestSet.contains(v1) && activeNodesTestSet.contains(v2)) {
                
	                nodes.add(v1);
	                nodes.add(v2);
	                double persistence= Double.parseDouble(st.sval);
	                st.nextToken();
	                double totalDuration = Double.parseDouble(st.sval);               
	                st.nextToken();
	                double to = Double.parseDouble(st.sval);
	                st.nextToken();
	                double toCount = Double.parseDouble(st.sval);                
	                st.nextToken();                
	                double degreeSrc = Double.parseDouble(st.sval);                
	                st.nextToken();
	                double degreeDest = Double.parseDouble(st.sval);                
	                st.nextToken();
	                int countEncounters = Integer.parseInt(st.sval);                
	                st.nextToken();                
	                int classEdge = Integer.parseInt(st.sval);                
	                st.nextToken();  
	                this.countClassesTestSet[classEdge]++;
	             
	                countClassesPerNode[v1][classEdge]++;
	                countClassesPerNode[v2][classEdge]++;
	                
	                if(v1 < v2) {
	                	this.edgeClasses.put(new MultiKey<Integer>(v1, v2), classEdge);
	                	this.edgeClasses.put(new MultiKey<Integer>(v2, v1), classEdge);
	               }
	                
	                if(classEdge == 1 || classEdge == 3) {
	                	dsTO.addValue(to);
	                }
	                if(classEdge == 1 || classEdge == 2) {
	                	dsPer.addValue(persistence);
	                }
                } //fim IF TEST_SET
                else {
                	//jumps to the next line
                	for(int i=1; i<=8; i++)
                		st.nextToken();
                }
            }
            
		} catch(Exception e) {
			
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	
	}
	

}
