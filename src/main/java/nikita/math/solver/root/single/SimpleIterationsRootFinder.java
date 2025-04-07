package nikita.math.solver.root.single;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.matheclipse.core.interfaces.IExpr;

import nikita.logging.NLogger;
import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.extremum.Extremum;
import nikita.math.construct.extremum.Maximum;
import nikita.math.construct.extremum.Minimum;

public class SimpleIterationsRootFinder extends SingleRootFinder {

	static final int extraPrecision = 5;

	static final String prefix = "SimpleIterationsSingleRootFinder";
	static final String tableTemplate = "%-8s | %-15s | %-15s | %-15s | %-15s";
	static final String header = String.format(tableTemplate, "Iteration", "x_i", "x_i+1", "f(x_i+1)", "|x_i+1 - x_1|");

	public static BigDecimal find(Expression expression, Interval interval, Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue() + extraPrecision, RoundingMode.HALF_UP);
		Precision adjustedPrecision = new Precision(precision.getString());
		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(extraPrecision)));

		BigDecimal lambda = findLambda(expression, interval, adjustedPrecision);
		Expression relaxedExpression = relax(expression, lambda);
		info(String.format("Relaxed expression form: %s", relaxedExpression));

		BigDecimal x = findInitial(expression, interval, adjustedPrecision);
		info(String.format("Initial approximation x0 = %s", x.toPlainString()));

		logHeader();
		int iteration = 0;
		while (true) {
			iteration++;
			BigDecimal xnew = NMath.getBigDecimal(NMath.replaceAll(relaxedExpression, x.toPlainString()), adjustedPrecision);
			BigDecimal diff = xnew.subtract(x, mathContext);
			BigDecimal ynew = NMath.getBigDecimal(NMath.replaceAll(expression, xnew.toPlainString()), adjustedPrecision);
			logStep(iteration, x, xnew, ynew, diff);

			if (diff.abs().compareTo(precision.getAccuracy()) < 0) {
				x = xnew;
				break;
			}

			x = xnew;
		}

		mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);
		return new BigDecimal(x.toPlainString(), mathContext);
	}

	private static Expression relax(Expression expression, BigDecimal lambda) {
		Expression relaxedExpression = new Expression(String.format("x + a*(%s)", expression));
		IExpr replacedRelaxedExpression = NMath.replaceAll(relaxedExpression, "a", lambda.toPlainString());
		return new Expression(replacedRelaxedExpression.toString());
	}

	private static BigDecimal findLambda(Expression expression, Interval interval, Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);
		Expression derivative = expression.derivative("x");

		Maximum maximum = NMath.maximum(derivative, interval, "x", precision);
		Minimum minimum = NMath.minimum(derivative, interval, "x", precision);
		info(String.format("Found extreme values: %s %s", minimum, maximum));
		Extremum extremum;

		if (maximum.getY().abs().compareTo(minimum.getY().abs()) > 0) {
			extremum = maximum;
		} else {
			extremum = minimum;
		}

		info(String.format("Absolute maximum value: [%s; %s]", extremum.getX(), extremum.getY()));

		BigDecimal lambda = BigDecimal.ONE.divide(extremum.getY().abs(), mathContext);

		if (extremum.getY().compareTo(BigDecimal.ZERO) > 0) {
			lambda = lambda.multiply(BigDecimal.valueOf(-1), mathContext);
		}
		return lambda;
	}

	private static BigDecimal findInitial(Expression expression, Interval interval, Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);
		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();

		Expression firstDerivative = expression.derivative("x");
		Expression secondDerivative = firstDerivative.derivative("x");

		BigDecimal fLeft = NMath.getBigDecimal(NMath.replaceAll(expression, left.toPlainString()), precision);
		BigDecimal ddfLeft = NMath.getBigDecimal(NMath.replaceAll(secondDerivative, left.toPlainString()), precision);

		if (fLeft.multiply(ddfLeft, mathContext).compareTo(BigDecimal.ZERO) > 0) {
			return left;
		}
		return right;
	}

	// -----LOGGING----- //
	private static void info(String message) {
		NLogger.info(String.format("%s: %s", prefix, message));
	}

	private static void logHeader() {
		info(header);
	}

	private static void logStep(int iteration, BigDecimal xi, BigDecimal xi1, BigDecimal fxi1, BigDecimal diff) {
		info(String.format(tableTemplate, iteration, xi, xi1, fxi1, diff));
	}
}
