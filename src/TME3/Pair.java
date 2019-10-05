package TME3;

import java.util.ArrayList;

public class Pair {

	private String s;
	private ArrayList<Coord> coords;
	
	public Pair(String s, ArrayList<Coord> coords) {
		this.s = s;
		this.coords = coords;
	}
	
	public String getS() {
		return s;
	}

	public ArrayList<Coord> getCoords() {
		return coords;
	}
}
