import java.util.Arrays;

public class TestMatching {
    public static void main(String[] args){
        char[] texte = {' ','m','a','m','a','m','i','a',' ','m','i','m','i','m','o','m','a','m','m','a'};
        char[] mamamia = {'m','a','m','a','m','i','a'};
        char[] chichicha = {'c','h','i','c','h','i','c','h','a'};
        Matching m = new Matching();
        int[] retenue_mamamia = m.genRetenue(mamamia);
        int[] retenue_chichicha = m.genRetenue(chichicha);
        System.out.println("Mamamia: "+Arrays.toString(retenue_mamamia));
        System.out.println("Chichicha: "+Arrays.toString(retenue_chichicha));
        System.out.println("Final Mamamia: "+ m.matchingAlgo(mamamia,retenue_mamamia,texte));
        System.out.println("Final Chichicha: "+ m.matchingAlgo(chichicha,retenue_chichicha,texte));
    }
}