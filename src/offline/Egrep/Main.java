package offline.Egrep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import offline.TME1.RegEx;
import offline.TME2.Matching;
import offline.TME3.Coord;
import offline.TME3.Indexing;
import offline.TME3.RadixTree;

public class Main {

	public static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
	public static final String ANSI_RESET = "\u001B[0m";

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
			System.out.println("Utilisation de la Machine de Guerre");
			String[] args = new String[2];
			args[0] = regEx;
			args[1] = fichier;
			RegEx.main(args);
		}
		if(algo == 2) {
			System.out.println("Utilisation de KMP");
			String[] args = new String[2];
			args[0] = regEx;
			args[1] = fichier;
			Matching.main(args);
		}
		if(algo == 1) {
			//Pour UTILISER LA SERIALISATION, Changer l'init du tree par la méthode unserializeTree et décommenter la clause catch en bas
			System.out.println("Utilisation du RadixTree");
			try {
				//RadixTree tree = (RadixTree)RadixTree.unSerializeTree("src/offline/"+fichier+".ser");
				RadixTree tree = Indexing.createRadix(new File("src/offline/"+fichier));
				ArrayList<Coord> coord_regex = tree.search(regEx.toLowerCase().toCharArray(), new ArrayList<>());
				System.out.println("  >> egrep \""+regEx+"\" "+fichier+" \n");
				BufferedReader br = new BufferedReader(new FileReader(new File("src/offline/"+fichier)));
				ArrayList<String> text = new ArrayList<String>();
				String line;
				while ((line = br.readLine()) != null) text.add(line);
				br.close();
				//colorisation du texte et affichage des lignes matchés dans le terminal
				for(Coord c : coord_regex) {
					String s = "";
					s+=text.get(c.getA()-1).substring(0, c.getB());
					s+=ANSI_BRIGHT_YELLOW+text.get(c.getA()-1).substring(c.getB(),c.getB()+regEx.length());
					s+=ANSI_RESET+text.get(c.getA()-1).substring(c.getB()+regEx.length(), text.get(c.getA()-1).length());
					System.out.println(s);
				}	
			/*} catch (ClassNotFoundException e) {e.printStackTrace();*/} catch (IOException e) {e.printStackTrace();}
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("  >> Please enter a file: ");
		String file = scanner.next();
		System.out.print("  >> Please enter a regEx: ");
		String regEx = scanner.next();
		scanner.close();
		File file_ser = new File("src/offline/"+file+".ser");
		if(!file_ser.exists()) {
			System.out.println("on applique le pré processing de radix tree");
			Indexing.runIndexing(new File("src/offline/"+file));
		}
		if(file_ser.exists()) {
			System.out.println(file);			
		}
		run(regEx,file);
		
		//tests à la main
		//run("S(a|g|r)*on","src/offline/vol2.txt");
		//run("Sargonids--","vol2.txt");
		//run("mimm","src/offline/test.txt");
	}
}
