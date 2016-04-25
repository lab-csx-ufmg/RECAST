package network.functions;


import java.util.HashSet;
import java.util.Set;

import network.graph.Edge;
import network.graph.MyGraph;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class IndividualFunctions {
	
	public static double clusteringCoefficient(MyGraph g, int node) {
		double cc=0;
		Integer edges[] = g.getNeighs(node);
		int n = edges.length;
		
		if(n == 0)
			return -1;
		
		for(int i=0; i<n; i++) {
			for(int j=i+1; j<n; j++) {
				if(g.existsEdge(edges[i], edges[j]))
					cc++;
			}
		}
		cc = 2*cc/(n*(n-1));
		return cc;
	}
	
	public static DescriptiveStatistics getFullyDisconnectedDegrees(MyGraph g) {
		
		DescriptiveStatistics stat = new DescriptiveStatistics();
		
		Integer nodes[] = g.getNodes();
		for(int i=0; i<nodes.length; i++) {
			double cc = clusteringCoefficient(g, nodes[i]);
			if(cc == 0 && g.getDegree(nodes[i]) > 0) {
				stat.addValue(g.getDegree(nodes[i]));
			}
		}
		
		return stat;
		
	}
	
	public static double getTopologicalOverlapPct(MyGraph g, int v1, int v2) {
		
		Set<Integer> edges1 = g.getNeighsSet(v1);
		int degreeSrc = edges1.size();
		Set<Integer> edges2 = g.getNeighsSet(v2);
		int degreeDest = edges2.size();
		
		int edgeExists = 0;
		if(g.existsEdge(v1, v2))
			edgeExists = 1;
		
		HashSet<Integer> edgesTO = new HashSet<Integer>(edges1);
		edgesTO.retainAll(edges2);
		
		int to = edgesTO.size();
		
		double toPct = ((double)to/( (degreeSrc-edgeExists) + (degreeDest-edgeExists) - to));
		
		return toPct;
	}	
	
	public static int getTopologicalOverlap(MyGraph g, Edge e) {
		
		Set<Integer> edges1 = g.getNeighsSet(e.getNodeSrc());
		Set<Integer> edges2 = g.getNeighsSet(e.getNodeDest());
		
		
		HashSet<Integer> edgesTO = new HashSet<Integer>(edges1);
		edgesTO.retainAll(edges2);
		return edgesTO.size();
	}
	
	

}
