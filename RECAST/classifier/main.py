from __future__ import division
'''
Created on Jan 9, 2012

@author: Pedro
'''
   
import RandomGGen as rg
import DB
import RandomGraphCmp
from Classifier import classify_edges


def gen_random_graphs(db):
    for i in range(0,db.num_random_files):
        rg.gen_random_graphs(i, db)
        
 
    
def runRecast(db):
    G = db.read_aggregated_graph(db.get_final_time())
    print("reading random graphs...")
    rgc = RandomGraphCmp.RandomGraphComparator()
    rgc.read_rnd_network_files(db)    
    
    prnds = [db.get_classification_threshold()]
    if(prnds[0] == -1):
        prnds = [0.1, 0.01, 0.001, 0.0001, 0]
    
    for p in prnds:
        db.set_classification_thresh(p)
        classify_edges(G, db, rgc)       


#MAIN

db = DB.DB()

db.read_config_File()

gen_random_graphs(db)

runRecast(db)

    








