package TME3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import TME2.Matching;

public class Indexing {

	/** 
	 * Classe représentant la Structure de donnée d'une Paire de deux entiers
	 * getters/setters pour chaque entier
	 * toString pour afficher la paire (A,B)
	 */
	static class Pair{
		private Integer a,b;
		public Pair(Integer a, Integer b) {
			this.a = a;
			this.b = b;
		}
		public Integer getA() {return a;}
		public Integer getB() {return b;}
		public String toString() {return "("+getA()+","+getB()+")";}


	}


	public static String getCoords(ArrayList<Pair> array_pair) {
		String coords = "";
		for(Pair pair : array_pair)
			coords+=pair.toString();
		return coords;
	}

	public static void runIndexing(ArrayList<File> files) {
		for(int i = 0;i<files.size();i++) {
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(files.get(i)));

				String line;
				int numeroligne = 0;

				HashMap<String, ArrayList<Pair>> map = new HashMap<String,ArrayList<Pair>>();

				while ((line = br.readLine()) != null) {
					numeroligne++;
					line = line.toLowerCase();
					String line_lu = "";
					String line_reste = line;
					String[] splitedLine = line.split("[^a-zA-Z'-]");
					for (String word : splitedLine){		
						if(word.length() == 0) {continue;}
						int[] retenue = Matching.genRetenue(word.toCharArray());
						int colonne = Matching.matchingAlgo(word.toCharArray(), retenue, line_reste.toCharArray());
						if (!map.containsKey(word)) {
							ArrayList<Pair> coords_list = new ArrayList<>();
							coords_list.add(new Pair(numeroligne, colonne+line_lu.length()));
							map.put(word,coords_list);
						}else {
							map.get(word).add(new Pair(numeroligne, colonne+line_lu.length()));
						}
						line_lu = line_lu+line_reste.substring(0,colonne+word.length());
						line_reste = line_reste.substring(colonne+word.length()); 
					}
				}
				br.close();

				PrintWriter writer = new PrintWriter("test"+(i+1)+".index", "UTF-8");
				map.forEach((k,v)-> {writer.println(k+" "+getCoords(v));});
				writer.close();
			} catch (IOException e) {}
		}
	}
}

