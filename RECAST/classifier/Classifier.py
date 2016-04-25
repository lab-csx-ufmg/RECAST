'''
Created on Jan 14, 2012

@author: Pedro
'''

import networkx as nx
import random
from os import path, makedirs

def load_classified_graph(db):
    
    filename = db.get_classified_graph_full_name(0)
    G = nx.read_edgelist(filename,nodetype=int,data=(('per',float), ('weight', float), ('topct', float), ('to', float), ('degreei', float), ('degreej', float), ('count', int), ('class', int)))
    return G



def save_classified_graph(G, db):
    
    filename = db.get_classified_graph_full_name(0)
    nx.write_edgelist(G,filename,data=['per', 'weight', 'topct', 'to', 'degreei', 'degreej', 'count', 'class'])
    
    SG=nx.Graph( [ (u,v,d) for u,v,d in G.edges(data=True) if random.random() > 0.66 and d['class']<=4] ) 
    nx.write_edgelist(SG,filename + 'samprreal',data=['per', 'weight', 'topct', 'to', 'degreei', 'degreej', 'count', 'class'])


    for i in range(1,5):
        print("saving graph of class" + str(i))
        filename = db.get_classified_graph_full_name(i)
        SG=nx.Graph( [ (u,v,d) for u,v,d in G.edges(data=True) if d['class']==i] )
        nx.write_edgelist(SG,filename,data=['per', 'weight', 'topct', 'to', 'degreei', 'degreej', 'count', 'class'])

def classify_edges(G, db, rgc):
    
    threshold = db.get_classification_threshold()
    print("classifying edges...")
    count=0;
    count1=0;
    count2=0;
    count3=0;
    count4=0;
    for (i,j) in G.edges():
        
        if ((count % 100) == 0):
            print(count)
            print(str(count1) + " - " + str(count2) + " - " + str(count3) + " - " + str(count4))
        count += 1
        
        edata = G.get_edge_data(i,j)
        perp = rgc.get_per_rnd_pct(edata['per'])
        top = rgc.get_topct_rnd_pct(edata['topct'])
        

        
        if(perp <= threshold and top <= threshold):
            G[i][j]['class'] = 1;
            count1+=1 
        elif(perp <= threshold and top > threshold):
            G[i][j]['class'] = 2;
            count2+=1 
        elif(perp > threshold and top <= threshold):
            G[i][j]['class'] = 3;
            count3+=1 
        else:
            G[i][j]['class'] = 4;
            count4+=1 
        
    #saving classification summary    
    print(str(count1) + " - " + str(count2) + " - " + str(count3) + " - " + str(count4))
    
    #checking if directory does not exists
    directory = db.get_classified_graphs_path()
    if not path.exists(directory):
        makedirs(directory)    
    
    
    filename = db.get_classification_summary_name()
    f = open(filename, 'w') 
    total_edges = float(count1 + count2 + count3 + count4)
    f.write("1 " + str(count1) + " " + str(count1/total_edges) + "\n" + "2 " + str(count2) + " " + str(count2/total_edges) + "\n" + "3 " + str(count3) + " " + str(count3/total_edges) + "\n" + "4 " + str(count4) + " " + str(count4/total_edges))
    
    save_classified_graph(G, db)
    


