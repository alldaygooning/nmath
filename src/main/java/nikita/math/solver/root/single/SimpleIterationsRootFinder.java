package nikita.math.solver.root.single;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.matheclipse.core.interfaces.IExpr;

import nikita.logging.NLogger;
import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.extremum.Extremum;
import nikita.math.construct.extremum.Maximum;
import nikita.math.construct.extremum.Minimum;
import nikita.math.exception.construct.expression.ExpressionConversionException;
import nikita.math.exception.construct.root.InitialApproximationException;
import nikita.math.exception.construct.root.InterruptedSearchException;

public class SimpleIterationsRootFinder extends SingleRootFinder {

	static final int EXTRA_PRECISION = 5;

	static final String CLASS_PREFIX = "SimpleIterationsSingleRootFinder";
	static final String TABLE_TEMPLATE = "%-8s | %-15s | %-15s | %-15s | %-15s";
	static final String TABLE_HEADER = String.format(TABLE_TEMPLATE, "Iteration", "x_i", "x_i+1", "f(x_i+1)", "|x_i+1 - x_1|");

	public static BigDecimal find(Expression expression, Interval interval, Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue() + EXTRA_PRECISION, RoundingMode.HALF_UP);
		Precision adjustedPrecision = new Precision(precision.getString());
		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(EXTRA_PRECISION)));

		BigDecimal x = findInitial(expression, interval, adjustedPrecision);
		info(String.format("Initial approximation x0 = %s", x.toPlainString()));
		BigDecimal y = expression.evaluateAt(new Variable("x", x)).toBigDecimal(precision);
		if (y.compareTo(BigDecimal.ZERO) == 0) {
			return x;
		}

		BigDecimal lambda = findLambda(expression, interval, adjustedPrecision);
		Expression relaxedExpression = relax(expression, lambda);
		info(String.format("Relaxed expression form: %s", relaxedExpression));

		logHeader();
		int iteration = 0;
		while (true) {
			iteration++;
			BigDecimal xnew;
			BigDecimal ynew;
			BigDecimal diff;
			try {
				xnew = relaxedExpression.evaluateAt(new Variable("x", x)).toBigDecimal(adjustedPrecision);
				diff = xnew.subtract(x, mathContext);
				ynew = expression.evaluateAt(new Variable("x", xnew)).toBigDecimal(adjustedPrecision);
			} catch (ExpressionConversionException e) {
				throw new InterruptedSearchException(x, e.getMessage());
			}
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

		Expression firstDerivative = expression.derivative("x");
		Expression secondDerivative = firstDerivative.derivative("x");

		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();

		Variable leftVar = new Variable("x", left);
		Variable rightVar = new Variable("x", right);

		BigDecimal fLeft = expression.evaluateAt(leftVar).toBigDecimal(precision);
		BigDecimal fRight = expression.evaluateAt(rightVar).toBigDecimal(precision);

		if (fLeft.compareTo(BigDecimal.ZERO) == 0) {
			return left;
		}

		if (fRight.compareTo(BigDecimal.ZERO) == 0) {
			return right;
		}

		BigDecimal ddfLeft = null;
		try {
			ddfLeft = secondDerivative.evaluateAt(leftVar).toBigDecimal(precision);
		} catch (ExpressionConversionException e) {
		}

		if (ddfLeft != null && fLeft.multiply(ddfLeft, mathContext).compareTo(BigDecimal.ZERO) > 0) {
			return left;
		}

		BigDecimal ddfRight = null;
		try {
			ddfRight = secondDerivative.evaluateAt(rightVar).toBigDecimal(precision);
		} catch (ExpressionConversionException e) {
		}

		if (ddfRight != null && fRight.multiply(ddfRight, mathContext).compareTo(BigDecimal.ZERO) > 0) {
			return right;
		}

		info(String.format("Expression: %s.", expression));
		info(String.format("Second Derivative: %s.", secondDerivative));
		info(String.format("f(%s) = %s\tf(%s) = %s", left.toPlainString(), fLeft.toPlainString(), right.toPlainString(),
				fRight.toPlainString()));
		if (ddfLeft != null) {
			info(String.format("f''(%s) = %s", left.toPlainString(), ddfLeft.toString()));
		}

		if (ddfRight != null) {
			info(String.format("f''(%s) = %s", right.toPlainString(), ddfRight.toString()));
		}

		throw new InitialApproximationException(expression, interval);
	}

	// -----LOGGING----- //
	private static void info(String message) {
		NLogger.info(String.format("%s: %s", CLASS_PREFIX, message));
	}

	private static void logHeader() {
		info(TABLE_HEADER);
	}

	private static void logStep(int iteration, BigDecimal xi, BigDecimal xi1, BigDecimal fxi1, BigDecimal diff) {
		info(String.format(TABLE_TEMPLATE, iteration, xi, xi1, fxi1, diff));
	}
}
