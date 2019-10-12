package offline.TME1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MachineDeGuerre {

	private boolean[] tab_final;
	private boolean[] tab_init;
	private Integer[][] transis;
	public MachineDeGuerre(Automate dfa) {
		tab_final = dfa.getTab_fin();
		transis = dfa.getTransitions();
		this.tab_init = dfa.getTab_init();
	}

	public ArrayList<String> run(String file){
		File f = new File(file);
		ArrayList<String> lines_ok = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while((line = br.readLine()) != null) {
				if(line.length() != 0) {
					String[] words = line.split(" ");
					for(int i =0;i<words.length;i++) {
						String word = words[i];
						if(word.length() != 0 && checkWord(word) && !lines_ok.contains(line)) {//on check chaque mot et on verifie qu'il n'y ait pas de doublon
							lines_ok.add(line);
						}
					}
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lines_ok;
	}

	public boolean checkWord(String word) {
		if(word.equals("ff.).]")){
			System.out.println("worddddddd$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			char[] word_list = word.toCharArray();
			int current_letter = 0;
			boolean start_parcours = false;
			int current_state = 0;
			for(int i = 0;i<tab_init.length;i++) {
				if(tab_init[i]) current_state = i;
			}
			while(current_letter <= word.length()-1) {
				if(!start_parcours) {
					for(int i = 0;i<transis[current_state].length;i++) {
						if(transis[current_state][i] != -1) {
							if((int)(word_list[current_letter]) == i || i == 256) {//si notre lettre courant est �gale � la position 
								start_parcours = true;
								current_state = transis[current_state][i];
								break;
							}
						}
					}
					current_letter++;
					if(!start_parcours) {continue;}
				}else {
					//on as passé le premier etat dans l'automate car on as trouvé une lettre qui match le premier pattern, on fait la suite de l'automate
					boolean suite = false; // permet de savoir si on as trouv� la lettre correspondante dans l'une des possibilit� de de l'etat courant
					for(int i = 0;i<transis[current_state].length;i++) {
						if((int)(word_list[current_letter]) == i || i == 256) {//si notre lettre courant est �gale � la position 
							if(transis[current_state][i] != -1) {
								suite = true;
								current_state = transis[current_state][i];
								break;
							}
						}
					}
					if(suite) {//si on peut faire le prochain etat de l'automate
						if(tab_final[current_state]) {
							return true; //si on est tomb� sur un etat final acceptant on s'arrete
						}else { //si on est pas encore sur le dernier etat, on continue
							if(current_letter == word.length()) break;
							current_letter++;
							suite = false;
						}
					}else {//si on as pas trouv� de lettre pour avancer dans l'automate, on remet l'etat courant � 0 et on refait la recherche � partir de la m�me lettre
						if(tab_final[current_state]) return true;//ajout pour le caract�re universel
						current_state = 0;
						start_parcours = false;
						break;
					}
				}
			}
			return false;
		}
		char[] word_list = word.toCharArray();
		int current_letter = 0;
		boolean start_parcours = false;
		int current_state = 0;
		for(int i = 0;i<tab_init.length;i++) {
			if(tab_init[i]) current_state = i;
		}
		while(current_letter <= word.length()-1) {
			if(!start_parcours) {
				for(int i = 0;i<transis[current_state].length;i++) {
					if(transis[current_state][i] != -1) {
						if((int)(word_list[current_letter]) == i || i == 256) {//si notre lettre courant est �gale � la position 
							start_parcours = true;
							current_state = transis[current_state][i];
							break;
						}
					}
				}
				current_letter++;
				if(!start_parcours) {continue;}
			}else {
				//on as passé le premier etat dans l'automate car on as trouvé une lettre qui match le premier pattern, on fait la suite de l'automate
				boolean suite = false; // permet de savoir si on as trouv� la lettre correspondante dans l'une des possibilit� de de l'etat courant
				for(int i = 0;i<transis[current_state].length;i++) {
					if((int)(word_list[current_letter]) == i || i == 256) {//si notre lettre courant est �gale � la position 
						if(transis[current_state][i] != -1) {
							suite = true;
							current_state = transis[current_state][i];
							break;
						}
					}
				}
				if(suite) {//si on peut faire le prochain etat de l'automate
					if(tab_final[current_state]) {
						return true; //si on est tomb� sur un etat final acceptant on s'arrete
					}else { //si on est pas encore sur le dernier etat, on continue
						if(current_letter == word.length()) break;
						current_letter++;
						suite = false;
					}
				}else {//si on as pas trouv� de lettre pour avancer dans l'automate, on remet l'etat courant � 0 et on refait la recherche � partir de la m�me lettre
					if(tab_final[current_state]) return true;//ajout pour le caract�re universel
					current_state = 0;
					start_parcours = false;
					break;
				}
			}
		}
		return false;
	}
}
