'''
Created on Jan 10, 2012

@author: Pedro
'''

import math
from operator import itemgetter
import random
from GUtil import save_random_graph, merge_temporal_graphs, compute_edge_features
import networkx as nx
from os import path, makedirs

def gen_random_graphs(seed, db):
    
    print "generating random graph with seed " + str(seed)
    directory = db.get_rnd_graphs_path()
    if not path.exists(directory):
        makedirs(directory)
    
    filename = db.get_rnd_graph_full_name(str(seed), str(db.get_final_time()))
    if(path.exists(filename)):
        print "random graph with seed " + str(seed) + " already exists! Skipping..."
        return

    
    pathD = db.get_graphs_path()
    filename = pathD + db.get_windowed_graph_name(0)
    G=nx.read_edgelist(filename, nodetype = int, data=(('weight',float),))
    GR = get_random_graph_from(G, seed)
    save_random_graph(GR,1, db)
    
    for i in range(2,db.get_final_time()+1):
        filename = pathD + db.get_windowed_graph_name(str(i))
        if(not path.exists(filename)):
            f = open(filename,'w')
            f.close()
            
        G=nx.read_edgelist(filename, nodetype = int, data=(('weight',float),))
        GRnew = get_random_graph_from(G, seed)
        GR.graph['nmerges'] = i-2
        GR = merge_temporal_graphs(GR, GRnew)
        GR = compute_edge_features(GR)
        save_random_graph(GR,i, db)
    
        print("G_RND[" + str(i)  + "] has " + str(GR.number_of_edges()) + " edges")

            


def get_random_graph_from(G, seed):
    d = list(G.degree().values())
    GR = my_expected_degree_graph(d, seed)
    GR.graph['random'] = seed
    return GR

def my_expected_degree_graph(w, seed=None, selfloops=False):
    r"""Return a random graph with given expected degrees.

    IMPORTANT!!!
    THIS IS A COPY OF THE EXISTING METHOD, BUT CREATES EDGES WITH WEIGHT=1.0
    
    Given a sequence of expected degrees `W=(w_0,w_1,\ldots,w_{n-1}`)
    of length `n` this algorithm assigns an edge between node `u` and
    node `v` with probability

    .. math::

       p_{uv} = \frac{w_u w_v}{\sum_k w_k} .

    Parameters
    ----------
    w : list 
        The list of expected degrees.
    selfloops: bool (default=True)
        Set to False to remove the possibility of self-loop edges.
    seed : hashable object, optional
        The seed for the random number generator.

    Returns
    -------
    Graph

    Examples
    --------
    >>> z=[10 for i in range(100)]
    >>> G=nx.expected_degree_graph(z)

    Notes
    -----
    The nodes have integer labels corresponding to index of expected degrees
    input sequence.

    The complexity of this algorithm is `\mathcal{O}(n+m)` where `n` is the
    number of nodes and `m` is the expected number of edges.

    The model in [1]_ includes the possibility of self-loop edges.
    Set selfloops=False to produce a graph without self loops.
    
    For finite graphs this model doesn't produce exactly the given 
    expected degree sequence.  Instead the expected degrees are as
    follows.

    For the case without self loops (selfloops=False),

    .. math::

       E[deg(u)] = \sum_{v \ne u} p_{uv} 
                = w_u \left( 1 - \frac{w_u}{\sum_k w_k} \right) .


    NetworkX uses the standard convention that a self-loop edge counts 2 
    in the degree of a node, so with self loops (selfloops=True),

    .. math::

       E[deg(u)] =  \sum_{v \ne u} p_{uv}  + 2 p_{uu} 
                = w_u \left( 1 + \frac{w_u}{\sum_k w_k} \right) .

    References
    ----------
    .. [1] Fan Chung and L. Lu, Connected components in random graphs with  
       given expected degree sequences, Ann. Combinatorics, 6, 
       pp. 125-145, 2002.
    .. [2] Joel Miller and Aric Hagberg, 
       Efficient generation of networks with given expected degrees,
       in Algorithms and Models for the Web-Graph (WAW 2011), 
       pp. 115-126, 2011.
    """
    n = len(w)
    G=nx.empty_graph(n)
    if n==0 or max(w)==0: # done if no edges
        return G 
    if seed is not None:
        random.seed(seed)
    rho = 1/float(sum(w))
    # sort weights, largest first
    # preserve order of weights for integer node label mapping
    order = sorted(enumerate(w),key=itemgetter(1),reverse=True)
    mapping = dict((c,uv[0]) for c,uv in enumerate(order))
    seq = [v for u,v in order]
    last=n
    if not selfloops:
        last-=1
    for u in range(last):
        v = u 
        if not selfloops:
            v += 1
        factor = seq[u] * rho
        p = seq[v]*factor  
        if p>1:
            p = 1
        while v<n and p>0:
            if p != 1:
                r = random.random()
                v += int(math.floor(math.log(r)/math.log(1-p)))
            if v < n:
                q = seq[v]*factor
                if q>1:
                    q = 1
                if random.random() < q/p: 
                    G.add_edge(mapping[u],mapping[v], weight=1.0)
                v += 1
                p = q 
    return G