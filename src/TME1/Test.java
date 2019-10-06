package TME1;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main (String[] args) {
		int[] a = {1,2,3,4,5};
		int[] b = {1,2,3,4,5};
		System.out.println(a.equals(b));
		
		for(int i=0;i<a.length;i++) {
			if (a[i]!=b[i]) {
				return false;
			}
		}
		return true;
	}
}
