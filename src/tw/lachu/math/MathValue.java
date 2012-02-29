package tw.lachu.math;

import java.util.Map;

public class MathValue extends MathElement {
	
	String value;
	
	MathValue(){
		
	}

	@Override
	double value(Map<String, Double> variables) {
		if(value.equals("Math.PI")){
			return Math.PI;
		}else if(value.equals("Math.E")){
			return Math.E;
		}else if("1234567890".indexOf(value.charAt(0))!=-1){
			return Double.valueOf(value);
		}else if(variables.get(value)!=null){
			return variables.get(value);
		}else{
			return 0;
		}
	}

	@Override
	String parse(String expression) {
		if("1234567890".indexOf(expression.charAt(0))!=-1){
			value = expression.split("[^0-9.]+")[0];
			expression = expression.substring(value.length());
		}else{
			value = expression.split("[^A-Za-z.]")[0];
			expression = expression.substring(value.length());
		}
		return expression;
	}

}
