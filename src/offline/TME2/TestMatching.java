package offline.TME2;

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
		int[] retenue = Matching.genRetenue(regEx);
		for(int i = 0;i<retenue.length;i++) System.out.println(retenue[i]);
		File f = new File("src/offline/test.txt");
		ArrayList<String> lines_ok = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while((line = br.readLine()) != null) {
				if(line.length() != 0) {
					
					int pos = Matching.matchingAlgo(regEx,retenue,line.toCharArray());
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