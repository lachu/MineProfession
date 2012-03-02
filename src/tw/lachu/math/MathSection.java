package tw.lachu.math;

import java.util.ArrayList;
import java.util.Map;

public class MathSection extends MathElement {
	
	
	private int sign = 1;
	private ArrayList<MathElement> elements;
	
	MathSection(){
		elements = new ArrayList<MathElement>();
	}
	
	@Override
	public double value(Map<String, Double> variables) {
		double temp = sign;
		for(MathElement element:elements){
			temp *= element.value(variables);
		}
		return temp;
	}
	
	String parse(String expression){
		if(expression.charAt(0)=='+'){
			expression = expression.substring(1);
			sign = 1;
		}else if(expression.charAt(0)=='-'){
			expression = expression.substring(1);
			sign = -1;
		}else{
			sign = 1;
		}
		
		expression = "*".concat(expression);
		
		while(!expression.isEmpty()){
			if(expression.charAt(0)=='('){
				MathExpression exp = new MathExpression();
				elements.add(exp);
				expression = exp.parse(expression.substring(1)).substring(1);
			}else if("*/".indexOf(expression.charAt(0))!=-1){
				MathToken token = new MathToken();
				elements.add(token);
				expression = token.parse(expression);
			}else{ // +-)
				return expression;
			}
		}
		return "";
	}
}


//(a+b+c)