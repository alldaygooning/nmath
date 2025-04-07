package nikita.math.solver.root.single;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import nikita.logging.NLogger;
import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;

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

		Expression derivative = expression.derivative("x");

		logHeader();
		int iteration = 0;
		while (true) {
			iteration++;

			BigDecimal fx = NMath.getBigDecimal(NMath.replaceAll(expression, x.toPlainString()), adjustedPrecision);
			BigDecimal dfx = NMath.getBigDecimal(NMath.replaceAll(derivative, x.toPlainString()), adjustedPrecision);

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

	private static void logStep(int iteration, BigDecimal xi, BigDecimal fxi, BigDecimal dfxi, BigDecimal xi1, BigDecimal diff) {
		info(String.format(tableTemplate, iteration, xi, fxi, dfxi, xi1, diff));
	}

}
