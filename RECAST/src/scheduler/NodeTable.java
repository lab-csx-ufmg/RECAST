package scheduler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import main.DB;

public class NodeTable {
	
	HashMap<String,Integer> table = new HashMap<String,Integer>();
	HashMap<Integer,String> reverseTable = new HashMap<Integer,String>();
	Integer nodeCount = 0;
	
	public NodeTable() {
		File file = new File(DB.getNodeTableFile());
		if(file.exists()) {
			try {
				FileReader fin = new FileReader(file);
				Scanner src = new Scanner(fin);
				while(src.hasNext()) {
					String idnode = src.next();
					int hashedid = src.nextInt();
					this.table.put(idnode, hashedid);
					this.reverseTable.put(hashedid, idnode);
				}
				src.close();
				fin.close();
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void populateNodeTable(int vi, int vf) {
		for(int i=vi; i<=vf; i++) {
			this.table.put(String.valueOf(i), i);
			this.reverseTable.put(i, String.valueOf(i));
			nodeCount++;
		}
	}
	
	public Integer getNode(String nodeCode) {
		if(table.containsKey(nodeCode))
			return table.get(nodeCode);
		table.put(nodeCode, nodeCount);
		reverseTable.put(nodeCount, nodeCode);
		nodeCount++;
		
		return nodeCount-1;
	}
	
	//original name
	public String getNodeString(int node) {
		return reverseTable.get(node);
	}
	
	public void recordNodeTable() {
		File file = new File(DB.getNodeTableFile());
		if(!file.exists()) {
			try {
				FileWriter writer = new FileWriter(file);
				PrintWriter out = new PrintWriter(writer);
				Iterator<String> inodeids = table.keySet().iterator();
				while(inodeids.hasNext()) {
					String id = inodeids.next();
					out.println(id + " " + this.table.get(id));
				}
				out.close();
				writer.close();
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
				
	}

}
