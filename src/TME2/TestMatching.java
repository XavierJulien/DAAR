package TME2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestMatching {

	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		System.out.print("  >> Please enter a regEx: ");
		char[] regEx = scanner.next().toCharArray();
		scanner.close();
		Matching m = new Matching();
		int[] retenue = m.genRetenue(regEx);
		File f = new File("vol1.txt");
		ArrayList<String> lines_ok = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while((line = br.readLine()) != null) {
				if(line.length() != 0) {
					int pos = m.matchingAlgo(regEx,retenue,line.toCharArray());
					if(!lines_ok.contains(line) && pos != -1) {//on check chaque mot et on verifie qu'il n'y ait pas de doublon
						lines_ok.add(line);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String res = "";
		for(String s : lines_ok) {
			res += s+"\n";
		}
		System.out.println(res);
	}
}