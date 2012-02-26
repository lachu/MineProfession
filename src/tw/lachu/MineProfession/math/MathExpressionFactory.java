package tw.lachu.MineProfession.math;

public class MathExpressionFactory {
	public MathExpressionFactory(){
		
	}
	
	public MathExpression parse(String expression){
		MathExpression exp = new MathExpression();
		exp.parse(expression.replaceAll("\\s", ""));
		return exp;
	}
}
