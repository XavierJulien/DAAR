package online;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TestNode {

	public static void main(String[] args) {
		
		//CREATION DES LIVRES
		int nblivres = 50;
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<File> files = new ArrayList<File>();
		double[][] tab_dist = new double[nblivres][nblivres];
		for(int i = 0;i<nblivres;i++) {
			File f = new File("src/online/livres/"+i+".txt");
			files.add(f);
		}
		
		//FILTRAGE DE MOTS TROPS COMMUN
		ArrayList<String> words_filter = new ArrayList<>();
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/online/100.txt"));
			while((line = br.readLine()) != null) 
				words_filter.add(line);
			br.close();
		}catch(FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
		
		//CREATION DE NOEUD POUR CHAQUE LIVRE
		for(File f : files) nodes.add(new Node(f,words_filter));
		
		for(Node n : nodes) {
			ArrayList<Node> nodes_without_node = new ArrayList<Node>(nodes);
			nodes_without_node.remove(n.myident);
			n.generateNeighbours(nodes_without_node);
			n.generateDist(nodes);
			for(int i = 0;i<nodes.size();i++) {
				if(n.myident == i) {
					tab_dist[i][n.myident] = Float.POSITIVE_INFINITY;
				}else {
					tab_dist[i][n.myident] = n.dist.get(i);
				}
			}
			System.out.println("Document "+n.myident+" : "+n.neighbours.size()+ " voisins.");
		}
		
		//AFFICHAGE
		for (int i = 0; i < nblivres; i++) {
			for (int j = 0; j < nblivres; j++) {
				if(j == 0) {
					System.out.print(i+" :"+String.format("%.2f", tab_dist[i][j])+" ");				
				}else {
					System.out.print(String.format("%.2f", tab_dist[i][j])+" ");					
				}
			}
			System.out.println();
		}
		Graphe g = new Graphe(nodes, tab_dist, nblivres);
		System.out.println("mat dist");
		g.printmat_dist();
		System.out.println("mat path");
		g.printmat_path();
		
		for(int i = 0;i<nblivres;i++) {
			//System.out.println("betweeness de "+i+" : "+g.getBetweenness(i));			
			System.out.println("closeness de "+i+" : "+g.getCloseness(i));
		}
	}
}
