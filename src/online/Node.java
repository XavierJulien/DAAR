package online;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Node {
	
	public static final Double seuil = 0.75;
	
	protected static int id = 0;
	protected int myident;
	protected String title = "";
	
	protected ArrayList<Integer> neighbours = new ArrayList<>();
	protected ArrayList<Double> dist = new ArrayList<>();
	protected HashMap<String, Integer> index;
	
	private static Double dist_temp = 0.0;
	long timeElapsed = 0;
	
	
	public Node(File f,ArrayList<String> words_filter) {
		myident = id++;
		this.index = getIndex(f);
		for(String word : words_filter)	this.index.remove(word);
	}
	
	public HashMap<String,Integer> getIndex(File document){
		int nb_word = 0;
		long startTime = System.nanoTime();
		HashMap <String,Integer> index = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(document));
			String line;
			while((line = br.readLine()) != null) {
				if (line.startsWith("Title")) title = line.substring(7);
				String[] line_split = line.split("[^A-Za-z]");
				for(String word : line_split) {
					nb_word++;
					if(word.equals("")) continue;
					word = word.toLowerCase();
					if(index.containsKey(word)) {
						index.put(word.toLowerCase(), index.get(word)+1);
					}else {
						index.put(word.toLowerCase(), 1);
					}
				}
			}
			br.close();
		}catch(FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
		long endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println(title+" index size"+index.size()+"nb word"+nb_word+" time "+timeElapsed / 1000000);
		return index;
	}
	
	public static double getDistJaccard(HashMap<String,Integer> document1,HashMap<String,Integer> document2) {
		int dividende = 0;
		int diviseur = 0;
		HashSet<String> all_words = new HashSet<String>(document1.keySet());
		all_words.addAll(document2.keySet());
		for(String k : all_words) {
			int cptDoc1 = 0, cptDoc2 = 0;
			if(document1.containsKey(k)) cptDoc1 = document1.get(k);
			if(document2.containsKey(k)) cptDoc2 = document2.get(k);
			dividende += (Math.max(cptDoc1, cptDoc2)-Math.min(cptDoc1, cptDoc2));
			diviseur += Math.max(cptDoc1, cptDoc2);
		}
		dist_temp = dividende/(diviseur*1.0);
		return dividende/(diviseur*1.0);
	}
	
	public static boolean isNeighbour(HashMap<String,Integer> document1,HashMap<String,Integer> document2) {
		return getDistJaccard(document1, document2) <= seuil;
	}
	
	public  void generateNeighboursAndDist(ArrayList<Node> nodes) {
		for(Node doc : nodes) {
			if(this.myident == doc.myident) {dist.add(Double.POSITIVE_INFINITY);continue;}
			if(isNeighbour(index, doc.index)) {
				neighbours.add(doc.myident);
				dist.add(dist_temp);
			}else {
				dist.add(Double.POSITIVE_INFINITY);
			}
		}
	}
}
