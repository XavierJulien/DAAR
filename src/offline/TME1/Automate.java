package offline.TME1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class Automate {
	
	private Integer[][] transitions;
	private ArrayList<ArrayList<Integer>> epsilon_transi; 
	private boolean[] tab_init ;
	private boolean[] tab_fin; 
	private int einitial;
	private int efinal;
	
	
	public Integer[][] getTransitions() {
		return transitions;
	}
	
	public boolean[] getTab_fin() {
		return tab_fin;
	}
	
	public boolean[] getTab_init() {
		return tab_init;
	}
	
	
	//constructeur d'un automate simple (basis)
	public Automate() {
		this.einitial = 0;
		this.efinal = 1;
		this.transitions = new Integer[2][257]; 
		this.epsilon_transi = new ArrayList<>();
		this.tab_init = new boolean[2];
		this.tab_fin = new boolean[2];
		initializeEpsilonsInitFin(2);
		initializeTransitions(2);
	}
	
	public Automate(int nbEtat) {
		this.transitions = new Integer[nbEtat][257];
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
			for (int j=0;j<257;j++) {
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
			case RegEx.DOT : return applyBasisBis(256);
			case RegEx.ETOILE : return applyEtoile(input.subTrees.get(0));
			case RegEx.CONCAT : return applyConcat(input.subTrees.get(0),input.subTrees.get(1));
			case RegEx.ALTERN : return applyAltern(input.subTrees.get(0),input.subTrees.get(1));
			default : return applyBasis(input);
		}
	}
	
	public static Automate applyBasisBis(int i) {
		Automate res = new Automate();
		res.addTransition(i,0,1);
		return res;
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
		Automate res = new Automate();
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
			for (int j=0;j<257;j++) {
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
			for (int j=0;j<257;j++) {
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
			for (int j=0;j<257;j++) {
				ret.transitions[i][j] = gauche.transitions[i][j];
			}
			ret.epsilon_transi.set(i, gauche.epsilon_transi.get(i));
		}
		//copie de l'automate droit -> attention à prendre en compte le décalage
		for (int i=0;i<nbD;i++) {
			for (int j=0;j<257;j++) {
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
			for (int j=0;j<257;j++) {
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
			res += i+":";
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
	
	public static Automate getDeterminisation2(Automate auto) {
		ArrayList<Integer> importantStates = new ArrayList<>();
		importantStates.add(0);
		for (int i=0; i<auto.transitions.length; i++) {
			for (int j=0; j<257; j++) {
				int dest = auto.transitions[i][j];
				if (dest != -1)
					importantStates.add(dest);
			}
		}
		
		Integer[][] newTransitions = new Integer[importantStates.size()][257];
		boolean[] newInit = new boolean[importantStates.size()];
		boolean[] newFin = new boolean[importantStates.size()];
		Arrays.fill(newInit, false);
		Arrays.fill(newFin, false);
		
		for (int i=0;i<importantStates.size();i++) {
			int fromState = importantStates.get(i);
			HashSet<Integer> rechables = getEpsilonReachables(fromState, auto.epsilon_transi, new HashSet<>());
			//System.out.println("Pour l'état " +fromState+" reachables = "+rechables);
			for (int e : rechables) {
				for(int t=0;t<257;t++) {
					int dest_old = auto.transitions[e][t];
					if (dest_old != -1) {
						//System.out.println(e +" a une transition "+t+" vers "+dest_old );
						//System.out.println("indexof "+dest_old+" = "+importantStates.indexOf(dest_old));
						newTransitions[i][t] = importantStates.indexOf(dest_old);
					}else {
						if (newTransitions[i][t] == null)//jamais affecté encore
							newTransitions[i][t] = -1;
					}
				}
				if (auto.tab_init[e])
					newInit[i] = true;
				if (auto.tab_fin[e])
					newFin[i] = true;
			}
		}
		
		Automate res = new Automate(newTransitions, newInit, newFin);
		
		return res;
	}

	@SuppressWarnings("unchecked")
	public static Automate getDeterminisation(Automate auto) {
		ArrayList<HashSet<Integer>> newStates = new ArrayList<>();
		HashSet<Integer>[] reachables = new HashSet[257];
		int[] transi_uneligne = new int[257];
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
			reachables = new HashSet[257];
			Arrays.fill(transi_uneligne, -1);
			//exploration des états atteignables depuis newstates suivant la transition
			for (Integer fromState : newStates.get(cpt)) {
				for (int j=0; j<257; j++) {
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
			for (int j=0; j<257; j++) {
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
		transitions = new Integer[transi.size()][257];
		tab_init = new boolean[transi.size()];
		tab_fin = new boolean[transi.size()];
		for (int i=0; i<transi.size();i++) {
			for (int j=0; j<257; j++) {
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
		res.add(fromState);
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
		
		Automate pre = preProcess(automate);
		//if (true) return pre;
		Integer[][] transitions = pre.transitions;
		LinkedHashMap<String, ArrayList<Integer>> map_nom_etats = new LinkedHashMap<>();
		
		LinkedHashMap<String, ArrayList<Integer>> map_a_traiter = new LinkedHashMap<>();
		LinkedHashMap<String, ArrayList<Integer>> map_fini = new LinkedHashMap<>();
		
		String[][] ensEtatDestination = new String[pre.transitions.length+1][257];
		String[] ensembles = new String[pre.transitions.length+1];
		
		//initialisation des ensembles
		ArrayList<Integer> ens_etats_non_finaux = new ArrayList<>();
		ArrayList<Integer> ens_etats_finaux = new ArrayList<>();
		for (int i=0;i<transitions.length;i++) {
			if (pre.tab_fin[i]) {
				ensembles[i] = "B";
				ens_etats_finaux.add(i);
			}else { 
				ensembles[i] = "A";
				ens_etats_non_finaux.add(i);
			}
		}
		ensembles[transitions.length] = "A";
		ens_etats_non_finaux.add(transitions.length);
		map_nom_etats.put("A",ens_etats_non_finaux);
		map_nom_etats.put("B",ens_etats_finaux);
		
		
		//calcul des ensembles destinations
		for (int i = 0; i < ensEtatDestination.length; i++) {
			if (i == ensEtatDestination.length-1) {
				for (int j = 0; j < 257; j++)
					ensEtatDestination[i][j] = "A";
				continue;
			}
			for (int j = 0; j < 257; j++) {
				int destination = transitions[i][j];
				if (destination == -1)
					ensEtatDestination[i][j] = "A";
				else
					ensEtatDestination[i][j] = ensembles[destination];
			}
		}
		
		//itération pour atteindre point fixe
		while (!map_nom_etats.isEmpty()) {
			map_a_traiter.clear();
			for (String nom : map_nom_etats.keySet()) {
				ArrayList<Integer> cibles = map_nom_etats.get(nom);
				if (cibles.size() == 1) {//plus de partition possible : singleton
					map_fini.put(nom, cibles);
					continue;
				}
				HashMap<String, ArrayList<Integer>> partition = getPartitions(nom, map_nom_etats.get(nom), ensEtatDestination);
				if (partition.size() == 1) 
					map_fini.putAll(partition);
				else {
					map_a_traiter.putAll(partition);
				}					
			}
			//update des ensembles :
			for (String nom : map_fini.keySet()) {
				map_fini.get(nom).forEach(etat -> ensembles[etat]=nom);
			}
			for (String nom : map_a_traiter.keySet()) {
				map_a_traiter.get(nom).forEach(etat -> ensembles[etat]=nom);
			}
			
			//update des ensEtatsDestination
			for (int i = 0; i < ensEtatDestination.length; i++) {
				if (i == ensEtatDestination.length-1) 
					continue;
				for (int j = 0; j < 257; j++) {
					int destination = transitions[i][j];
					if (destination != -1 )
						ensEtatDestination[i][j] = ensembles[destination];
				}
			}
				
			map_nom_etats.clear();
			map_nom_etats.putAll(map_a_traiter);
		}
		// à la sortie du while le processus de partitionnement est terminé
	
		// calcul de la nouvelle table de transition
		ArrayList<String> newStatesName = new ArrayList<>();
		ArrayList<Integer> newStatesOldNb = new ArrayList<>();
		
		for (String key : map_fini.keySet()) {
			Integer etatGarde = map_fini.get(key).get(0);
			if (etatGarde == transitions.length) continue; 
			newStatesName.add(ensembles[etatGarde]);
			newStatesOldNb.add(etatGarde);
		}
		Integer[][] newTransitions = new Integer[ensEtatDestination.length-1][257];
		boolean[] tab_init = new boolean[newStatesName.size()];
		boolean[] tab_fin = new boolean[newStatesName.size()];
		boolean deadFound = false;
		for (int i=0; i<newStatesName.size(); i++) {
			int oldNb = newStatesOldNb.get(i);
			if (oldNb == transitions.length) {
				deadFound = true;
				continue;
			}
			int etat = i;
			if (deadFound)
				etat--;
			for (int j=0; j<257; j++) {
				newTransitions[etat][j] = newStatesName.indexOf(ensEtatDestination[oldNb][j]);
			}
			
			tab_init[etat] = pre.tab_init[oldNb];
			tab_fin[etat] = pre.tab_fin[oldNb];
		}
		
		//gestion des etats finaux et non finaux done egalement

		Automate res = new Automate(newTransitions, tab_init, tab_fin);
		/*System.out.println("L'automate de preprocess : \n"+pre.toString());
		System.out.println("L'automate de minimisation : \n"+res.toString());*/
		
		return res;
	}

	public static Automate preProcess(Automate automate) {
		Integer[][] transitions = automate.transitions;
		ArrayList<ArrayList<Integer>> newStates = new ArrayList<>();
		
		boolean[] treated = new boolean[transitions.length];
		Arrays.fill(treated, false);
		
		for(int i=0;i<transitions.length;i++) {
			if (treated[i]) continue;
			Integer[] etatA = transitions[i];
			ArrayList<Integer> similar = getSimilarIndex(etatA,transitions,i);
			for (Integer index : similar) {
				treated[index] = true;
			}
			newStates.add(similar);
		}
		
		Integer[][] newTransitions = new Integer[newStates.size()][257];
		boolean[] tab_init = new boolean[newStates.size()];
		boolean[] tab_fin = new boolean[newStates.size()];
 		
		Map<Integer,Integer> map_old_new = new HashMap<>();
		for (int i=0;i<newStates.size();i++) {
			ArrayList<Integer> etatsSimilaires = newStates.get(i);
			for (Integer old : etatsSimilaires) {
				map_old_new.put(old, i);
			}
		}
		
		for (int k=0;k<newStates.size();k++) {
			ArrayList<Integer> etatsSimilaires = newStates.get(k);
			int etatQuiReste = etatsSimilaires.get(0);
			Integer[] newTransi = new Integer[257];
			for (int i=0;i<transitions[0].length;i++) {
				int etat = transitions[etatQuiReste][i];
				if (etat == -1)
					newTransi[i] = -1;
				else
					newTransi[i] = map_old_new.get(etat);
			}
			newTransitions[k] = newTransi;
			tab_init[k] = automate.tab_init[etatQuiReste];
			tab_fin[k] = automate.tab_fin[etatQuiReste];
			
		}
		return new Automate(newTransitions, tab_init, tab_fin);
	}
	
	private static ArrayList<Integer> getSimilarIndex(Integer[] etatA, Integer[][] transitions, int etatCourant) {
		ArrayList<Integer> res = new ArrayList<>();
		res.add(etatCourant);
		for(int i=etatCourant+1;i<transitions.length;i++) {
			Integer[] etatB = transitions[i];
			if (isEqual(etatA, etatB))
				res.add(i);
		}
		return res;
	}

	public static boolean isEqual(Integer[] a, Integer[]b) {
		if (a.length!=b.length) return false;
		
		for(int i=0;i<a.length;i++) {
			if (a[i]!=b[i]) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isEqual(String[] a, String[]b) {
		if (a.length!=b.length) return false;
		
		for(int i=0;i<a.length;i++) {
			if (a[i]!=b[i]) {
				return false;
			}
		}
		return true;
	}
		
	public static HashMap<String, ArrayList<Integer>> getPartitions(String name, ArrayList<Integer> etats, String[][] ensEtatDestination) {
		int nbEtats = etats.size();
		int cpt = 1;
		LinkedHashMap<String, ArrayList<Integer>> res = new LinkedHashMap<>();
		ArrayList<Integer> same = new ArrayList<>();
		boolean[] treated = new boolean[nbEtats];
		Arrays.fill(treated, false);
		for (int i=0; i<nbEtats; i++) {
			same.clear();
			if (!treated[i]) {
				int etat = etats.get(i);
				same.add(etat);
				for (int j=i+1; j< nbEtats; j++) {
					if (!treated[j]) {
						int etatCompare = etats.get(j);
						if(isEqual(ensEtatDestination[etat], ensEtatDestination[etatCompare])) { 
							same.add(etatCompare);
							treated[j] = true;
						}
					}
				}
				treated[i] = true;
			if (same.size() == etats.size()) {//il n'y a pas de partition plus petite
				res.put(name, same);
				return res;
			}
			res.put(name+cpt, new ArrayList<>(same));
			cpt++;
			}
		}
		return res;
	}
	
}
