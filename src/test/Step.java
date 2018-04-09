package test;

public class Step {
	public static void main(String[] args) {
		for (int i = 7; i < 2 * 3 * 4 * 5 * 6 * 7; i++) {
			if (i % 2 == 1 && i % 3 == 2 && i % 4 == 3 && i % 5 == 4 && i % 6 == 5 && i % 7 == 0) {
				System.out.println("the steps : " + i);
				break;
			}
		}
	}
}
