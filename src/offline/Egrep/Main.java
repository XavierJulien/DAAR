package offline.Egrep;

import offline.TME2.Matching;
import offline.TME1.RegEx;

public class Main {

	public static int chooseAlgo(String facteur) {
		int algo = 3;//par defaut
		if(facteur.matches("[a-zA-Z-']*")) algo = 2;
		if(facteur.matches("[a-zA-Z]*")) algo = 1;
		return algo;
	}

	public static void run(String regEx, String fichier) {
		int algo = chooseAlgo(regEx);
		if(algo == 3) {
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
			String[] args = new String[2];
			args[0] = regEx;
			args[1] = fichier;
			Matching.main(args);
		}
	}

	public static void main(String[] args) {
		//run("S(a|g|r)*on","vol2.txt");
		run("Sargon","vol2.txt");
		//run("S(a|g|r)*on","vol2.txt");
	}
}
