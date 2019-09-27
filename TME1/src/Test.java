import java.util.ArrayList;
import java.util.HashSet;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main (String[] args) {
		HashSet<HashSet<Integer>> a1 = new HashSet<>();
		HashSet<Integer> a2 = new HashSet<>();
		a2.add(1);
		a2.add(3);
		a2.add(2);
		a1.add(a2);
		HashSet<Integer> a3 = new HashSet<>();
		a3.add(1);
		a3.add(2);
		a3.add(3);
		
		System.out.println(a1.contains(a3));
	}
}
