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
		if(facteur.matches(".*[|()*.].*")) algo = 2;
		if(facteur.matches("[a-zA-Z-']*")) {
			if(facteur.length() <=2) {
				algo = 2;
			}else {
				algo = 1;				
			}
		}
		return algo;
	}

	public static void run(String regEx, String fichier) {
		//int algo = chooseAlgo(regEx);
		int algo = 2;
		if(algo == 3) {
			System.out.println("Utilisation de la Machine de Guerre");
			String[] args = new String[2];
			args[0] = regEx;
			args[1] = fichier;
			Long time_before = System.currentTimeMillis();
			RegEx.main(args);
			Long time_after = System.currentTimeMillis();
			System.out.println("Temps écoulé pour la Machine de Guerre (en ms) : "+(time_after-time_before));
		}
		if(algo == 2) {
			System.out.println("Utilisation de KMP");
			String[] args = new String[2];
			args[0] = regEx;
			args[1] = fichier;
			Long time_before = System.currentTimeMillis();
			Matching.main(args);
			Long time_after = System.currentTimeMillis();
			System.out.println("Temps écoulé pour KMP (en ms) : "+(time_after-time_before));

		}
		if(algo == 1) {
			//Pour UTILISER LA SERIALISATION, Changer l'init du tree par la m�thode unserializeTree et d�commenter la clause catch en bas
			System.out.println("Utilisation du RadixTree");
			try {
				//RadixTree tree = (RadixTree)RadixTree.unSerializeTree("src/offline/"+fichier+".ser");
				BufferedReader br = new BufferedReader(new FileReader(new File("src/offline/"+fichier)));
				ArrayList<String> text = new ArrayList<String>();
				String line;
				while ((line = br.readLine()) != null) text.add(line);
				br.close();
				RadixTree tree = Indexing.createRadix(new File("src/offline/"+fichier+".index"));
				Long time_before = System.currentTimeMillis();
				ArrayList<Coord> coord_regex = tree.search(regEx.toLowerCase().toCharArray(), new ArrayList<>());
				System.out.println(coord_regex.size());
				System.out.println("  >> egrep \""+regEx+"\" "+fichier+" \n");
				//colorisation du texte et affichage des lignes match�s dans le terminal
				for(Coord c : coord_regex) {
					String s = "";
					s+=text.get(c.getA()-1).substring(0, c.getB());
					s+=ANSI_BRIGHT_YELLOW+text.get(c.getA()-1).substring(c.getB(),c.getB()+regEx.length());
					s+=ANSI_RESET+text.get(c.getA()-1).substring(c.getB()+regEx.length(), text.get(c.getA()-1).length());
					System.out.println(s);
				}
				Long time_after = System.currentTimeMillis();
				System.out.println("Temps écoulé pour le radixTree(en ms) : "+(time_after-time_before));
			/*} catch (ClassNotFoundException e) {e.printStackTrace();*/} catch (IOException e) {e.printStackTrace();}
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("  >> Please enter a regEx: ");
		String regEx = scanner.next();
		System.out.print("  >> Please enter a file: ");
		String file = scanner.next();
		scanner.close();
		//File file_ser = new File("src/offline/"+file+".ser");
		File file_index = new File("src/offline/"+file+".index");
		/*if(!file_ser.exists()) {
			System.out.println("on applique le pr� processing de radix tree");
			Indexing.runIndexing(new File("src/offline/"+file));
		}*/
		if(!file_index.exists()) {
			Indexing.runIndexing(new File("src/offline/"+file));
		}
		
		run(regEx,file);
		
		//tests � la main
		//run("S(a|g|r)*on","src/offline/vol2.txt");
		//run("Sargonids--","vol2.txt");
		//run("mimm","src/offline/test.txt");
	}
}
