package TME2;

import java.util.ArrayList;
import java.util.Arrays;

public class Matching{

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

    public int[] genRetenue(char[] facteur){
        char firstletter = facteur[0];
        int[] retenue = new int[facteur.length+1];
        retenue[facteur.length] = 0;
        for(int i = 0;i<facteur.length;i++){
            if(firstletter==facteur[i]){
                retenue[i] = -1;
            }else{
                char temp = facteur[i];
                ArrayList<String> list_suffix= getSuffixList(Arrays.copyOfRange(facteur,0,i)); //i est exclu
                ArrayList<String> list_proper_prefix= getPrefixList(Arrays.copyOfRange(facteur,0,i));
                String s = compareList(list_suffix,list_proper_prefix);
                if (facteur[s.length()]==temp)
                    retenue[i] = retenue[s.length()];
                else
                    retenue[i] = s.length();
            }
        }
        return retenue;
    }


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
        for(int i = list1.size()-1;i>0;i--){
            if(list1.get(i).equals(list2.get(i))){
                return list1.get(i);
            }
        }
        return "";
    }

}