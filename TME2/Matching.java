import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;

public class Matching{

    public int matchingAlgo (char[] facteur, int[] retenue, char[] texte){
        int i = 0,j = 0;
        while(i<texte.length){
            System.out.println("print "+texte[i]);
            if(j==facteur.length){
                return i-facteur.length;
            }
            if(texte[i] == facteur[j]){
                i++;
                j++;
            }else{
                if(retenue[j]==-1){
                    i++;
                    j=0;
                }else{
                    j=retenue[j];
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
                ArrayList<String> list_suffix= getSuffixList(Arrays.copyOfRange(facteur,0,i)); //i est exclue
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