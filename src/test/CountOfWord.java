package test;

import java.util.Scanner;

public class CountOfWord {

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		System.out.println("please input a sentance:");
		String sentance = sc.nextLine();
		int count = countWord(sentance);
		System.out.println("the sentance contains " + count + " word");
	}
	
	public static int countWord(String sentance) {
		int count = 0;
		String[] words = sentance.split(" ");		
		for(String s : words) {
			if(!s.equals("")) {
				count++;
			}
		}
		return count;
	}
}
