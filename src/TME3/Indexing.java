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


	static class Pair{
		private Integer a,b;
		public Pair(Integer a, Integer b) {
			this.a = a;
			this.b = b;
		}
		public Integer getA() {return a;}
		public Integer getB() {return b;}
		
		public String toString() {
			String res = "("+getA()+","+getB()+")";
			return res;
		}


	}

	
	public static String getCoords(ArrayList<Pair> a) {
		String res = "";
		for(Pair p : a) {
			res+=p.toString();
		}
		return res;
	}

	public static void main(String[] args) {
		File f = new File("vol1.txt");
		BufferedReader br;
		Matching match = new Matching();
		try {
			br = new BufferedReader(new FileReader(f));

			String line;
			int numeroligne = 0;

			HashMap<String, ArrayList<Pair>> map = new HashMap<String,ArrayList<Pair>>();

			while ((line = br.readLine()) != null) {
				numeroligne++;
				line = line.toLowerCase();
				String[] splitedLine = line.split("[^a-zA-Z'-]");
				for (String word : splitedLine){		
					if(word.length() == 0) {continue;}
					int[] retenue = match.genRetenue(word.toCharArray());
					int colonne = match.matchingAlgo(word.toCharArray(), retenue, line.toCharArray());
					if(colonne == -1 ) {System.out.println(line+";"+word);}
					if (!map.containsKey(word)) {
						ArrayList<Pair> coords_list = new ArrayList<>();
						coords_list.add(new Pair(numeroligne, colonne));
						map.put(word,coords_list);
					}else {
						map.get(word).add(new Pair(numeroligne, colonne));
					}

				}
			}
			br.close();
			
			PrintWriter writer = new PrintWriter("test1.index", "UTF-8");
			map.forEach((k,v)-> {writer.println(k+" "+getCoords(v));});
			writer.close();
		} catch (IOException e) {}
		
		
	}


}

