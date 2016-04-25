'''
Created on Jan 13, 2012

@author: Pedro
'''

import networkx as nx
from scipy import stats
from MathUtil import myround

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

        time = db.get_final_time()
        num_files = db.get_num_random_files()
        pathD = db.get_rnd_graphs_path()
        
        self.perCache = {}
        self.topctCache = {}
        
        filename = pathD + db.get_rnd_graph_name(0, time)
        G=nx.read_edgelist(filename, nodetype = int, data=(('per',float),('weight',float),('topct',float),('to',float),('degreei',float),('degreej',float),))
        self.topct = nx.get_edge_attributes(G,'topct').values()
        self.per = nx.get_edge_attributes(G,'per').values()
        
        for seed in range(0,num_files):
            print("reading file " + str(seed))
            filename = pathD + db.get_rnd_graph_name(seed, time)
            G=nx.read_edgelist(filename, nodetype = int, data=(('per',float),('weight',float),('topct',float),('to',float),('degreei',float),('degreej',float),))
            self.topct.extend(nx.get_edge_attributes(G,'topct').values())
            self.per.extend(nx.get_edge_attributes(G,'per').values())
            
        