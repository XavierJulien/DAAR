package offline.TME3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import static java.util.stream.Collectors.toMap;

import offline.TME2.Matching;

public class Indexing {

	public static String getCoords(ArrayList<Coord> array_pair) {
		String coords = "";
		for(Coord pair : array_pair)
			coords+=pair.toString();
		return coords;
	}

	public static RadixTree runIndexing(ArrayList<File> files) {
		for(int i = 0;i<files.size();i++) {
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(files.get(i)));

				String line;
				int numeroligne = 0;

				HashMap<String, ArrayList<Coord>> map = new HashMap<String,ArrayList<Coord>>();

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
							ArrayList<Coord> coords_list = new ArrayList<>();
							coords_list.add(new Coord(numeroligne, colonne+line_lu.length()));
							map.put(word,coords_list);
						}else {
							map.get(word).add(new Coord(numeroligne, colonne+line_lu.length()));
						}
						line_lu = line_lu+line_reste.substring(0,colonne+word.length());
						line_reste = line_reste.substring(colonne+word.length()); 
					}
				}
				br.close();
				HashMap<String, ArrayList<Coord>> sorted_map = map.entrySet().stream()
						.sorted(Comparator.comparingInt(v -> v.getValue().size()))
						.collect(toMap(
								Map.Entry::getKey,
								Map.Entry::getValue,
								(a, b) -> { throw new AssertionError(); },
								LinkedHashMap::new
								));
				ArrayList<Pair> string_coords = new ArrayList<>();
				sorted_map.forEach((k,v) -> string_coords.add(new Pair(k,v)));
				return RadixTree.createTree(string_coords);
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}
}
/*PrintWriter writer = new PrintWriter("test"+(i+1)+".index", "UTF-8");
map.forEach((k,v)-> {writer.println(k+" "+getCoords(v));});
writer.close();
*/