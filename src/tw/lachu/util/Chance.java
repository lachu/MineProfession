package tw.lachu.util;

public class Chance {
	public static boolean happen(double probability){
		return Math.random() <= probability;
	}
	
	public static enum ContributeType{
		LOWER_AMOUNT_HIGHER_CHANCE,
		CLOSET_TO_EXPECT;
	}
	
	public static int contribute(double expect, ContributeType type){
		if(type==ContributeType.LOWER_AMOUNT_HIGHER_CHANCE){
			int max = (int)(Math.ceil(expect*3)+0.01);
			double probability = expect*6/max/(max+1)/(max+2);
			double rand = Math.random();
			for(int i=1;i<=max;++i){
				if((rand -= (max+1-i)*probability)<0){
					return i;
				}
			}
		}
		if(type==ContributeType.CLOSET_TO_EXPECT){
			int min = (int)(expect);
			double probability = expect-min;
			return min+(happen(probability)?1:0);
		}
		return 0;
	}
	
}
