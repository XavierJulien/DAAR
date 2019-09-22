import java.util.ArrayList;

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
		for (int i=0 ; i<2 ; i++) {
			epsilon_transi.add(new ArrayList<>());
			tab_fin[i]=false;
			tab_init[i]=false;
		}
	}
	
	public Automate(int nbEtat) {
		this.transitions = new Integer[nbEtat][256];
		this.epsilon_transi = new ArrayList<>();
		this.tab_init = new boolean[nbEtat];
		this.tab_fin = new boolean[nbEtat];
		for (int i=0;i<nbEtat;i++) {
			epsilon_transi.add(i, new ArrayList<>());
			tab_init[i]=false;
			tab_fin[i]=false;
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
				if (gauche.transitions[i][j] != null)
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
				if (droite.transitions[i][j] != null)
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
		addEpsilonTransition(ret.epsilon_transi, gauche.efinal+1, newNb-1);
		addEpsilonTransition(ret.epsilon_transi, droite.efinal+nbG+2, newNb-1);

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
				if(droite.transitions[i][j]!=null)
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
				if(automate.transitions[i][j]!=null)
					ret.transitions[i+1][j] = automate.transitions[i][j]+1;
			}
			ArrayList<Integer> updatedArray = automate.epsilon_transi.get(i);
			for (int k=0;k<updatedArray.size();k++) {
				updatedArray.set(k, updatedArray.get(k)+1);
			}
			ret.epsilon_transi.set(i+1, updatedArray);
		}
		//ajout des nouveaux epsilon-transitions
		addEpsilonTransition(ret.epsilon_transi, 0, automate.einitial);
		addEpsilonTransition(ret.epsilon_transi, 0, newNb-1);
		addEpsilonTransition(ret.epsilon_transi, automate.efinal, automate.einitial);
		addEpsilonTransition(ret.epsilon_transi, automate.efinal, newNb-1);
		
		ret.setInitial(0);
		ret.setFinal(newNb-1);
		return ret;
	}
	
	public String toString() {
		String res = "";
		for (int i=0;i<transitions.length;i++) {
			for (int j=0;j<transitions[0].length;j++) {
				if(transitions[i][j] != null) {
					res += transitions[i][j];
				}else {
					res += ".";
				}
			}
			res += "\n";
		}
		res += "etat initial : "+einitial+"\n";
		res += "etat final : "+efinal+"\n";
		return res;
	}
	
	public Automate getDeterminisation(Automate auto) {
		ArrayList<ArrayList<Integer>> newStates = new ArrayList<>();
		ArrayList<Integer>[] suites = new ArrayList[256];
		int cpt = 0;
		boolean done = false;
		ArrayList<int[]> transi = new ArrayList<>();
		boolean[] tab_init;
		boolean[] tab_fin;
		Integer[][] transitions;
		/**
				private Integer[][] transitions;
		private ArrayList<ArrayList<Integer>> epsilon_transi; 
		private boolean[] tab_init ;
		private boolean[] tab_fin; 
		private int einitial;
		private int efinal;
		**/
		//initialisation de la procédure
		newStates.add(new ArrayList<>());
		newStates.get(0).add(0);

		while (!done) {
			//ajout des etats atteignables suivant epsilon transition depuis etat cpt
			ArrayList<Integer> epsilons = auto.epsilon_transi.get(cpt);
			newStates.get(cpt).addAll(epsilons);
			//exploration des états atteignables depuis newstates suivant la transition
			for (Integer fromState : newStates.get(cpt)) {
				for (int j=0; j<256; j++) {
					Integer toState = auto.transitions[fromState][j];
					if (toState != null)
						if (suites[j] == null)
							suites[j] = new ArrayList<>();
						suites[j].add(toState);
				}
				//ajout des prochaines étapes d'exploration (cad les newStates) et ajout des transitions 
				int[] transi_uneligne = new int[256];
				for (int j=0; j<256; j++) {
					if (suites[j] != null) {
						//ajout seulement si l'état n'existe pas déjà dans les newStates
						if (!newStates.contains(suites[j])) 
							newStates.add(suites[j]);
						transi_uneligne[j]=newStates.indexOf(suites[j]);
					}
				}
				transi.add(cpt, transi_uneligne);
			}
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
	
}
