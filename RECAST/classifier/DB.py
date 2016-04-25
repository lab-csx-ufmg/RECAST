'''
Created on Jan 13, 2012

@author: Pedro
'''

import networkx as nx
from os import path

class DB(object):
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.classification_thresh = -1#% chance of being random
        self.num_random_files = 5
        self.window = 24#*7
        self.name = "no_name"
        self.final_time = 0
        self.trace_path = "."
        
    def read_config_File(self):
        f = open("../config.txt", "r")
        hour_size = 1
        ts_size = 1
        for line in f:
            content = (line.rstrip('\n')).split('=')
            if(content[0] == "name"):
                self.name = content[1]
                print "loading config file for trace " + self.name
            elif(content[0] == "num_random_files"):
                self.num_random_files = int(content[1])
            elif(content[0] == "hour_size"):
                hour_size = float(content[1])
            elif(content[0] == "timestep_size"):
                ts_size = int(content[1])
            elif(content[0] == "p_rand"):
                self.classification_thresh = float(content[1])
            elif(content[0] == "final_time"):
                self.final_time = int(content[1])     
            elif(content[0] == "path"):
                self.trace_path = "." + content[1].strip()                              
                
        self.window = int(round(ts_size/hour_size))
        f.close()
        if self.final_time == 0:
            self.load_final_time()
            
    def get_node_table(self):
        filename = self.get_trace_path() + self.name + "_nodetable.dat"
        f = open(filename, "r")
        t = dict()
        for line in f:
            content = line.split()
            t[content[0]] = content[1] #content[0] = real, content[1] = recast id
        f.close()
        return t
    
    def get_reverse_node_table(self):
        filename = self.get_trace_path() + self.name + "_nodetable.dat"
        f = open(filename, "r")
        t = dict()
        for line in f:
            content = line.split()
            t[int(content[1])] = content[0] #content[0] = real, content[1] = recast id
        f.close()
        return t    
        
    def load_final_time(self):
        i = 1
        filename_agr_g = self.get_aggregated_graph_full_name(i)
        while(path.exists(filename_agr_g)):
            i = i+1
            filename_agr_g = self.get_aggregated_graph_full_name(i)
        self.final_time = i-1
        
    def set_classification_thresh(self, ct):
        self.classification_thresh = ct
        
    def set_num_random_files(self, num_rf):
        self.num_random_files = num_rf
        
    def get_classification_threshold(self):
        return self.classification_thresh
    
    def get_trace_path(self):
        return self.trace_path
    
    def get_num_random_files(self):
        return self.num_random_files
    
    def get_final_time(self):
        return self.final_time
    
        
    def get_db_name(self):
        return self.name
    
    def set_db_name(self, new_name):
        self.name = new_name
      
    def get_graphs_path(self):
        path = "../Graphs_Data/" + self.get_db_name() + "/"
        return path
    
    def get_rnd_graphs_path(self):
        path = self.get_graphs_path() + "random/"
        return path
    
    def get_classified_graphs_path(self):
        path = self.get_graphs_path() + "classified/"
        return path   
    
    def get_results_graphs_path(self):
        path = self.get_graphs_path() + "results/"
        return path  
    
    def get_aggregated_graph_name(self, time):
        name = "Aggregated" + str(self.window) + "-" + str(time) + ".graph"
        return name
    
    def get_windowed_graph_name(self, time):
        name = "Windowed" + str(self.window) + "-" + str(time) + ".graph"   
        return name
    
    def get_rnd_graph_name(self, seed, time):
        name = "rnd" + self.get_db_name() + "_seed" + str(seed) + "_threshold" + str(self.classification_thresh) + "_time" + str(time)
        return name
    
    def get_classified_graph_name(self, class_g):
        name = "classified" + self.get_db_name() + "_class" + str(class_g) + "_threshold" + str(self.classification_thresh) + "_time" + str(self.final_time)
        return name    
    
    def get_results_graph_name(self, class_g):
        name = "results" + self.get_db_name() + "_class" + str(class_g) + "_threshold" + str(self.classification_thresh) + "_time" + str(self.final_time) + ".dat"
        return name  
    
    def get_classification_summary_name(self):
        name = self.get_classified_graphs_path() + "Summary" + self.get_db_name() + "_threshold" + str(self.classification_thresh) + "_time" + str(self.final_time)
        return name
    
    def get_rnd_graph_full_name(self, seed, time):
        name = self.get_rnd_graphs_path() + self.get_rnd_graph_name(seed, time)
        return name
 
    def get_aggregated_graph_full_name(self, time):
        name = self.get_graphs_path() + self.get_aggregated_graph_name(time)
        return name
    
    def get_classified_graph_full_name(self, class_g):
        name = self.get_classified_graphs_path() + self.get_classified_graph_name(class_g)
        return name    
    
    def get_results_graph_full_name(self, class_g):
        name = self.get_results_graphs_path() + self.get_results_graph_name(class_g)
        return name    

   
    def read_aggregated_graph(self, time):
        filename = self.get_aggregated_graph_full_name(time)
        print("reading aggregated graph " + filename)
        G=nx.read_edgelist(filename, nodetype = int, data=(('per',float),('weight',float),('topct',float),('to',float),('degreei',float),('degreej',float),('count',int),))
        G.graph['time'] = time
        return G
    
    def read_classified_graph(self, class_g):
        filename = self.get_classified_graph_full_name(class_g)
        G=nx.read_edgelist(filename, nodetype = int, data=(('per',float),('weight',float),('topct',float),('to',float),('degreei',float),('degreej',float),('count',int),('class', int),))
        G.graph['time'] = self.final_time
        return G    

    
 
    