package nikita.math.solver.root.single;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import nikita.logging.NLogger;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;

public class ChordRootFinder extends SingleRootFinder {

	static final int extraPrecision = 5;

	static final String prefix = "ChordRootFinder";
	static final String tableTemplate = "%-8s | %-15s | %-15s | %-15s | %-15s | %-15s | %-15s | %-15s ";
	static final String header = String.format(tableTemplate, "Iteration", "Left", "Right", "x", "f(Left)", "f(Right)", "f(x)",
			"|x_i+1 - x|");

	public static BigDecimal find(Expression expression, Interval interval, Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);
		Precision adjustedPrecision = new Precision(precision.getString());
		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(extraPrecision)));

		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();
		BigDecimal x = findX(expression, interval, precision);
		info(String.format("Initial approximation x0 = %s", x.toPlainString()));

		logHeader();
		int iteration = 0;
		while (true) {
			iteration++;

			BigDecimal fx = expression.evaluateAt(new Variable("x", x)).toBigDecimal(adjustedPrecision);
			if (fx.abs().compareTo(precision.getAccuracy()) < 0) {
				break;
			}
			BigDecimal fLeft = expression.evaluateAt(new Variable("x", left)).toBigDecimal(adjustedPrecision);
			BigDecimal fRight = expression.evaluateAt(new Variable("x", right)).toBigDecimal(adjustedPrecision);

			BigDecimal oldLeft = left;
			BigDecimal oldRight = right;

			if (fx.multiply(fLeft, mathContext).compareTo(BigDecimal.ZERO) < 0) {
				right = x;
			} else {
				left = x;
			}

			interval = new Interval(left, right);
			BigDecimal xnew = findX(expression, interval, precision);
			BigDecimal diff = xnew.subtract(x, mathContext).abs();

			logStep(iteration, oldLeft, oldRight, x, fLeft, fRight, fx, diff);
			if (diff.compareTo(precision.getAccuracy()) < 0
					|| left.subtract(right, mathContext).abs().compareTo(precision.getAccuracy()) < 0) {
				x = xnew;
				break;
			}

			x = xnew;
		}

		mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);
		return new BigDecimal(x.toPlainString(), mathContext);
	}

	private static BigDecimal findX(Expression expression, Interval interval, Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);

		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();

		BigDecimal fLeft = expression.evaluateAt(new Variable("x", left)).toBigDecimal(precision);
		BigDecimal fRight = expression.evaluateAt(new Variable("x", right)).toBigDecimal(precision);

		BigDecimal numerator = (right.subtract(left, mathContext)).multiply(fLeft);
		BigDecimal denominator = fRight.subtract(fLeft, mathContext);

		BigDecimal x = left.subtract((numerator.divide(denominator, mathContext)), mathContext);

		return x;
	}

	// -----LOGGING----- //
	private static void info(String message) {
		NLogger.info(String.format("%s: %s", prefix, message));
	}

	private static void logHeader() {
		info(header);
	}

	private static void logStep(int iteration, BigDecimal left, BigDecimal right, BigDecimal x, BigDecimal fLeft, BigDecimal fRight,
			BigDecimal fx, BigDecimal diff) {
		info(String.format(tableTemplate, iteration, left, right, x, fLeft, fRight, fx, diff));
	}
}
