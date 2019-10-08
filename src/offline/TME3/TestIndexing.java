package offline.TME3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TestIndexing {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		File vol1 = new File("src/offline/vol1.txt");
		File vol2 = new File("src/offline/vol2.txt");
		ArrayList<File> files = new ArrayList<>();
		files.add(vol1);
		files.add(vol2);
		//Indexing.runIndexing(files);
			
		RadixTree tree = (RadixTree)RadixTree.unSerializeTree("src/offline/vol2.txt.ser");

		//RadixTree tree = radix_list.get(0);
		/*ArrayList<RadixTree> rl = tree.getList_noeuds();
		for(RadixTree r : rl) {
			System.out.println(r.getCharacter());
			System.out.println(r.isFin());
			System.out.println(r.getList_coords());
		}*/
		System.out.println(tree.search("Sargon".toLowerCase().toCharArray(),new ArrayList<>()).size());
	}

}
