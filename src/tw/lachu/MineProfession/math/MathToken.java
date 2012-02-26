package tw.lachu.MineProfession.math;

import java.util.Map;

public class MathToken extends MathElement {

	boolean divide;
	MathElement base;
	MathElement time;
	
	MathToken(){
		
	}
	
	@Override
	public double value(Map<String, Double> variables) {
		double temp = base.value(variables);
		if(time != null){
			temp = Math.exp(Math.log(temp)*time.value(variables));
		}
		return ((divide)?(1/temp):(temp));
	}

	@Override
	String parse(String expression) {
		//System.out.println(this.getClass().toString()+" "+expression);
		if(expression.charAt(0)=='*'){
			divide = false;
			expression = expression.substring(1);
		}else if(expression.charAt(0)=='/'){
			divide = true;
			expression = expression.substring(1);
		}else{
			divide = false;
		}
		
		if(expression.charAt(0)=='('){
			base = new MathExpression();
			expression = base.parse(expression.substring(1)).substring(1);
		}else if(expression.matches("Math\\.[A-Za-z]*\\(.*")){
			base = new MathFunction();
			expression = base.parse(expression);
		}else{
			base = new MathValue();
			expression = base.parse(expression);
		}
		
		if(!expression.isEmpty() && expression.charAt(0)=='^'){
			expression = expression.substring(1);
			if(expression.charAt(0)=='('){
				time = new MathExpression();
				expression = time.parse(expression.substring(1)).substring(1);
			}else if(expression.matches("Math\\.[A-Za-z]*\\(.*")){
				time = new MathFunction();
				expression = time.parse(expression);
			}else{
				time = new MathValue();
				expression = time.parse(expression);
			}
		}
		
		return expression;
	}

}
