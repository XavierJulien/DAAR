import java.util.ArrayList;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main (String[] args) {
		ArrayList<ArrayList<Integer>> a1 = new ArrayList<>();
		ArrayList<Integer> a2 = new ArrayList<>();
		a2.add(1);
		a2.add(2);
		a2.add(3);
		a1.add(a2);
		ArrayList<Integer> a3 = new ArrayList<>();
		a3.add(1);
		a3.add(2);
		a3.add(3);
		
		System.out.println(a1.contains(a3));
	}
}
