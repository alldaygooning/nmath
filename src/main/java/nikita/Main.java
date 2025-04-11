package nikita;

import java.math.BigDecimal;

import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.calculus.Integral;
import nikita.math.construct.expression.Expression;
import nikita.math.solver.integrate.Integrator;

public class Main {
	public static void main(String[] args) {
//		Precision precision = new Precision("0.0000000001");
//		Expression expression = new Expression("(1/3)*x^3-5*x+x*ln(x)");
//		Interval interval = new Interval(new BigDecimal("3"), new BigDecimal("4"));

//		Precision precision = new Precision("0.0001");
//		Expression expression = new Expression("log(15*x)^2+x^2/(17*x)-10");
//		Interval interval = new Interval(new BigDecimal("0.1"), new BigDecimal("20"));

//		Precision precision = new Precision("0.000001");
//		Expression expression = new Expression("sqrt(3*x)^2");
//		Interval interval = new Interval(new BigDecimal("0"), new BigDecimal("1"));

//		Precision precision = new Precision("0.00001");
//		Expression expression = new Expression("sin((x^2)/3)*x + 18.5");
//		Interval interval = new Interval(new BigDecimal("-19.95"), new BigDecimal("-19.05"));

//		Precision precision = new Precision("0.000000000000000000000000000000000000000000000001");
//		Expression expression = new Expression("sin(x^2)/(3*x)");
//		Interval interval = new Interval(new BigDecimal("-4"), new BigDecimal("-3.8"));

//		NLogger.info(String.format("Searching for roots of %s on %s with %s", expression, interval, precision));

//		int estimatedRootsNumber = SingleRootFinder.estimateRootsNumber(expression, interval, precision);
//		System.out.println(estimatedRootsNumber);
//
//		NLogger.info("-----Simple Iterations Algorithm-----");
//		System.out.println(SimpleIterationsRootFinder.find(expression, interval, precision));
//
//		NLogger.info("-----Chord Algorithm-----");
//		System.out.println(ChordRootFinder.find(expression, interval, precision));
//
//		NLogger.info("-----Newton Algorithm-----");
//		System.out.println(NewtonRootFinder.find(expression, interval, precision));

//		Precision precision = new Precision("0.00000001");
//		Expression expression1 = new Expression("0.1*x^2+x+0.2*y^2-0.3");
//		Expression expression2 = new Expression("0.2*x^2+y+0.1*x*y-0.7");
//		ExpressionSystem system = new ExpressionSystem(expression1, expression2);
//		Point initialApproximation = new Point(BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.7));
//
//		Precision precision = new Precision("0.0000000");
//		Expression expression1 = new Expression("sqrt(x)+y");
//		Expression expression2 = new Expression("x+y^2");
//		ExpressionSystem system = new ExpressionSystem(expression1, expression2);
//		Point initialApproximation = new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(0));
//
//		System.out.println(SimpleIterationsSystemRootFinder.searchIterations(system, initialApproximation, precision));

//		Expression expression = new Expression("sqrt(3*x)");
//		Interval interval = new Interval(BigDecimal.valueOf(0), BigDecimal.valueOf(1));
//		Precision precision = new Precision("0.0001");

//		System.out.println("ROOTS COUNT: " + SingleRootFinder.estimateRootsNumber(expression, interval, precision));

//		System.out.println(NewtonRootFinder.find(expression, interval, precision));
//		System.out.println(SimpleIterationsRootFinder.find(expression, interval, precision));
//		System.out.println(ChordRootFinder.find(expression, interval, precision));

		Expression expression = new Expression("2*x^3-3*x^2+5*x-9");
		Integral integral = new Integral(expression, new Variable("x"), BigDecimal.valueOf(1), BigDecimal.valueOf(2));
		Precision precision = new Precision("0.00000000000001");
		int n = 10;

		Integrator.evaluate(integral, n, precision, "simpson");
		
//		Expression expression = new Expression(
//				"(x^3 + sin(x))/(x + 1) + e^x/(ln(x + 2) + 3) + cos(x)/(x^2 + 3.14159) + sqrt(x - 1)/(e + x^2) + arctan(x)/(ln(x + 3.14159) + e) + ((x + 5)^2)/(sin(x) + cos(x) + 1) + (exp(-x^2) + 1)/(sqrt(x^2 + 1) + ln(x + e) + 2)");
//		expression.singularities();

	}
}
