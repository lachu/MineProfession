package tw.lachu.MineProfession.math;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

public class MathFunction extends MathElement {
	
	private String funcName;
	private ArrayList<MathExpression> arguments;
	
	MathFunction(){
		
	}

	@Override
	double value(Map<String, Double> variables) {
		ArrayList<Class<?>> clazz = new ArrayList<Class<?>>();
		ArrayList<Double> values = new ArrayList<Double>();
		for(MathExpression exp : arguments){
			clazz.add(double.class);
			values.add(exp.value(variables));
		}
		Double value;
		try {
			Method method = Math.class.getMethod(funcName, clazz.toArray(new Class[]{}));
			value = (Double)method.invoke(null, values.toArray());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return 0;
		} catch (SecurityException e) {
			e.printStackTrace();
			return 0;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return 0;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return 0;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return 0;
		}
		return value;
	}

	@Override
	String parse(String expression) {
		funcName = expression.substring(expression.indexOf('.')+1, expression.indexOf('('));
		arguments = new ArrayList<MathExpression>();
		expression = expression.substring(expression.indexOf('('));
		while(expression.charAt(0)!=')'){
			MathExpression exp = new MathExpression();
			arguments.add(exp);
			expression = exp.parse(expression.substring(1));
		}
		return expression.substring(1);
	}

}
