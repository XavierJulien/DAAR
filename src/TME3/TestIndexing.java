package TME3;

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
		index.runIndexing(files);
	}

}
