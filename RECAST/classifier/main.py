from __future__ import division
'''
Created on Jan 9, 2012

@author: Pedro Olmo
Modified by Michele A. Brandao
'''
   
import RandomGGen as rg
import DB
import RandomGraphCmp
from Classifier import classify_edges
from datetime import datetime
import itertools
from multiprocessing import Pool, freeze_support


def func_star(a_b):
    """Convert `f([1,2])` to `f(1,2)` call."""
    return rg.gen_random_graphs(*a_b)

def gen_random_graphs(db):
    pool = Pool(2)
    
    i=range(0,db.num_random_files)
    
    a_args = i
    second_arg = db
    pool.map(func_star, itertools.izip(a_args, itertools.repeat(second_arg)))
    
    pool.close()
    pool.join()
    
    '''for i in range(0,db.num_random_files):
        rg.gen_random_graphs(i, db)'''
        
    
 
    
def runRecast(db):
    G = db.read_aggregated_graph(db.get_final_time())
    print("reading random graphs...")
    rgc = RandomGraphCmp.RandomGraphComparator()
    #rgc.read_rnd_network_files(db) 
    
    #Measuring the time
    start_time = datetime.now()
    rgc.read_rnd_network_files(db)
    end_time = datetime.now()
    print('Duration: {0}'.format(end_time - start_time))
    
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

    








