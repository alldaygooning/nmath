package nikita.math.solver.root.single;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import nikita.logging.NLogger;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.exception.construct.expression.ExpressionConversionException;
import nikita.math.exception.construct.expression.ExpressionEvaluationException;
import nikita.math.exception.construct.root.InitialApproximationException;

public class NewtonRootFinder extends SingleRootFinder {

	static final int extraPrecision = 5;

	static final String prefix = "NewtonRootFinder";
	static final String tableTemplate = "%-8s | %-15s | %-15s | %-15s | %-15s | %-15s ";
	static final String header = String.format(tableTemplate, "Iteration", "x_i", "f(x_i)", "f'(x_i)", "x_i+1", "|x_i+1 - x|");

	public static BigDecimal find(Expression expression, Interval interval, Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);
		Precision adjustedPrecision = new Precision(precision.getString());
		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(extraPrecision)));

		BigDecimal x = findInitial(expression, interval, precision);
		info(String.format("Initial approximation x0 = %s", x.toPlainString()));
		BigDecimal y = expression.evaluateAt(new Variable("x", x)).toBigDecimal(precision);
		if (y.compareTo(BigDecimal.ZERO) == 0) {
			return x;
		}

		Expression derivative = expression.derivative("x");

		logHeader();
		int iteration = 0;
		while (true) {
			iteration++;

			Variable xVar = new Variable("x", x);
			BigDecimal fx = expression.evaluateAt(xVar).toBigDecimal(adjustedPrecision);
			BigDecimal dfx;
			try {
				dfx = derivative.evaluateAt(xVar).toBigDecimal(adjustedPrecision);
			} catch (ExpressionConversionException e) {
				throw new ExpressionEvaluationException(derivative, xVar);
			}

			BigDecimal fraction = fx.divide(dfx, mathContext);
			BigDecimal xnew = x.subtract(fraction, mathContext);

			BigDecimal diff = xnew.subtract(x, mathContext).abs();
			logStep(iteration, x, fx, dfx, xnew, diff);
			if (diff.compareTo(precision.getAccuracy()) < 0 || fraction.abs().compareTo(precision.getAccuracy()) < 0
					|| fx.abs().compareTo(precision.getAccuracy()) < 0) {
				x = xnew;
				break;
			}

			x = xnew;
		}

		mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);
		return new BigDecimal(x.toPlainString(), mathContext);
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
		NLogger.info(String.format("%s: %s", prefix, message));
	}

	private static void logHeader() {
		info(header);
	}

	private static void logStep(int iteration, BigDecimal xi, BigDecimal fxi, BigDecimal dfxi, BigDecimal xi1, BigDecimal diff) {
		info(String.format(tableTemplate, iteration, xi, fxi, dfxi, xi1, diff));
	}

}
