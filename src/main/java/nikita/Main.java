package nikita;

import java.math.BigDecimal;

import nikita.math.construct.Point;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.expression.ExpressionSystem;
import nikita.math.solver.root.system.SimpleIterationsSystemRootFinder;

public class Main {
	public static void main(String[] args) {
//		Precision precision = new Precision("0.0000000001");
//		Expression expression = new Expression("(1/3)*x^3-5*x+x*ln(x)");
//		Interval interval = new Interval(new BigDecimal("3"), new BigDecimal("4"));

//		Precision precision = new Precision("0.0001");
//		Expression expression = new Expression("x^3-x+4");
//		Interval interval = new Interval(new BigDecimal("-2"), new BigDecimal("-1"));

//		Precision precision = new Precision("0.000001");
//		Expression expression = new Expression("x^3-2*x^2+0.5*x^4");
//		Interval interval = new Interval(new BigDecimal("-4"), new BigDecimal("-3"));

//		Precision precision = new Precision("0.00001");
//		Expression expression = new Expression("sin((x^2)/3)*x + 18.5");
//		Interval interval = new Interval(new BigDecimal("-19.95"), new BigDecimal("-19.05"));

//		Precision precision = new Precision("0.000001");
//		Expression expression = new Expression("(1/3)*x^3-5*x+x*ln(x)");
//		Interval interval = new Interval(new BigDecimal("1.5"), new BigDecimal("2"));

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

		Precision precision = new Precision("0.00000001");
		Expression expression1 = new Expression("sin(x+y)-1.5*x+0.1");
		Expression expression2 = new Expression("x^2+2*y^2-1");
		ExpressionSystem system = new ExpressionSystem(expression1, expression2);
		Point initialApproximation = new Point(BigDecimal.valueOf(-0.5), BigDecimal.valueOf(-0.6));

		System.out.println(SimpleIterationsSystemRootFinder.find(system, initialApproximation, precision));
	}
}
