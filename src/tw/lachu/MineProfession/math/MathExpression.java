package tw.lachu.MineProfession.math;

import java.util.ArrayList;
import java.util.Map;

public class MathExpression extends MathElement {

	private ArrayList<MathSection> sections;
	
	MathExpression(){
		sections = new ArrayList<MathSection>();
	}
	
	@Override
	public double value(Map<String, Double> variables) {
		double temp = 0;
		for(MathSection section:sections){
			temp += section.value(variables);
		}
		return temp;
	}
	
	String parse(String expression){
		//System.out.println(this.getClass().toString()+" "+expression);
		if(expression.charAt(0)=='+'){
			expression = expression.substring(1);
		}
		
		while(!expression.isEmpty()){
			
			if("),".indexOf(expression.charAt(0)) != -1){
				return expression;
			}
			
			MathSection section = new MathSection();
			sections.add(section);
			expression = section.parse(expression);
		}
		return "";
		
	}

}
