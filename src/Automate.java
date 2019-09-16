
public class Automate {
	
	private Integer[][] transitions;
	private Integer[][] epsilon_transi; 
	private int einitial = -1;
	private int efinal = -1;
	private RegExTree input;
	
	public Automate(RegExTree input) {
		int nbetats = getNbEtats(input);
		this.input = input;
		this.transitions = new Integer[nbetats][256]; 
		this.epsilon_transi = new Integer[nbetats][1];
	}
	
	//valide 
	private int getNbEtats(RegExTree input) {
		int cpt_etats = 0;
		if (input.subTrees.isEmpty()) {
			return 2;
		}
		switch(input.root) {
		case RegEx.ETOILE : cpt_etats = cpt_etats + getNbEtats(input.subTrees.get(0));
		default : cpt_etats = 2 + cpt_etats + getNbEtats(input.subTrees.get(0)) + getNbEtats(input.subTrees.get(1));
		}
		return cpt_etats;
	}

	
	public Automate getAutomate(RegExTree input) {
		int nbetats = getNbEtats(input);
		autom = new Integer[nbetats][256];
		for(int i = 0;i < nbetats;i++) {
			for(int j = 0; j < nbetats;j++) {
				autom[i][j] = -1;
			}
		}
		switch(input.root) {
		case RegEx.ETOILE : return applyEtoile(input.subTrees.get(0));
		case RegEx.CONCAT : return applyConcat(input.subTrees.get(0),input.subTrees.get(1));
		case RegEx.ALTERN : return applyAltern(input.subTrees.get(0),input.subTrees.get(1));
		default : return applyBasis(input);
		}
		//Automate res = getAutomate()
	}
	

	public Automate applyBasis(RegExTree input) {
		Automate res = new Automate(input);
		res.setInitial(0);
		res.setFinal(1);
		res.addTransition(input.root,0,1);
		return res;
	}
	
	public void addEpsilonTransition(int src, int dest) {
		epsilon_transi[src][]
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

	public Automate applyAltern(RegExTree regExTree, RegExTree regExTree2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Automate applyConcat(RegExTree regExTree, RegExTree regExTree2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Automate applyEtoile(RegExTree regExTree) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
