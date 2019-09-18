import java.util.ArrayList;

public class Automate {
	
	private Integer[][] transitions;
	private ArrayList<ArrayList<Integer>> epsilon_transi; 
	private int einitial;
	private int efinal;
	//private RegExTree input;
	
	//constructeur d'un automate simple (basis)
	public Automate(RegExTree input) {
		/*int nbEtat = getNbEtat(input);
		//this.input = input;
		this.transitions = new Integer[nbEtat][256]; 
		this.epsilon_transi = new ArrayList<>();
		for (int i=0;i<nbEtat;i++) {
			epsilon_transi.add(i, new ArrayList<>());
		}*/
		// autant de ligne que d'état, mais une seule colonne qui contient le tableau des destinations de la epsilon transition
		this.einitial = 0;
		this.efinal = 1;
		this.transitions = new Integer[2][256]; 
		this.epsilon_transi = new ArrayList<>();
		epsilon_transi.add(new ArrayList<>());
		epsilon_transi.add(new ArrayList<>());
		
	}
	
	public Automate(int nbEtat) {
		this.transitions = new Integer[nbEtat][256];
		this.epsilon_transi = new ArrayList<>();
		for (int i=0;i<nbEtat;i++) {
			epsilon_transi.add(i, new ArrayList<>());
		}
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
		System.out.println("root est "+input.root);
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
		
	}

	public void setInitial(int i) {
		einitial = i;
		
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
		
		//copie de l'automate gauche 
		for (int i=0;i<nbG;i++) {
			for (int j=0;j<256;j++) {
				ret.transitions[i+1][j] = gauche.transitions[i][j];
			}
			ArrayList<Integer> updatedArray = droite.epsilon_transi.get(i);
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
		
		ret.setInitial(0);
		ret.setFinal(gauche.efinal+1);
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
		System.out.println("Dans applyEtoile");
		Automate automate = getAutomate(regExTree);
		int newNb = getNbEtat2(automate)+2;
		Automate ret = new Automate(newNb);
		//copie des anciennes transitions
		for (int i=1;i<newNb-2;i++) {
			for (int j=0;j<256;j++) {
				ret.transitions[i][j] = automate.transitions[i-1][j];
			}
			ret.epsilon_transi.set(i, automate.epsilon_transi.get(i-1));
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
					res += "X";
				}
			}
			res += "\n";
		}
		res += "etat initial : "+einitial+"\n";
		res += "etat final : "+efinal+"\n";
		return res;
	}
	
}
