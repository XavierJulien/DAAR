package TME2;

import java.util.ArrayList;
import java.util.Arrays;

public class Matching{

	/**
	 * Algorithme de matching d'un mot sur un texte
	 * @param facteur mot qu'on souhaite chercher
	 * @param retenue tableau de retenue pour chaque lettre du facteur
	 * @param texte texte sur lequel on applique l'algorithme
	 * @return position de la première charactère du premier mot que l'on match dans le texte
	 */
    public int matchingAlgo (char[] facteur, int[] retenue, char[] texte){
        int courant_line = 0,courant_facteur = 0;
        while(courant_line<=texte.length){
            if(courant_facteur==facteur.length){
                return courant_line-facteur.length;
            }
            if(texte[courant_line] == facteur[courant_facteur]){
                courant_line++;
                courant_facteur++;
            }else{
                if(retenue[courant_facteur]==-1){
                    courant_line++;
                    courant_facteur=0;
                }else{
                    courant_facteur=retenue[courant_facteur];
                }
            }
        }
        return -1;
    }

    /**
     * Méthode générant un facteur pour un mot donné
     * @param facteur mot représenté sous forme de tableau de charactère
     * @return tableau représentant la retenue
     */
    public int[] genRetenue(char[] facteur){
        char firstletter = facteur[0];
        int[] retenue = new int[facteur.length+1];
        retenue[facteur.length] = 0;
        for(int i = 0;i<facteur.length;i++){
            if(firstletter==facteur[i]){ // pour chaque lettres égales à notre première lettre et mettons -1 dans sa case
                retenue[i] = -1;
            }else{ // sinon , on cherche le suffix et prefix le plus long commun et on ajoute sa longueur dans la case de la lettre courante
                char temp = facteur[i];
                ArrayList<String> list_suffix= getSuffixList(Arrays.copyOfRange(facteur,0,i)); //i est exclu
                ArrayList<String> list_prefix= getPrefixList(Arrays.copyOfRange(facteur,0,i));
                String s = compareList(list_suffix,list_prefix);
                retenue[i] = (facteur[s.length()]==temp)?retenue[s.length()] : s.length();
            }
        }
        return retenue;
    }

    
    //*********************************************
    //*				   AUXILLARY	     		  *
    //*********************************************
    public ArrayList<String> getPrefixList(char[] facteur){
        ArrayList<String> list_prefix = new ArrayList<String>();
        String s = ""; 
        for(int i = 0;i<facteur.length-1;i++){
            s+=facteur[i];
            list_prefix.add(s);
        }
        return list_prefix;
    }
    
    public ArrayList<String> getSuffixList(char[] facteur){
        ArrayList<String> list_suffix = new ArrayList<String>();
        String s = ""; 
        for(int i = facteur.length-1;i>=1;i--){
            s=facteur[i]+s;
            list_suffix.add(s);
        }
        return list_suffix;
    }
    
    public String compareList(ArrayList<String> list1, ArrayList<String> list2){
        for(int i = list1.size()-1;i>0;i--)
            if(list1.get(i).equals(list2.get(i))) return list1.get(i);
        return "";
    }

}