package online;
import java.util.ArrayList;


public class Graphe {

	ArrayList<Integer> sommets = new ArrayList<>();
	int nbSommets;
	ArrayList<Integer> [][] mat_path;
	double [][] mat_dist; 
	double [][] mat_nb_chemin; 

	@SuppressWarnings("unchecked")
	public Graphe(ArrayList<Node> all_nodes,double [][] jaccard, int nbSommets) {
		for (int i = 0; i < nbSommets; i++) {
			sommets.add(i);
		}
		this.nbSommets = nbSommets;
		this.mat_dist = new double[nbSommets][nbSommets];
		this.mat_nb_chemin = new double[nbSommets][nbSommets];
		this.mat_path = new ArrayList[nbSommets][nbSommets];
		for (int i=0;i<nbSommets;i++) {
			ArrayList<Node> neighbours = all_nodes.get(i).neighbours;
			for (int j=0;j<nbSommets;j++) {
				final int cpt = j;
				mat_path[i][j] = new ArrayList<>();
				if(neighbours.stream().filter(n -> n.myident == cpt).findFirst().isPresent()) {
					this.mat_nb_chemin[i][j] = 1f;
					this.mat_dist[i][j] = Math.round(jaccard[i][j]*100.0)/100.0;
					this.mat_path[i][j].add(j);
				}else {
					mat_nb_chemin[i][j] = Double.POSITIVE_INFINITY;
					mat_dist[i][j] = Double.POSITIVE_INFINITY;					
				}
			}
		}

		//calcul de la matrice de distance
		for (int k = 0; k < nbSommets; k++) {
			for (int i = 0; i < nbSommets; i++) {
				if(i == k) continue;
				for (int j = 0; j < nbSommets; j++) {
					if(i==j || j == k) continue;
					if(mat_nb_chemin[i][j] > mat_nb_chemin[i][k] + mat_nb_chemin[k][j]) mat_nb_chemin[i][j] = mat_nb_chemin[i][k] + mat_nb_chemin[k][j];
					if(mat_dist[i][j] > mat_dist[i][k] + mat_dist[k][j]) mat_dist[i][j] = Math.round((mat_dist[i][k] + mat_dist[k][j])*100.0)/100.0;
				}
			}
		}
		//calcul de la matrice de chemin
		for (int k = 0; k < nbSommets; k++) {
			for (int i = 0; i < nbSommets; i++) {
				if(i == k) continue;
				for (int j = 0; j < nbSommets; j++) {
					if(i==j || j == k) continue;
					if(mat_nb_chemin[i][k] == 1) {//si k est un de mes voisins
						if(mat_nb_chemin[i][j] != Double.POSITIVE_INFINITY && 
						   mat_nb_chemin[i][j] == mat_nb_chemin[i][k] + mat_nb_chemin[k][j]) 
							mat_path[i][j].add(k);
						
						if(mat_nb_chemin[i][j] > mat_nb_chemin[i][k] + mat_nb_chemin[k][j]) {
							mat_path[i][j].clear();
							mat_path[i][j].add(k);
						}
					}
				}
			}
		}
		
		//re-calcul de la matrice de chemin
		for (int i = 0; i < nbSommets; i++) {
			for (int j = 0; j < nbSommets; j++) {
				if(i==j) continue;
				if(mat_dist[i][j] != Double.POSITIVE_INFINITY && mat_path[i][j].size() > 1) {
					for (int k = mat_path[i][j].size()-1; k >= 0; k--) {
						if(mat_dist[i][j] < Math.round((mat_dist[i][mat_path[i][j].get(k)] + mat_dist[mat_path[i][j].get(k)][j])*100.0)/100.0) {
							mat_path[i][j].remove(k);
						}
					}
				}
			}
		}
	}

	public void printmat_path() {
		for (int j = 0; j < mat_path.length; j++) {
			System.out.print(j+" |" );
			for (int j2 = 0; j2 < mat_path.length; j2++) System.out.print(" "+mat_path[j][j2]+" ");	
			System.out.println();
		}
	}

	public void printmat_nb_chemin() {
		for (int j = 0; j < mat_nb_chemin.length; j++) {
			System.out.print(j+" |" );
			for (int j2 = 0; j2 < mat_nb_chemin.length; j2++) System.out.print(" "+mat_nb_chemin[j][j2]+" ");	
			System.out.println();
		}
	}
	
	public void printmat_dist() {
		for (int j = 0; j < mat_dist.length; j++) {
			System.out.print(j+" |" );
			for (int j2 = 0; j2 < mat_dist.length; j2++) System.out.print(String.format("%.2f", mat_dist[j][j2])+" ");
			System.out.println();
		}
	}

	public double[][] getMat_distance() {
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
				//System.out.println("i et j : "+i+", "+j);
				double nbByV = getNbPlusPetitCheminByV(s,t,sommet);
				double nb = getNbPlusPetitChemin(s,t);
				//System.out.println("nbByV "+nbByV+ ", nb "+nb);
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