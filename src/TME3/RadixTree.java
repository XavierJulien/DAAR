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

	public ArrayList<Coord> getList_coords() {
		return list_coords;
	}

	public void setList_coords(ArrayList<Coord> list_coords) {
		this.list_coords = list_coords;
	}

	public boolean isFin() {
		return fin;
	}

	public void setFin(boolean fin) {
		this.fin = fin;
	}

	public void add(char[] s,ArrayList<Coord> coords) {
		if(s.length == 1) {
			for(RadixTree r : list_noeuds) {
				if(r.character == s[0]) {
					r.setList_coords(coords);
					r.setFin(true);
					return;
				}
			}
			RadixTree r = new RadixTree(s[0],coords);
			r.setFin(true);
			list_noeuds.add(r);
		}else {
			for(RadixTree r : list_noeuds) {
				if(r.character == s[0]) {
					r.add(Arrays.copyOfRange(s, 1, s.length),coords);
					return;
				}
			}
			RadixTree r = new RadixTree(s[0],null);
			r.add(Arrays.copyOfRange(s, 1, s.length),coords);
			list_noeuds.add(r);
		}
	}

	public ArrayList<Coord> search(char[] s) {
			if(s.length == 1) {
				for(RadixTree r : list_noeuds) {
					if(r.character == s[0]) {
						return r.list_coords;
					}
				}
				return null;
			}
			for(RadixTree r : list_noeuds) {
				if(r.character == s[0]) {
					System.out.println(r.character);
					return r.search(Arrays.copyOfRange(s, 1, s.length));
				}
			}
			return null;
	}

	public static RadixTree createTree(ArrayList<Pair> string_coords) {
		RadixTree root = new RadixTree('.', null);
		for(Pair p : string_coords) {
			root.add(p.getS().toCharArray(), p.getCoords());
		}
		return root;
	} 



}
