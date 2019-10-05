package TME3;

import java.util.ArrayList;
import java.util.Arrays;

public class RadixTree {
	
	private char character;	
	private boolean fin;
	private ArrayList<RadixTree> list_noeuds;
	private ArrayList<Coord> list_coords;
	
	public RadixTree(char character,ArrayList<Coord> coords) {
		this.character = character;
		this.list_noeuds = new ArrayList<RadixTree>();
		this.list_coords = coords;
		this.fin = false;
	}
	
	
	public char getCharacter() {
		return character;
	}
	
	public ArrayList<RadixTree> getList_noeuds() {
		return list_noeuds;
	}
	
	public boolean isFin() {
		return fin;
	}
	
	public void setFin(boolean fin) {
		this.fin = fin;
	}
	
	public void add(char[] s,ArrayList<Coord> coords) {
		
		for(RadixTree r : list_noeuds) {
			if(r.character == s[0]) {
				r.add(Arrays.copyOfRange(s, 1, s.length),coords);
			}
		}
		//le mot n'est pas dans l'arbre actuel
		RadixTree r = new RadixTree(s[0],null);
		r.add(Arrays.copyOfRange(s, 1, s.length),coords);
		list_noeuds.add(r);
	}
	
	public ArrayList<Coord> search(char[] s) {
		if(s.length == 0) {
			return list_coords;
		}
		for(RadixTree r : list_noeuds) {
			if(r.character == s[0]) {
				r.search(Arrays.copyOfRange(s, 1, s.length));
			}
		}
		return null;
	}
	
	public RadixTree createTree(ArrayList<Coord> string_coords) {
		
	} 
	
	
	
}
