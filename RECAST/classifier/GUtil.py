'''
Created on Jan 10, 2012

@author: Pedro
'''

import networkx as nx

def compute_edge_features(G):
    for n,nbrsdict in G.adjacency_iter():
        for nbr,eattr in nbrsdict.items():
            G[n][nbr]['per'] = G[n][nbr]['weight']/( G.graph['nmerges']+1)
            nbr_n = G.neighbors(n)
            G[n][nbr]['degreei'] = len(nbr_n)
            nbr_nbr = G.neighbors(nbr)
            G[n][nbr]['degreej'] = len(nbr_nbr)
            to = len(set(nbr_n).intersection(set(nbr_nbr)))
            G[n][nbr]['to'] = to
            topct = 0
            denominator = (len(nbr_n)-1 + len(nbr_nbr)-1 - to)
            if(denominator > 0):
                topct = float(to)/denominator
            G[n][nbr]['topct'] = topct
        
    
    return G


def merge_temporal_graphs(G1, G2):
    
    if 'nmerges' in G1.graph:
        G1.graph['nmerges'] += 1
    else:
        G1.graph['nmerges'] = 1
    
    for n,nbrsdict in G2.adjacency_iter():
        for nbr,eattr in nbrsdict.items():
            if 'weight' in eattr:
                #print(n,nbr,eattr['weight'])
                if n < nbr:
                    if G1.has_edge(n,nbr):
                        G1[n][nbr]['weight'] = G1[n][nbr]['weight'] + G2[n][nbr]['weight']
                    else:
                        G1.add_edge(n,nbr,weight=1.0)
    
    return G1

    
def save_random_graph(G,timew, db):

    filename = db.get_rnd_graph_full_name(str(G.graph['random']), str(timew))
    nx.write_edgelist(G,filename,data=['per', 'weight', 'topct', 'to', 'degreei', 'degreej'])

#--------------------------------------------------------                
#def save_random_graph(G):
#    db = DB.DB()
    
#    filename = db.get_rnd_graph_full_name(str(G.graph['random']), str(G.graph['nmerges']))
#    nx.write_edgelist(G,filename,data=['per', 'weight', 'topct', 'to', 'degreei', 'degreej'])
