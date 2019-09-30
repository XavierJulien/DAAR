package TME1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Egrep {

	private boolean[] tab_final;
	private Integer[][] transis;
	public Egrep(Automate dfa) {
		tab_final = dfa.getTab_fin();
		transis = dfa.getTransitions();
	}
	
	public ArrayList<String> run(String file){
		File f = new File(file);
		ArrayList<String> words_ok = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while((line = br.readLine()) != null) {
				if(line.length() != 0) {
				String[] words = line.split(" ");
					for(int i =0;i<words.length;i++) {
						String word = words[i];
						if(word.length() != 0 && checkWord(word) && !words_ok.contains(word)) {//on check chaque mot et on verifie qu'il n'y ait pas de doublon
							words_ok.add(word);
						}
					}
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return words_ok;
	}
	
	public boolean checkWord(String word) {
		
		char[] word_list = word.toCharArray();
		int current_letter = 0;
		boolean restart = true;//permet de refaire le parcours du DFA si jamais on as fait un parcours non resultant
		while(restart && current_letter != word.length()-1) {
			boolean start_parcours = false;
			int current_state = 0;
			//on parcours le mot tant qu'on ne peut pas commencer le parcours du DFA
			while(!start_parcours) {
				if(current_letter != word.length()-1) {//si on n'est pas arriv� � la fin du mot , on continue � chercher une lettre pour commencer le parcours
					for(int i = 0;i<transis[current_state].length;i++) {
						if(transis[current_state][i] != -1) {
							if((int)(word_list[current_letter]) == i) {//si notre lettre courant est �gale � la position 
								start_parcours = true;
								current_state = transis[current_state][i];
							}
						}
					}
					current_letter++;
				}else {//si on est arriv� � la fin , on ne passe jamais le premier etat de l'automate, on ne valide pas le mot
					return false;
				}
			}
			//on parcours le DFA tant quon n'est pas a la fin du mot
			boolean suite = false; // permet de savoir si on as trouv� la lettre correspondante dans l'une des possibilit� de de l'etat courant
			while(current_letter != word.length()-1) {
				for(int i = 0;i<transis[current_state].length;i++) {
					if(transis[current_state][i] != -1) {
						if((int)(word_list[current_letter]) == i) {//si notre lettre courant est �gale � la position 
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
						current_letter++;
						suite = false;
					}
				}else {//si on as pas trouv� de lettre pour avancer dans l'automate, on remet l'etat courant � 0 et on refait la recherche � partir de la m�me lettre
					current_state = 0;
					start_parcours = false;
					break;
				}
			}
		}
		return false;
	}
}
