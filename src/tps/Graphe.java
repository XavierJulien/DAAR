package tps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


public class Graphe {

	ArrayList<Integer> sommets = new ArrayList<>();
	int nbSommets;
	ArrayList<Integer> [][] mat_path;
	float [][] mat_dist; 
	float [][] mat_nb_chemin; 

	@SuppressWarnings("unchecked")
	public Graphe(String filename, int nbSommets) {
		this.nbSommets = nbSommets;
		this.mat_dist = new float[nbSommets][nbSommets];
		this.mat_path = new ArrayList[nbSommets][nbSommets];
		for (int i=0;i<nbSommets;i++) {
			for (int j=0;j<nbSommets;j++) {
				if(i==j) {
					mat_dist[i][j] = 0f;
					mat_path[i][j] = new ArrayList<>();
				}
				mat_dist[i][j] = Float.POSITIVE_INFINITY;
				mat_path[i][j] = new ArrayList<>();
			}
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			HashSet<Integer> sommetsSet = new HashSet<>();
			while((line = br.readLine()) != null) {
				String[] splitedLine = line.split(" ");
				int s1 = Integer.valueOf(splitedLine[0]);
				int s2 = Integer.valueOf(splitedLine[1]);
				sommetsSet.add(s1);
				sommetsSet.add(s2);
				mat_dist[s1][s2]=1f;
				mat_dist[s2][s1]=1f;
				mat_path[s1][s2].add(s2);
				mat_path[s2][s1].add(s1);
			}
			sommets.addAll(sommetsSet);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//calcul de la matrice de distance
		for (int k = 0; k < nbSommets; k++) {
			for (int i = 0; i < nbSommets; i++) {
				for (int j = 0; j < nbSommets; j++) {
					if(i==j) continue;
					if(mat_dist[i][j] > mat_dist[i][k] + mat_dist[k][j]) {
						mat_dist[i][j] = mat_dist[i][k] + mat_dist[k][j];
					}
				}
			}
		}
		//calcul de la matrice de chemin
		for (int k = 0; k < nbSommets; k++) {
			for (int i = 0; i < nbSommets; i++) {
				for (int j = 0; j < nbSommets; j++) {
					if(i==j) continue;
					if(mat_dist[i][j] != Float.POSITIVE_INFINITY && mat_dist[i][j] == mat_dist[i][k] + mat_dist[k][j] && mat_dist[i][k] == 1) {
						mat_path[i][j].add(k);
					}
					if(mat_dist[i][j] > mat_dist[i][k] + mat_dist[k][j]) {
						if (mat_dist[i][k] == 1) {
							mat_path[i][j].clear();
							mat_path[i][j].add(k);
						}
					}
				}
			}
		}
	}

	public void printmat_path() {
		for (int j = 0; j < mat_path.length; j++) {
			System.out.print(j+" |" );
			for (int j2 = 0; j2 < mat_path.length; j2++) {
				System.out.print(" "+mat_path[j][j2]+" ");	
			}
			System.out.println();
		}
	}

	public void printmat_dist() {
		for (int j = 0; j < mat_dist.length; j++) {
			System.out.print(j+" |" );
			for (int j2 = 0; j2 < mat_dist.length; j2++) {
				System.out.print(String.format("%.2f", mat_dist[j][j2])+" ");
			}
			System.out.println();
		}
	}

	public float[][] getMat_distance() {
		return mat_dist;
	}

	public int getNbPlusPetitChemin(Integer s, Integer t) {
		int nbChemin = 0;
		ArrayList<Integer> working_list = new ArrayList<>();
		ArrayList<Integer> nexts;
		boolean begin = true;
		working_list.add(s);
		while(!working_list.isEmpty()) {
			Integer current = working_list.remove(0);
			nexts = mat_path[current][t];
			if (begin) {
				nbChemin += nexts.size();
				begin = false;
			}else {
				nbChemin += nexts.size()-1;
			}
			if (nexts.get(0) != t) working_list.addAll(nexts);
		}
		return nbChemin;
	}

	public int getNbPlusPetitCheminByV(Integer s, Integer t, Integer v) {
		int nbChemin = 0;
		ArrayList<Integer> working_list = new ArrayList<>();
		working_list.add(s);
		ArrayList<Integer> nexts;
		for (int i=0;i<working_list.size();i++) {
			Integer current = working_list.get(i);
			nexts = mat_path[current][t];
			if (current.equals(v)) nbChemin += nexts.size()-1;
			if (nexts.size() == 1 && nexts.get(0).equals(t)) continue;
			working_list.addAll(nexts);
		}
		return nbChemin + (int) working_list.stream().filter((e)-> e.equals(v)).count();
	}

	//betweeness : à quel point le noeud est important au vu du passage d'info entre les autres noeuds (permet une forte communication)
	public double getBetweenness(int sommet) {		
		double res = 0.0;
		for (int i=0; i<sommets.size(); i++) {
			Integer s = sommets.get(i);
			if (s.equals(sommet)) continue;
			for (int j=i+1; j<sommets.size(); j++) {
				Integer t = sommets.get(j);
				if (t.equals(sommet)) continue; //il ne devrait pas y avoir ce cas mais juste au cas où
				double nbByV = getNbPlusPetitCheminByV(s,t,sommet);
				double nb = getNbPlusPetitChemin(s,t);
				res += nbByV/nb;
			}
		}
		return res;
	}

	//closeness : noeud proche de tout les autres noeuds
	public double getCloseness(Integer sommet) {
		int sum = 0;
		for (Integer u : sommets) {
			if(u.equals(sommet)) continue;
			sum += mat_dist[sommet][u];
		}
		return (nbSommets-1.)/sum;
	}



}