package nikita.math.solver.extremum;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.extremum.Minimum;

public class ChordExtremumFinder extends ExtremumFinder {

	private static final int extraPrecision = 2;

	public static Minimum minimum(Expression expression, Interval interval, Precision precision) {
		BigDecimal accuracy = precision.getAccuracy();
		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();

		int scale = precision.getPrecision().intValue() + extraPrecision;

		Expression derivative = new Expression(expression.derivative("x").toString());
		BigDecimal xnew;
		BigDecimal ynew;
		int iteration = 0;
		logHeader();

		while (true) {
			BigDecimal dfa = NMath.getBigDecimal(NMath.replaceAll(derivative, left.toPlainString()), precision, extraPrecision);
			BigDecimal dfb = NMath.getBigDecimal(NMath.replaceAll(derivative, right.toPlainString()), precision, extraPrecision);

			xnew = left.subtract(dfa.divide((dfa.subtract(dfb)), scale, RoundingMode.HALF_UP).multiply(left.subtract(right)));
			xnew = xnew.setScale(scale, RoundingMode.HALF_UP);

			if (NMath.getBigDecimal(NMath.replaceAll(expression, left.toPlainString()), precision)
					.multiply(NMath.getBigDecimal(NMath.replaceAll(expression, xnew.toPlainString()), precision))
					.compareTo(BigDecimal.ZERO) > 0) {
				right = xnew;
				continue;
			}

			ynew = NMath.getBigDecimal(NMath.replaceAll(derivative, xnew.toPlainString()), precision, extraPrecision);

			logStep(iteration, left, right, xnew, ynew);

			if (ynew.abs().compareTo(accuracy) <= 0) {
				break;
			}

			if (ynew.compareTo(BigDecimal.ZERO) > 0) {
				right = xnew;
			} else {
				left = xnew;
			}
			iteration++;
		}

		MathContext mc = new MathContext(precision.getPrecision().intValue());
		BigDecimal xmin = new BigDecimal(xnew.toString(), mc);
		BigDecimal ymin = NMath.getBigDecimal(NMath.replaceAll(expression, xnew.toPlainString()), precision);
		return new Minimum(xmin, ymin);
	}

	private static void logHeader() {
		String header = String.format("%-10s \t| %-20s \t| %-20s \t| %-20s \t| %-20s", "Iteration", "Left", "Right", "xnew", "f'(xnew)");
		System.out.println(header);
		System.out.println(new String(new char[header.length()]).replace("\0", "-"));
	}

	private static void logStep(int iteration, BigDecimal left, BigDecimal right, BigDecimal xnew, BigDecimal ynew) {
		String line = String.format("%-10d \t| %-20s \t| %-20s \t| %-20s \t| %-20s", iteration, left.toPlainString(), right.toPlainString(),
				xnew.toPlainString(), ynew.toPlainString());
		System.out.println(line);
	}
}
