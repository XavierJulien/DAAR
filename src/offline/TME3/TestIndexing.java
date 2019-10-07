package offline.TME3;

import java.io.File;
import java.util.ArrayList;

public class TestIndexing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Indexing index = new Indexing();
		File vol1 = new File("vol1.txt");
		File vol2 = new File("vol2.txt");
		ArrayList<File> files = new ArrayList<>();
		files.add(vol1);
		files.add(vol2);
		RadixTree radix  = Indexing.runIndexing(files);
		ArrayList<RadixTree> rl = radix.getList_noeuds().get(0).getList_noeuds().get(0).getList_noeuds().get(0).getList_noeuds().get(0).getList_noeuds();
		for(RadixTree r : rl) {
			System.out.println(r.getCharacter());
			System.out.println(r.isFin());
			System.out.println(r.getList_coords());
		}
	}

}
