package offline.TME1;

public class Test {

	static final int BACKSLASH_ASCII = 92;
	
	public static int chooseAlgo(String facteur) {
		int algo = 3;//par defaut
		char[] facteur_array = facteur.toCharArray();
		for(int i = 0;i< facteur_array.length;i++) {
			char c = facteur_array[i];
			if(c == '*' || c == '|' || c == '(' || c == ')' || c == '.') {
				if (i != 0) {
					if(facteur_array[i-1] == ((char)BACKSLASH_ASCII)) {
						algo = 2;
					}else {						
						return 3;
					}
				}else {
					return 3;
				}
			}
		}
		if(facteur.matches("[a-zA-Z-']*")) {
			if(facteur.length() <=2) {
				algo = 2;
			}else {
				algo = 1;				
			}
		}
		return algo;
	}
	
	public static void main (String[] args) {
		int[] a = {1,2,3,4,5};
		int[] b = {1,2,3,4,5};
		System.out.println(a.equals(b));
		
		String algo1 = "Salut";
		String algo2 = "Sa\\.\\(u\\)t";
		String algo3 = "S(a|h)*lut";
		
		System.out.println(chooseAlgo(algo1));
		System.out.println(chooseAlgo(algo2));
		System.out.println(chooseAlgo(algo3));
	}
}
