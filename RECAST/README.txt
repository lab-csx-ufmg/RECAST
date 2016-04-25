---------------------------------------
1) Authorship and Reference
---------------------------------------

RECAST code was developed by Pedro O.S. Vaz de Melo and is licensed according to the MIT license. For more information regarding the MIT license, please see the file LICENSE.txt.

If you publish any work using this code (and/or need extra information about RECAST), please cite (see):

RECAST: Telling Apart Social and Random Relationships in Dynamic Networks. P O S Vaz de Melo, A C Viana, M Fiore, K Jaffrès-Runser, Le F Mouël, A A F Loureiro. 
DOI:10.1145/2507924.2507950 
In proceeding of: Proceedings of the 16th ACM International Conference on Modeling, Analysis and Simulation of Wireless and Mobile Systems (MSWiM'2013)

---------------------------------------
2) Code Summary
---------------------------------------

In short, RECAST reads a trace file containing encounters (or communications, interactions etc) among people and labels their relationships. Smith and Johnson can be labeled as:

Class 1 - Friends: Smith and Johnson see each other regularly and have a high number of common friends, i.e., they are part of the same community.

Class 2 - Bridges: Smith and Johnson see each other regularly but they do not have many common friends, i.e., they are part of different communities.

Class 3 - Acquaintances: Smith and Johnson rarely see each other but they have a high number of common friends, i.e., they are part of the same community.

Class 4 - Random: Smith and Johnson rarely see each other and do not have common friends, i.e., they encounter each other in a random fashion.

---------------------------------------
3) RECAST Input File
---------------------------------------

RECAST input file has the following format:

person_id1 person_id2 encounter_start_time encounter_end_time

Thus, each line of the input file has to contain an information about an encounter between person_id1 and person_id2. Those ids can be both numerical (e.g. 12345) and alphanumerical (e.g. abdce). For trace examples please see the folder .\traces\.

---------------------------------------
4) RECAST Config File
---------------------------------------

Besides the input file, you need to have a config.txt file properly configured. This file contains all the information RECAST needs to know to classify your trace. It has the following fields:

- path: the directory of your trace file. RECAST will need it because it saves a file in this folder contaning a dictionary of nodes, i.e., the node_id from your trace is given a numerical node_id by RECAST, and this file has the whole correspondence.
Example: path=../traces/dartmouth/

- filename: the name of the trace file.
Example: filename=dartmouthsorted.dat.sample

- name: the name YOU want to give to your trace. All the output files will follow this name.
Example: name=dartmouth

- timestep_size: the size of the time step in the scale that time is represented in your trace file. RECAST will divide your trace file into time steps and then see, for instance, the encounter frequency through the time steps.
Example: timestep_size=21600

- hour_size: Considering the scale of the time in your trace file, write here the size of one hour. 
Example: hour_size=3600

- num_random_files: the number of random files you want to use to run recast. 
Example: num_random_files=5

- p_rand [OPTIONAL]: the probability of miss labeling a random relatioship. For more information, please read the paper mentioned in Section 1 of this file. If you do not specify this parameter, RECAST will run for five different values of p_rand: 0.1, 0.01, 0.001, 0.0001 and 0.
Example: p_rand=0.01

---------------------------------------
5) RECAST Dependencies
---------------------------------------

Before running recast, be sure that you have the following softwares installed:

- Python 2. External packages: networkx and scipy.
- Java 1.7 or higher.
- Add all external jars located in folder "external jars"

---------------------------------------
6) Running RECAST
---------------------------------------

The RECAST code is divided into two parts:

A - graph_extractor. Generates graphs from your encounter trace file.

B - classifier. Classify the relationships of your trace file from the graphs generated in part A.

Thus, in order to classify the relationships, first you have to extract the graphs from your trace file (part A). Then, you are able to classify the relationships through the classifier (part B).

This package contains a Makefile file where the command lines to run these two parts are described. If you have make installed, just run "make" to run RECAST's parts A and B. If you want to run only part A, just run "make graphs". If you want to run only part B, run "make classes".

If you do not have "make" installed, run:

For part A:	java -jar .\graph_extractor\graph_extractor.jar
For part B:	python .\classifier\main.py

---------------------------------------
7) RECAST Output Files
---------------------------------------

First, the graph_extractor will create a folder "Graphs_Data" containing all the graphs it generates from your trace. The graphs are the following:

The AggregatedX-T.graph contains all the information aggregated  from time step 1 to time step T. The WindowedX-T.graph contains only the information within the time step T. The DynamicX-T.graph contains only the active encounters seen at the end of time step T. X is the time step size described in the config.txt file.

Second, the classifier will create two folders inside "Graphs_Data": "random" and "classified". The folder "random" contains all the random graphs generated and the folder "classified" contains the graphs with the edges (relationships) classified. There are several files, but the most basic one, with the classes created by RECAST is 

classified[name]_class0_threshold[p_rand]_time[T]

	where [name] and [p_rand] are as described in your config.txt file. [T] is the time step and goes from 1 to the last one processed from your trace file. The edge (relationship) class is given in the final column of each row and the nodes involved are the first two columns. Thus, a row like

0 149 0.04 0.0 0.16 18.0 43.0 87.0 1 4

	means that nodes 0 and 140 share the class 4, where class 1 = friends, 2 = bridges, 3 = acquaintances and 4 = random.

---------------------------------------
8) Have fun! 
---------------------------------------
If you have any questions, commnents or suggestions, please send an e-mail to Pedro O.S. Vaz de Melo at olmo@dcc.ufmg.br.
