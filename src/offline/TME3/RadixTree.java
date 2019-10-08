package offline.TME3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


public class RadixTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1677381878864364819L;


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

	//recupere tous les mots matchant s en préfixe  finir cette methode et celle en dessous 
	public ArrayList<Coord> search(char[] s, ArrayList<Coord> coords) {
		if(s.length == 1) {
			for(RadixTree r : list_noeuds) {
				if(r.character == s[0]) {
					coords.addAll(r.list_coords);
					for(RadixTree r_child : r.list_noeuds)
						coords.addAll(r_child.getCoordsFromChilds(new ArrayList<>()));
					return coords;
				}
			}
		}
		for(RadixTree r : list_noeuds) {
			if(r.character == s[0]) {
				r.search(Arrays.copyOfRange(s, 1, s.length),coords);
			}
		}
		return coords;
	}
//recupere les listes de coords de tous les fils : exemple : Sargon.*
	private ArrayList<Coord> getCoordsFromChilds(ArrayList<Coord> coords) {
		if(this.isFin()) {
			coords.addAll(this.list_coords);
			for(RadixTree r_child : list_noeuds) {
				coords.addAll(r_child.getCoordsFromChilds(new ArrayList<>()));
			}
			return coords;
		}else {
			for(RadixTree r_child : list_noeuds) {
				coords.addAll(r_child.getCoordsFromChilds(new ArrayList<>()));
			}
			return coords;
		}
	}


	public static RadixTree createTree(ArrayList<Pair> string_coords) {
		RadixTree root = new RadixTree('.', null);
		for(Pair p : string_coords) {
			root.add(p.getS().toCharArray(), p.getCoords());
		}
		return root;
	} 

	public void serializeTree(String filename) throws IOException {
		FileOutputStream fichier = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(fichier);
		out.writeObject(this);
		out.close();
		fichier.close();
		System.out.println("Arbre serialisé au nom " + filename);
	}

	public static RadixTree unSerializeTree(String filename) throws IOException, ClassNotFoundException {
		FileInputStream fichier = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fichier);
		RadixTree radix = (RadixTree) ois.readObject();
		ois.close();
		fichier.close();
		return radix;
	}

}
