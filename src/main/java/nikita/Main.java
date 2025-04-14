package nikita;

import nikita.math.construct.expression.Expression;

public class Main {
	public static void main(String[] args) {
		Expression expression = new Expression(
				"(x^3 + sin(x))/(x + 1) + e^x/(ln(x + 2) + 3) + cos(x)/(x^2 + 3.14159) + sqrt(x - 1)/(e + x^2) + arctan(x)/(ln(x + 3.14159) + e) + ((x + 5)^2)/(sin(x) + cos(x) + 1) + (exp(-x^2) + 1)/(sqrt(x^2 + 1) + ln(x + e) + 2)");

		System.out.println(expression.getDenominators());
	}
}
