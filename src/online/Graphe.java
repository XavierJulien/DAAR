package online;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


public class Graphe {

	HashSet<Integer> sommets = new HashSet<>();
	int nbSommet = sommets.size();
	ArrayList<Integer> [][] mat_path;
	int [][] mat_distance; 

	@SuppressWarnings("unchecked")
	public Graphe(String filename) {
		this.mat_distance = new int[nbSommet][nbSommet];
		this.mat_path = new ArrayList[nbSommet][nbSommet];
		for (int i=0;i<nbSommet;i++) {
			for (int j=0;j<nbSommet;j++) {
				mat_distance[i][j] = Integer.MAX_VALUE;
				mat_path[i][j] = new ArrayList<>();
			}
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while((line = br.readLine()) != null) {
				String[] splitedLine = line.split(" ");
				int s1 = Integer.valueOf(splitedLine[0]);
				int s2 = Integer.valueOf(splitedLine[1]);
				sommets.add(s1);
				sommets.add(s2);
				mat_distance[s1][s2]=1;
				mat_distance[s2][s1]=1;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public double getBetweenness(int sommet) {



		for (int k = 0; k < nbSommet; k++) {
			for (int i = 0; i < nbSommet; i++) {
				for (int j = 0; j < nbSommet; j++) {
					if(mat_distance[i][j] > mat_distance[i][k] + mat_distance[k][j]) {
						mat_distance[i][j] = mat_distance[i][k] + mat_distance[k][j];
						mat_path[i][j].add(k);
	
					}
				}
			}
		}

		return .0;
	}

	/*if (dist[i][j] > dist[i][k] + dist[k][j]) {
		dist[i][j] = dist[i][k] + dist[k][j];
		paths[i][j] = paths[i][k];

	}*/
	public double getCloseness(int sommet) {
		int somme = 0;
		for (Integer u : sommets) {
			if(u==sommet) continue;
			somme += mat_distance[sommet][u];
		}
		return nbSommet-1/somme;
	}



}
