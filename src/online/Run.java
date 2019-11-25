package online;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class Run {

	//*********************SORT FUNCTION*********************
	public static LinkedHashMap<Integer, Double> sortHashMapByValues(
	        HashMap<Integer, Double> passedMap) {
	    List<Integer> mapKeys = new ArrayList<>(passedMap.keySet());
	    List<Double> mapValues = new ArrayList<>(passedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);

	    LinkedHashMap<Integer, Double> sortedMap =
	        new LinkedHashMap<>();

	    Iterator<Double> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	    	Double val = valueIt.next();
	        Iterator<Integer> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            Integer key = keyIt.next();
	            Double comp1 = passedMap.get(key);
	            Double comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
	//*******************************************************
	
	
	public static void main(String[] args) throws IOException{
		
		//************INIT VARIABLES*************
		int nblivres = 50;
		double[][] tab_dist = new double[nblivres][nblivres];
		//***************************************
		
		//*****************FILES*****************
		ArrayList<File> files = new ArrayList<File>();
		for(int i = 0;i<nblivres;i++) {
			File f = new File("src/online/livres/"+i+".txt");
			files.add(f);
		}
		//***************************************
		
		//*************COMMON WORDS**************
		ArrayList<String> words_filter = new ArrayList<>();
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/online/100.txt"));
			while((line = br.readLine()) != null) words_filter.add(line);
			br.close();
		}catch(FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
		//***************************************
		
		
		//*****************NODES*****************
		ArrayList<Node> nodes = new ArrayList<Node>();
		for(File f : files) nodes.add(new Node(f,words_filter));
		//***************************************
		
		//************DIST/NEIGHBOURS************
		for(Node n : nodes) {
			ArrayList<Node> nodes_without_node = new ArrayList<Node>(nodes);
			nodes_without_node.remove(n.myident);
			n.generateNeighbours(nodes_without_node);
			n.generateDist(nodes);
			for(int i = 0;i<nodes.size();i++) {
				if(n.myident == i) {
					tab_dist[i][n.myident] = Double.POSITIVE_INFINITY;
				}else {
					tab_dist[i][n.myident] = n.dist.get(i);
				}
			}
			System.out.println("Document "+n.myident+" : "+n.neighbours.size()+ " voisins.");
		}
		//***************************************

		//************GENERATE GRAPHE************
		Graphe g = new Graphe(nodes, tab_dist);
		//***************************************
		
		
		//***************AFFICHAGE***************
		System.out.println("**********************************************\n"
						 + "*************MATRICE JACCARD REELLE***********\n"
						 + "**********************************************");
		for (int j = 0; j < tab_dist.length; j++) {
			System.out.print(j+" |" );
			for (int j2 = 0; j2 < tab_dist.length; j2++) System.out.print(String.format("%.2f", tab_dist[j][j2])+" ");
			System.out.println();
		}
		System.out.println("**********************************************\n"
						 + "************MATRICE JACCARD CALCULEE**********\n"
						 + "**********************************************");
		g.printmat_dist();
		System.out.println("**********************************************\n"
						 + "****************MATRICE CHEMIN****************\n"
						 + "**********************************************");
		g.printmat_path();
		//***************************************
		
		//***************BETWEENESS/CLOSENESS**************
		//init
		HashMap<Integer,Double> betweeness = new HashMap<>();
		HashMap<Integer,Double> closeness = new HashMap<>();
		for(int i = 0;i<nblivres;i++) {
			betweeness.put(i,g.getBetweenness(i));
			closeness.put(i,g.getCloseness(i));
		}
		
		//sort
		LinkedHashMap<Integer,Double> betweeness_sorted = sortHashMapByValues(betweeness);
		LinkedHashMap<Integer,Double> closeness_sorted = sortHashMapByValues(closeness);
		
		//affichage
		System.out.println("**********************************************\n"
				         + "******************BETWEENESS******************\n"
				         + "**********************************************");
		for(Entry<Integer, Double> entry : betweeness_sorted.entrySet()) {
			if(entry.getKey()/10 == 0) {System.out.println("betweeness de  "+entry.getKey()+" : "+String.format("%.2f", entry.getValue()));
			}else {
				System.out.println("betweeness de "+entry.getKey()+" : "+String.format("%.2f", entry.getValue()));						
			}

		}
		
		System.out.println("**********************************************\n"
						 + "******************CLOSENESS*******************\n"
						 + "**********************************************");
		for(Entry<Integer, Double> entry : closeness_sorted.entrySet()) {
			if(entry.getKey()/10 == 0) {System.out.println("closeness de  "+entry.getKey()+" : "+String.format("%.2f", entry.getValue()));			
			}else {
				System.out.println("closeness de "+entry.getKey()+" : "+String.format("%.2f", entry.getValue()));						
			}
		}
		//***************************************
	}
}
