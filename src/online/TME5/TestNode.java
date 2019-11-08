package online.TME5;

import java.io.File;
import java.util.ArrayList;

public class TestNode {

	public static void main(String[] args) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<File> files = new ArrayList<File>();
		for(int i = 0;i<50;i++) {
			File f = new File("src/online/TME5/livres/"+i+".txt");
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
			System.out.println("Document "+n.myident+" : "+n.neighbours.size()+ " voisins.");
		}
	}
}
