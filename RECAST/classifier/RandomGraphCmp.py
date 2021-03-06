'''
Created on Jan 13, 2012

@author: Pedro Olmo
Modified by Michele A. Brandao
'''

import networkx as nx
from scipy import stats
from MathUtil import myround
from datetime import datetime
from multiprocessing import Pool
import pandas as pd
import numpy as np



class RandomGraphComparator(object):
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.per = []
        self.perCache = {}
        self.topct = []
        self.topctCache = {}

    
      
    def get_per_rnd_pct(self, per_value):
        per_value = myround(per_value)
        if(self.perCache.has_key(per_value)):
            p = self.perCache.get(per_value)
        else:
            p = stats.percentileofscore(self.per, per_value)
            self.perCache[per_value] = p
        return 100-p
    
    def get_topct_rnd_pct(self, topct_value):
        topct_value = myround(topct_value)
        if(self.topctCache.has_key(topct_value)):
            p = self.topctCache.get(topct_value)
        else:
            p = stats.percentileofscore(self.per, topct_value)
            self.topctCache[topct_value] = p
        return 100-p    
  
    def read_rnd_network_files(self, db):
        
        #Multiprocessing
        pool = Pool()

        time = db.get_final_time()
        num_files = db.get_num_random_files()
        pathD = db.get_rnd_graphs_path()
        
        self.perCache = {}
        self.topctCache = {}
        
        filename = pathD + db.get_rnd_graph_name(1, time)
        
        #Using pandas to improve memory use for large graphs
        df = pd.read_csv(filename,header=None,delim_whitespace=True,names=['node1','node2','per','weight','topct','to','degreei','degreej'],dtype={'node1': np.int, 'node2': np.int,'per':np.float,'weight':np.float,'topct':np.float,'to':np.int,'degreei':np.int,'degreej':np.int},low_memory=True, buffer_lines=None, memory_map=False, float_precision=None)
        
        
        #G=nx.read_edgelist(filename, nodetype = int, data=(('per',float),('weight',float),('topct',float),('to',float),('degreei',float),('degreej',float),))

        
        G = nx.Graph()
        #G.add_weighted_edges_from([tuple(x,y,z,w,t,r) for x,y,z,w,t,r in df.values])
        G = nx.from_pandas_dataframe(df, 'node1', 'node2', ['per','weight','topct','to','degreei','degreej'])
        print G.edges


        self.topct = nx.get_edge_attributes(G,'topct').values()
        self.per = nx.get_edge_attributes(G,'per').values()

        for seed in range(0,num_files):
            print("reading file " + str(seed))
            start_time = datetime.now()
            filename = pathD + db.get_rnd_graph_name(seed, time)
            end_time = datetime.now()
            print('Duration reading file: {0}'.format(end_time - start_time))

            
            #Using pandas to improve memory use for large graphs
            df = pd.read_csv(filename,header=None,delim_whitespace=True,names=['node1','node2','per','weight','topct','to','degreei','degreej'],dtype={'node1': np.int, 'node2': np.int,'per':np.float,'weight':np.float,'topct':np.float,'to':np.int,'degreei':np.int,'degreej':np.int},low_memory=True, buffer_lines=None, memory_map=False, float_precision=None)
            
            
            print df
            
            #G=nx.read_edgelist(filename, nodetype = int, data=(('per',float),('weight',float),('topct',float),('to',float),('degreei',float),('degreej',float),))
            
            G = nx.Graph()
            #G.add_weighted_edges_from([tuple(x,y,z,w,t,r) for x,y,z,w,t,r in df.values])
            G = nx.from_pandas_dataframe(df, 'node1', 'node2', ['per','weight','topct','to','degreei','degreej'])
            print G.edges
            

            print ">> Armazenou em G"


            start_time = datetime.now()
            pool.apply_async(self.topct.extend(nx.get_edge_attributes(G,'topct').values()))
            #self.topct.extend(nx.get_edge_attributes(G,'topct').values())
            pool.apply_async(self.per.extend(nx.get_edge_attributes(G,'per').values()))
            #self.per.extend(nx.get_edge_attributes(G,'per').values())


            end_time = datetime.now()
            print('Duration extend: {0}'.format(end_time - start_time))
            
        pool.close()
        pool.join()
            
        