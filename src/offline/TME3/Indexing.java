package offline.TME3;

import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import offline.TME2.Matching;

public class Indexing {

	public static String getCoords(ArrayList<Coord> array_pair) {
		String coords = "";
		if(array_pair.size() == 1) {
			return array_pair.get(0).toString();
		}
		for(int i = 0;i<array_pair.size();i++) {
			if(i == array_pair.size()-1) {
				coords+=array_pair.get(i).toString();
			}else {
				coords+= array_pair.get(i).toString() + ";";
			}
		}
		return coords;
	}

	public static void runIndexing(ArrayList<File> files) {
		BufferedReader br;
		try {
			for(File file : files) {
				br = new BufferedReader(new FileReader(file));

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
							if(word.length() > 2) {
								map.put(word,coords_list);								
							}
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
				PrintWriter writer = new PrintWriter("src/offline/"+file.getName()+".index", "UTF-8");
				sorted_map.forEach((k,v)-> {writer.println(k+" "+getCoords(v));});
				writer.close();
				ArrayList<Pair> string_coords = new ArrayList<>();
				sorted_map.forEach((k,v) -> string_coords.add(new Pair(k,v)));
				//serialisation pour ajouter le radix tree dans un fichier et ne pas refaire plusieurs fois cette m�thode
				// DECOMMENTER EN DESSOUS POUR SERIALISER 
				//RadixTree.createTree(string_coords).serializeTree(file+".ser") ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void runIndexing(File file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

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
			PrintWriter writer = new PrintWriter("src/offline/ressources/"+file.getName()+".index", "UTF-8");
			sorted_map.forEach((k,v)-> {
				writer.println(k+" "+getCoords(v));});
			writer.close();
			//ArrayList<Pair> string_coords = new ArrayList<>();
			//sorted_map.forEach((k,v) -> string_coords.add(new Pair(k,v)));
			//serialisation pour ajouter le radix tree dans un fichier et ne pas refaire plusieurs fois cette m�thode
			// DECOMMENTER EN DESSOUS POUR SERIALISER 
			//RadixTree.createTree(string_coords).serializeTree(file+".ser") ;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static RadixTree createRadix(File file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

			ArrayList<Pair> map = new ArrayList<Pair>();
			String line;
			while ((line = br.readLine()) != null) {
				ArrayList<Coord> coords = new ArrayList<>();
				line = line.toLowerCase();

				String[] splitedLine = line.split(" ");
				String word = splitedLine[0];
				String list_coord = splitedLine[1];
				String[] list_coord_split = list_coord.split(";");
				for(int i = 0;i<list_coord_split.length;i++) {
					String coord = list_coord_split[i];
					coord = coord.substring(1,coord.length()-1);
					String[] pair_coord = coord.split(",");
					coords.add(new Coord(Integer.valueOf(pair_coord[0]),Integer.valueOf(pair_coord[1])));
				}
				map.add(new Pair(word,coords));
			}
			br.close();
			//serialisation pour ajouter le radix tree dans un fichier et ne pas refaire plusieurs fois cette m�thode
			return RadixTree.createTree(map);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("  >> Please enter a file: ");
		String file = scanner.next();
		scanner.close();
		File file_index = new File("src/offline/ressources/"+file+".index");
		if(!file_index.exists()) {
			Indexing.runIndexing(new File("src/offline/ressources/"+file));
		}
	}
}