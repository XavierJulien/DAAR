package online;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Node {
	
	public static final Float seuil = 0.75f;
	
	protected static int id = 0;
	protected int myident;
	protected ArrayList<Node> neighbours;
	protected ArrayList<Float> dist;
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
	
	public static float getDistJaccard(HashMap<String,Integer> document1,HashMap<String,Integer> document2) {
		int dividende = 0;
		int diviseur = 0;
		HashSet<String> keys = new HashSet<String>(document1.keySet());
		keys.addAll(document2.keySet());
		for(String k : keys) {
			int cptDoc1 = 0;
			int cptDoc2 = 0;
			if(document1.containsKey(k)){
				cptDoc1 = document1.get(k);
			}
			if(document2.containsKey(k)){
				cptDoc2 = document2.get(k);
			}
			dividende += (Math.max(cptDoc1, cptDoc2)-Math.min(cptDoc1, cptDoc2));
			diviseur += Math.max(cptDoc1, cptDoc2);
		}
		
		return (float) (dividende/(diviseur*1.0));
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
				dist.add(Float.POSITIVE_INFINITY);
			}
		}
	}
}
