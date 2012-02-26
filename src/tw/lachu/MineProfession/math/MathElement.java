package tw.lachu.MineProfession.math;

import java.util.Map;

public abstract class MathElement {
	abstract double value(Map<String, Double> variables);
	abstract String parse(String expression);
}
