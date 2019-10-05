package TME3;

import java.util.ArrayList;

public class TestRadixTree {
	
	public static void main(String[] args) {
		ArrayList<Coord> coords1 = new ArrayList<Coord>();
		coords1.add(new Coord(0,10));
		coords1.add(new Coord(1,10));
		coords1.add(new Coord(2,10));
		
		ArrayList<Coord> coords2 = new ArrayList<Coord>();
		coords2.add(new Coord(0,2));
		coords2.add(new Coord(1,2));
		coords2.add(new Coord(2,2));
		
		ArrayList<Pair> string_coords= new ArrayList<Pair>();
		string_coords.add(new Pair("ara",coords1));
		string_coords.add(new Pair("arb",coords2));
		RadixTree root = RadixTree.createTree(string_coords);
		String s2 = "ara";
		System.out.println(root.search(s2.toCharArray()));
		
	}
}
