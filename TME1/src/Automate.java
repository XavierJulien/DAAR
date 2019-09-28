import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Automate {
	
	private Integer[][] transitions;
	private ArrayList<ArrayList<Integer>> epsilon_transi; 
	private boolean[] tab_init ;
	private boolean[] tab_fin; 
	private int einitial;
	private int efinal;
	
	//private RegExTree input;
	
	//constructeur d'un automate simple (basis)
	public Automate(RegExTree input) {
		this.einitial = 0;
		this.efinal = 1;
		this.transitions = new Integer[2][256]; 
		this.epsilon_transi = new ArrayList<>();
		this.tab_init = new boolean[2];
		this.tab_fin = new boolean[2];
		initializeEpsilonsInitFin(2);
		initializeTransitions(2);
	}
	
	public Automate(int nbEtat) {
		this.transitions = new Integer[nbEtat][256];
		this.epsilon_transi = new ArrayList<>();
		this.tab_init = new boolean[nbEtat];
		this.tab_fin = new boolean[nbEtat];
		initializeEpsilonsInitFin(nbEtat);
		initializeTransitions(nbEtat);
	}
	
	private void initializeEpsilonsInitFin (int nbEtat) {
		for (int i=0 ; i<nbEtat ; i++) {
			epsilon_transi.add(new ArrayList<>());
			tab_fin[i]=false;
			tab_init[i]=false;
		}
	}
	
	private void initializeTransitions(int nbEtat) {
		for (int i=0;i<nbEtat;i++) {
			for (int j=0;j<256;j++) {
				transitions[i][j] = -1;
			}
		}
	}

	public Automate(Integer[][] transitions, boolean[] tab_init, boolean[] tab_fin) {
		this.transitions = transitions;
		this.tab_init = tab_init;
		this.tab_fin = tab_fin;
		this.epsilon_transi = null;
		this.einitial = -1;
		this.efinal = -1;
	}
	
	/* le seule endroit ou cette fonction est appelée est dans le constructeur 1 de Automate, qui lui est seulement appelé dans applyBasis
	 * donc pas besoin de savoir cb d'état à prévoir avec le parcours récursif, on sait qu'il y en a que 2 
	 * */
	//valide 
	public int getNbEtat(RegExTree input) {
		
		int cpt_etats = 0;
		if (input.subTrees.isEmpty()) {
			return 2;
		}
		switch(input.root) {
		case RegEx.ETOILE : cpt_etats = cpt_etats + getNbEtat(input.subTrees.get(0));
		default : cpt_etats = 2 + cpt_etats + getNbEtat(input.subTrees.get(0)) + getNbEtat(input.subTrees.get(1));
		}
		return cpt_etats;

	}
	
	// à partir d'une automate 
	public static int getNbEtat2(Automate automate) {
		return automate.transitions.length; // à vérifier que ca retourne bien le nombre de ligne du tableau de transitions 
	}


	public static Automate getAutomate(RegExTree input) {
		switch(input.root) {
			case RegEx.ETOILE : System.out.println("ETOILE");return applyEtoile(input.subTrees.get(0));
			case RegEx.CONCAT : System.out.println("CONCAT");return applyConcat(input.subTrees.get(0),input.subTrees.get(1));
			case RegEx.ALTERN : System.out.println("ALTERN");return applyAltern(input.subTrees.get(0),input.subTrees.get(1));
			default : return applyBasis(input);
		}
	}
	
	
	public static void addEpsilonTransition(ArrayList<ArrayList<Integer>> tab, int src, int dest) {
		//ici modification directement sur ce qui est passé en paramètre 
		tab.get(src).add(dest);
	}

	public void addTransition(int root, int i, int j) {
		transitions[i][root] = j;
	}

	public void setFinal(int i) {
		efinal = i;
		tab_fin[i] = true;
	}

	public void setInitial(int i) {
		einitial = i;
		tab_init[i] = true;
	}
	
	public static Automate applyBasis(RegExTree input) {
		Automate res = new Automate(input);
		res.addTransition(input.root,0,1);
		return res;
	}

	public static Automate applyAltern(RegExTree regExTree, RegExTree regExTree2) {
		Automate gauche = getAutomate(regExTree);
		Automate droite = getAutomate(regExTree2);
		int nbG = getNbEtat2(gauche);
		int nbD = getNbEtat2(droite);
		int newNb = nbG+nbD+2;
		Automate ret = new Automate(newNb);
		
		ret.setInitial(0);
		
		//copie de l'automate gauche 
		for (int i=0;i<nbG;i++) {
			for (int j=0;j<256;j++) {
				if (gauche.transitions[i][j] != -1)
					ret.transitions[i+1][j] = gauche.transitions[i][j]+1;
			}
			ArrayList<Integer> updatedArray = gauche.epsilon_transi.get(i);
			for (int k=0;k<updatedArray.size();k++) {
				updatedArray.set(k, updatedArray.get(k)+1);
			}
			ret.epsilon_transi.set(i+1, updatedArray);
		}
		//copie de l'automate droit -> attention à prendre en compte le décalage
		for (int i=0;i<nbD;i++) {
			for (int j=0;j<256;j++) {
				if (droite.transitions[i][j] != -1)
					ret.transitions[i+nbG+2][j] = droite.transitions[i][j]+nbG+2;
			}
			ArrayList<Integer> updatedArray = droite.epsilon_transi.get(i);
			for (int k=0;k<updatedArray.size();k++) {
				updatedArray.set(k, updatedArray.get(k)+nbG+2);
			}
			ret.epsilon_transi.set(i+nbG+2, updatedArray);
		}
		addEpsilonTransition(ret.epsilon_transi, 0, gauche.einitial+1);
		addEpsilonTransition(ret.epsilon_transi, 0, droite.einitial+nbG+2);
		addEpsilonTransition(ret.epsilon_transi, gauche.efinal+1, nbG+1);
		addEpsilonTransition(ret.epsilon_transi, droite.efinal+nbG+2, nbG+1);

		ret.setFinal(nbG+1);
		return ret;
	}

	public static Automate applyConcat(RegExTree regExTree, RegExTree regExTree2) {
		Automate gauche = getAutomate(regExTree);
		Automate droite = getAutomate(regExTree2);
		int nbG = getNbEtat2(gauche);
		int nbD = getNbEtat2(droite);
		Automate ret = new Automate(nbG+nbD);

		//copie de l'automate gauche 
		for (int i=0;i<nbG;i++) {
			for (int j=0;j<256;j++) {
				ret.transitions[i][j] = gauche.transitions[i][j];
			}
			ret.epsilon_transi.set(i, gauche.epsilon_transi.get(i));
		}
		//copie de l'automate droit -> attention à prendre en compte le décalage
		for (int i=0;i<nbD;i++) {
			for (int j=0;j<256;j++) {
				if(droite.transitions[i][j]!=-1)
					ret.transitions[i+nbG][j] = droite.transitions[i][j]+nbG;
			}
			ArrayList<Integer> updatedArray = droite.epsilon_transi.get(i);
			for (int k=0;k<updatedArray.size();k++) {
				updatedArray.set(k, updatedArray.get(k)+nbG);
			}
			ret.epsilon_transi.set(i+nbG, updatedArray);
		}
		//ajout de l'epsilon-transition de la concaténation
		addEpsilonTransition(ret.epsilon_transi, gauche.efinal, droite.einitial+nbG);
		
		ret.setInitial(0);
		ret.setFinal(nbG+nbD-1);
		return ret;
	}

	public static Automate applyEtoile(RegExTree regExTree) {
		Automate automate = getAutomate(regExTree);
		int newNb = getNbEtat2(automate)+2;
		Automate ret = new Automate(newNb);
		//copie des anciennes transitions
		for (int i=0;i<newNb-2;i++) {
			for (int j=0;j<256;j++) {
				if(automate.transitions[i][j]!=-1)
					ret.transitions[i+1][j] = automate.transitions[i][j]+1;
			}
			ArrayList<Integer> updatedArray = automate.epsilon_transi.get(i);
			for (int k=0;k<updatedArray.size();k++) {
				updatedArray.set(k, updatedArray.get(k)+1);
			}
			ret.epsilon_transi.set(i+1, updatedArray);
		}
		//ajout des nouveaux epsilon-transitions
		addEpsilonTransition(ret.epsilon_transi, 0, automate.einitial+1);
		addEpsilonTransition(ret.epsilon_transi, 0, newNb-1);
		addEpsilonTransition(ret.epsilon_transi, automate.efinal+1, automate.einitial+1);
		addEpsilonTransition(ret.epsilon_transi, automate.efinal+1, newNb-1);
		
		ret.setInitial(0);
		ret.setFinal(newNb-1);
		return ret;
	}
	
	public String toString() {
		String res = "";
		//init & fin
		if (einitial != -1) {
			res += "Etat initial : "+einitial+"\n";
			res += "Etat final : "+efinal+"\n";
		}else{
			res += "Etat initial | Etat final:\n";
			for (int i=0 ; i<tab_init.length ; i++) {
				res += i+"     " + tab_init[i]+"\t" + tab_fin[i] +" \n"; 
			}
		}
		
		//tab des transitions
		res += "Transitions : \n";
		for (int i=0;i<transitions.length;i++) {
			for (int j=0;j<transitions[0].length;j++) {
				if(transitions[i][j] != -1) {
					res += transitions[i][j];
				}else {
					res += ".";
				}
			}
			res += "\n";
		}
		//tab des epsilon-transitions
		if (epsilon_transi == null) return res;
		res += "Epsilon-transitions \n";
		for (int i=0 ; i<epsilon_transi.size() ; i++) {
			ArrayList<Integer> destinations = epsilon_transi.get(i);
			res += i + " -> ";
			for (Integer d : destinations) {
				res += d+ " ";
			}
			res += "\n";
		}
		return res;
	}
	
	
	public static Automate getDeterminisation(Automate auto) {
		ArrayList<HashSet<Integer>> newStates = new ArrayList<>();
		HashSet<Integer>[] reachables = new HashSet[256];
		int[] transi_uneligne = new int[256];
		int cpt = 0;
		boolean done = false;
		ArrayList<int[]> transi = new ArrayList<>();
		boolean[] tab_init;
		boolean[] tab_fin;
		Integer[][] transitions;

		//initialisation de la procédure
		newStates.add(new HashSet<>());
		newStates.get(0).add(0);
		//ajout des etats atteignables suivant epsilon transition depuis etat 0
		ArrayList<Integer> epsilons = auto.epsilon_transi.get(0);
		newStates.get(0).addAll(epsilons);
		
		while (!done) {
			//réinitialisations des outils
			reachables = new HashSet[256];
			Arrays.fill(transi_uneligne, -1);
			//exploration des états atteignables depuis newstates suivant la transition
			for (Integer fromState : newStates.get(cpt)) {
				for (int j=0; j<256; j++) {
					Integer toState = auto.transitions[fromState][j];
					if (toState != -1) {
						if (reachables[j] == null) {
							reachables[j] = new HashSet<>();
						}
						reachables[j].add(toState);
						// recherche des états atteignables en epsilon-transition depuis toState
						reachables[j].addAll(getEpsilonReachables(toState,auto.epsilon_transi,new HashSet<>())); 
					}
				}
			}
			//sur le tableau de determinisation = une ligne de faite
			//ajout des prochaines étapes d'exploration (cad les newStates) et ajout des vrais transitions pour le nouvel automate
			for (int j=0; j<256; j++) {
				if (reachables[j] != null) {
					if (!newStates.contains(reachables[j])) { //si l'état n'existe pas encore dans les newStates
						newStates.add(reachables[j]);
						transi_uneligne[j] = newStates.size()-1;						
					}else{ 
						transi_uneligne[j] = newStates.indexOf(reachables[j]);
					}
				}
			}
			transi.add(cpt, transi_uneligne.clone());
			cpt++;
			if (cpt >= newStates.size()) //il n'y a plus d'état à explorer
				done = true;
		}
		//newStates et transi sont de la même taille normalement 
		transitions = new Integer[transi.size()][256];
		tab_init = new boolean[transi.size()];
		tab_fin = new boolean[transi.size()];
		for (int i=0; i<transi.size();i++) {
			for (int j=0; j<256; j++) {
				transitions[i][j] = transi.get(i)[j];
			}
			//pour les tableaux des états initial et final
			if(newStates.get(i).contains(auto.einitial))
				tab_init[i]=true;
			else
				tab_init[i]=false;
			if(newStates.get(i).contains(auto.efinal))
				tab_fin[i]=true;
			else 
				tab_fin[i]=false;
		}
		Automate ret = new Automate(transitions, tab_init, tab_fin);
		return ret;
	}
	
	public static HashSet<Integer> getEpsilonReachables(int fromState, ArrayList<ArrayList<Integer>> epsilon_transi, HashSet<Integer> existings){
		HashSet<Integer> res = new HashSet<>();
		ArrayList<Integer> dest = epsilon_transi.get(fromState);
		if (dest.isEmpty())
			return res;
		for (Integer d : dest) {
			if (existings.contains(d)) continue;
			res.add(d);
			res.addAll(getEpsilonReachables(d, epsilon_transi, res));
		}
		return res;
	}
	
	public static Automate getMinimisation(Automate automate) {
		Automate pre = preProcess();
		return null;
	}

	public static Automate preProcess() {
		return null;
	}
	
}
