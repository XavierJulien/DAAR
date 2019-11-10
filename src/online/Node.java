package online;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Node {
	
	public static final Double seuil = 0.75;
	
	protected static int id = 0;
	protected int myident;
	protected ArrayList<Node> neighbours;
	protected ArrayList<Double> dist;
	protected HashMap<String, Integer> index;
	
	
	
	public Node(File f) {
		myident = id++;
		this.index = getIndex(f);
		neighbours = new ArrayList<>();
		dist = new ArrayList<>();
	}
	
	public Node(HashMap<String, Integer> index) {
		this.index = index;
		neighbours = new ArrayList<>();
		dist = new ArrayList<>();
	}

	
	public static HashMap<String,Integer> getIndex(File document){
		HashMap <String,Integer> result = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(document));
			String line;
			while((line = br.readLine()) != null) {
				String[] line_split = line.split("[^A-Za-z]");
				for(String word : line_split) {
					if(result.containsKey(word)) {
						result.put(word.toLowerCase(), result.get(word)+1);
					}else {
						result.put(word.toLowerCase(), 1);
					}
				}
			}
			br.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Double getDistJaccard(HashMap<String,Integer> document1,HashMap<String,Integer> document2) {
		int dividende = 0;
		int diviseur = 0;
		if(document1.size() > document2.size()) {
			for(String s2 : document2.keySet()) {
				if(document1.containsKey(s2)){
					dividende += (Math.max(document1.get(s2), document2.get(s2))-Math.min(document1.get(s2), document2.get(s2)));
					diviseur += Math.max(document1.get(s2), document2.get(s2));
				}
			}
		}else{
			for(String s1 : document1.keySet()) {
				if(document2.containsKey(s1)){
					dividende += (Math.max(document2.get(s1), document1.get(s1))-Math.min(document2.get(s1), document1.get(s1)));
					diviseur += Math.max(document2.get(s1), document1.get(s1));
				}
			}
		}
		return dividende/(diviseur*1.0);
	}
	
	public static boolean isNeighbour(HashMap<String,Integer> document1,HashMap<String,Integer> document2) {
		return getDistJaccard(document1, document2) < seuil;
	}
	
	public void generateNeighbours(ArrayList<Node> nodes) {
		for(Node doc : nodes) {
			if(isNeighbour(index, doc.index)) {
				neighbours.add(doc);
			}
		}
	}
	
	public void generateDist(ArrayList<Node> nodes) {
		for(Node doc : nodes) {
			if(isNeighbour(index, doc.index)) {
				dist.add(getDistJaccard(index, doc.index));
			}else {
				dist.add(0.0);
			}
		}
	}
}
