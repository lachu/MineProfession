package tw.lachu.MineProfession.util;

public class DoubleValue {
	public static double bind(double value, double lowerbound, double upperbound){
		if(value<lowerbound){
			value = lowerbound;
		}
		if(value>upperbound){
			value = upperbound;
		}
		return value;
	}
}
