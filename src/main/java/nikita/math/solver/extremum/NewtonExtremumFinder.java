package nikita.math.solver.extremum;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.extremum.Minimum;

public class NewtonExtremumFinder extends ExtremumFinder {

	private static final int extraPrecision = 2;

	public static Minimum minimum(Expression expression, Interval interval, Precision precision) {
		BigDecimal accuracy = precision.getAccuracy();
		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();

		int scale = precision.getPrecision().intValue() + extraPrecision;

		Expression firstDerivative = new Expression(expression.derivative("x").toString());
		Expression secondDerivative = new Expression(firstDerivative.derivative("x").toString());

		BigDecimal x;

//		BigDecimal fa = NMath.getBigDecimal(NMath.replaceAll(expression, left.toPlainString()), precision, scale);
//		BigDecimal ddfa = NMath.getBigDecimal(NMath.replaceAll(secondDerivative, left.toPlainString()), precision, scale);
//		if (fa.multiply(ddfa).compareTo(BigDecimal.ZERO) > 0) {
//			x = left;
//		} else {
//			x = right;
//		}

		x = interval.getLength().divide(BigDecimal.valueOf(2)).add(left);

		int iteration = 0;
		logHeader();

		BigDecimal dfx = NMath.getBigDecimal(NMath.replaceAll(firstDerivative, x.toPlainString()), precision, scale);
		BigDecimal ddfx = NMath.getBigDecimal(NMath.replaceAll(secondDerivative, x.toPlainString()), precision, scale);
		logStep(iteration, x, dfx, ddfx);

		while (dfx.abs().compareTo(accuracy) > 0) {
			dfx = NMath.getBigDecimal(NMath.replaceAll(firstDerivative, x.toPlainString()), precision, scale);
			ddfx = NMath.getBigDecimal(NMath.replaceAll(secondDerivative, x.toPlainString()), precision, scale);

			x = x.subtract(dfx.divide(ddfx, scale, RoundingMode.HALF_UP));
			x = x.setScale(scale, RoundingMode.HALF_UP);

			iteration++;
			dfx = NMath.getBigDecimal(NMath.replaceAll(firstDerivative, x.toPlainString()), precision, scale);
			ddfx = NMath.getBigDecimal(NMath.replaceAll(secondDerivative, x.toPlainString()), precision, scale);
			logStep(iteration, x, dfx, ddfx);
		}

		MathContext mathContext = new MathContext(precision.getPrecision().intValue());
		BigDecimal xmin = new BigDecimal(x.toString(), mathContext);
		BigDecimal ymin = NMath.getBigDecimal(NMath.replaceAll(expression, x.toPlainString()), precision);
		return new Minimum(xmin, ymin);
	}

	private static void logHeader() {
		String header = String.format("%-10s | %-20s | %-20s | %-20s", "Iteration", "x", "f'(x)", "f''(x)");
		System.out.println(header);
		System.out.println(new String(new char[header.length()]).replace('\0', '-'));
	}

	private static void logStep(int iteration, BigDecimal x, BigDecimal df, BigDecimal ddf) {
		String line = String.format("%-10d | %-20s | %-20s | %-20s", iteration, x.toPlainString(), df.toPlainString(), ddf.toPlainString());
		System.out.println(line);
	}
}
