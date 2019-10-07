package offline.Egrep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import offline.TME1.RegEx;
import offline.TME2.Matching;
import offline.TME3.Coord;
import offline.TME3.Indexing;
import offline.TME3.RadixTree;

public class Main {

	public static int chooseAlgo(String facteur) {
		int algo = 3;//par defaut
		if(facteur.matches("[a-zA-Z-']*")) algo = 2;
		if(facteur.matches("[a-zA-Z]*")) {
			if(facteur.length() <=2) {
				algo = 2;
			}else {
				algo = 1;				
			}
		}
		return algo;
	}

	public static void run(String regEx, String fichier) {
		int algo = chooseAlgo(regEx);
		if(algo == 3) {
			System.out.println("algo 3");
			String[] args = new String[2];
			args[0] = regEx;
			args[1] = fichier;
			RegEx.main(args);
		}
		if(algo == 2) {
			System.out.println("algo 2");
			String[] args = new String[2];
			args[0] = regEx;
			args[1] = fichier;
			Matching.main(args);
		}
		if(algo == 1) {
			System.out.println("algo 1");
			String[] args = new String[2];
			args[0] = regEx;
			args[1] = fichier;
			try {
				RadixTree tree = (RadixTree)RadixTree.unSerializeTree(fichier+".ser");
				ArrayList<Coord> coord_regex = tree.search(regEx.toLowerCase().toCharArray());
				System.out.println(coord_regex.size());
				System.out.println("  >> egrep \""+regEx+"\" "+fichier+" \n");
				/*BufferedReader br = new BufferedReader(new FileReader(new File(fichier)));
				int current_line = 0;
				String line;
				while ((line = br.readLine()) != null) {
					for(int i =0;i<coord_regex.size();i++) {
						if(coord_regex.get(i).getA() == current_line) {
							System.out.println(current_line);
							System.out.println(line);
							coord_regex.remove(i);
							current_line++;
							break;
						}
					}
					current_line++;
				}*/
				Matching.main(args);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		File f = new File("src/offline/vol2.txt.ser");
		if(!f.exists()) {
			System.out.println("on applique le pré processing de radix tree");
			Indexing.runIndexing(new File("src/offline/vol2.txt"));
		}
		//run("S(a|g|r)*on","src/offline/vol2.txt");
		run("Sargon","src/offline/vol2.txt");
		//run("S(a|g|r)*on","vol2.txt");
	}
}
