package tw.lachu.MineProfession.util;

public class Chance {
	public static boolean happen(double probability){
		return Math.random() <= probability;
	}
	
	public static int contribute(double probability, int max){
		double rand = Math.random();
		if(rand > max*probability){
			return 0;
		}
		return ((int)(rand/probability))+1;
	}
}
