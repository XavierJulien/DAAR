package offline.TME3;

import java.io.Serializable;

/** 
 * Classe représentant la Structure de donnée d'une Coord de deux entiers
 * getters/setters pour chaque entier
 * toString pour afficher la paire de coord (A,B)
 */
public class Coord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2581979497981392421L;
	private Integer a,b;
	public Coord(Integer a, Integer b) {
		this.a = a;
		this.b = b;
	}
	public Integer getA() {return a;}
	public Integer getB() {return b;}
	public String toString() {return "("+getA()+","+getB()+")";}


}