package tw.lachu.MineProfession.util;

public class Chance {
	public static boolean happen(double probability){
		return Math.random() <= probability;
	}
	
	public static int contribute(double probability, int max){
		double rand = Math.random();
		for(int i=1;i<=max;++i){
			if((rand -= (max+1-i)*probability)<0){
				return i;
			}
		}
		return 0;
	}
	
}
