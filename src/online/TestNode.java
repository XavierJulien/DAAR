package online;

import java.io.File;
import java.util.ArrayList;

public class TestNode {

	public static void main(String[] args) {
		int nblivres = 7;
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<File> files = new ArrayList<File>();
		float[][] tab_dist = new float[nblivres][nblivres];
		for(int i = 1;i<nblivres+1;i++) {
			File f = new File("src/online/livres2/4299"+i+".txt");
			files.add(f);
		}
		for(File f : files) {
			Node n = new Node(f);
			nodes.add(n);
		}
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
		for (int i = 0; i < nblivres/*tab_dist.length*/; i++) {
			for (int j = 0; j < nblivres/*tab_dist.length*/; j++) {
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
		
		System.out.println("betweeness de 3"+g.getBetweenness(3));
		
		
		Graphe g2loss = new Graphe("src/online/test/test.txt",5);
		g2loss.printmat_dist();
		g2loss.printmat_path();
	}
}
