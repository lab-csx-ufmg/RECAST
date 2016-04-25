'''
Created on Jan 14, 2012

@author: Pedro
'''

import networkx as nx
import random
from Classifier import load_classified_graph
from os import path, makedirs
import DB
import numpy

def saveRecastGraphYelp():
    
    db = DB.DB()
    db.read_config_File()
    
    name = db.get_db_name() + "-" +  str(db.get_classification_threshold()) + "-" + str(db.get_final_time())
    name1 = name + "-recast-all.gml" #all social edges from recast
    G1 = nx.Graph()
    
    name2 = name + "-recastonly-all.gml" #all social edges which appeared only in recast
    G2 = nx.Graph()
    
    name3 = name + "-recastonly-friends.gml" #friends which appeared only in recast
    G3 = nx.Graph()
    
    f = open(db.get_trace_path() + "graph.txt", "r")
    
    GS = nx.Graph()    
    #load nodetable
    nodetab = db.get_node_table()    
    #self reported social network
    for line in f:
        content = line.split()
        kv1 = content[0].strip()
        kv2 = content[1].strip()
        if kv1 in nodetab and kv2 in nodetab:
            v1 = int(nodetab[kv1])
            v2 = int(nodetab[kv2])
            GS.add_edge(v1,v2) 
            
                   
    G = load_classified_graph(db)
    rnodetable = db.get_reverse_node_table()
    print "comparing edges..." + str(G.number_of_edges())
        
    count = 0
    for (i,j) in G.edges():
        
        print count
        count = count + 1
        v1 = rnodetable[i]
        v2 = rnodetable[j]
        c = G[i][j]['class'];
        if c < 4:
            G1.add_edge(v1, v2)
        if (i,j) not in GS.edges() and c < 4:
            G2.add_edge(v1,v2)
        if (i,j) not in GS.edges() and c == 1:
            G3.add_edge(v1,v2)
            
    nx.write_gml(G1, name1)
    nx.write_gml(G2, name2)
    nx.write_gml(G3, name3)
            
    f.close()    


def openSRSNclassified_file(db):
    bk_name = db.get_db_name()
    db.set_db_name(db.get_db_name()+"_srsn")
    f = open(db.get_classified_graph_full_name(0), 'w')
    db.set_db_name(bk_name)
    return f

def cmpYelp(db):
    
    print "starting cmpYelp"
    
    M = numpy.zeros(shape=(2,4))
    
    G = load_classified_graph(db)
    
    f = open(db.get_trace_path() + "graph.txt", "r")
    
    GS = nx.Graph()
    
    #load nodetable
    nodetab = db.get_node_table()    
    
    print "checkin sfsn..."
    
    #self reported social network
    for line in f:
        content = line.split()
        kv1 = content[0].strip()
        kv2 = content[1].strip()
        if kv1 in nodetab and kv2 in nodetab:
            v1 = int(nodetab[kv1])
            v2 = int(nodetab[kv2])
            GS.add_edge(v1,v2)    
            
    print "comparing edges..." + str(G.number_of_edges())
        
    count = 0
    for (i,j) in G.edges():
        print count
        count = count + 1
        c = G[i][j]['class'];
        if (i,j) in GS.edges():
            M[1][c-1] = M[1][c-1] + 1
        else:
            M[0][c-1] = M[0][c-1] + 1
        
    f.close()

    print M
    return M        
    
def cmpSassy(db):
    
    M = numpy.zeros(shape=(2,4))
    
    G = load_classified_graph(db)
    
    f = open(db.get_trace_path() + "srsn.csv", "r")
    fc = openSRSNclassified_file(db)

    GS = nx.Graph()
    
    #load nodetable
    nodetab = db.get_node_table()
    
    #self reported social network
    for line in f:
        content = line.split(",")
        v1 = int(nodetab[content[0].strip()])
        v2 = int(nodetab[content[1].strip()])
        GS.add_edge(v1,v2)
        fc.write(str(v1) + " " + str(v2) + " 0.0 0.0 0.0 0.0 0.0 0.0 0 1 \n") #social
        
    for (i,j) in G.edges():
        c = G[i][j]['class'];
        if (i,j) in GS.edges():
            M[1][c-1] = M[1][c-1] + 1
        else:
            M[0][c-1] = M[0][c-1] + 1
            fc.write(str(i) + " " + str(j) + " 0.0 0.0 0.0 0.0 0.0 0.0 0 4 \n") #random
        
    f.close()
    fc.close()
    print M
    return M
    

def cmpUPB(db): 
    
    M = numpy.zeros(shape=(2,4))
    
    G = load_classified_graph(db)
    
    f = open(db.get_trace_path() + "social.dat", "r")
    
    fc = openSRSNclassified_file(db)

    GS = nx.Graph()
    
    #load nodetable
    nodetab = db.get_node_table()
    
    #self reported social network
    v1 = 1
    for line in f:
        content = line.split(",")
        for v2 in range(0,len(content)):
            if(int(content[v2]) == 1) and str(v1) in nodetab and str(v2+1) in nodetab:
                GS.add_edge(int(nodetab[str(v1)]),int(nodetab[str(v2+1)]))
                fc.write(str(v1) + " " + str(v2) + " 0.0 0.0 0.0 0.0 0.0 0.0 0 1 \n") #social
                
        v1 = v1 + 1
        
    for (i,j) in G.edges():
        c = G[i][j]['class'];
        if (i,j) in GS.edges():
            M[1][c-1] = M[1][c-1] + 1
        else:
            M[0][c-1] = M[0][c-1] + 1
            fc.write(str(i) + " " + str(j) + " 0.0 0.0 0.0 0.0 0.0 0.0 0 4 \n") #random
            
        
    f.close()
    fc.close()
    print M
    return M
 
def cmpWithFriends():
    db = DB.DB()
    db.read_config_File()
    
    if db.get_db_name() == 'upb':
        cmpUPB(db)
    elif db.get_db_name() == 'sassy':
        cmpSassy(db)
    elif db.get_db_name() == 'yelp':
        cmpYelp(db)        
        
        
cmpWithFriends()
#saveRecastGraphYelp()
    