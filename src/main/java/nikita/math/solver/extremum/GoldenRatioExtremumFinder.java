package nikita.math.solver.extremum;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.extremum.Maximum;
import nikita.math.construct.extremum.Minimum;

public class GoldenRatioExtremumFinder extends ExtremumFinder {

	private static final int extraPrecision = 4;

	public static Minimum minimum(Expression expression, Interval interval, Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue() + extraPrecision, RoundingMode.HALF_UP);
		Precision adjustedPrecision = new Precision(precision.getString());
		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(extraPrecision)));

		BigDecimal accuracy = precision.getAccuracy();
		BigDecimal a = interval.getLeft();
		BigDecimal b = interval.getRight();

		int scale = precision.getPrecision().intValue() + extraPrecision;

		BigDecimal sqrt5 = new BigDecimal(Math.sqrt(5), mathContext);
		BigDecimal r = sqrt5.subtract(BigDecimal.ONE, mathContext).divide(BigDecimal.valueOf(2), mathContext);

		BigDecimal x1 = b.subtract(b.subtract(a, mathContext).multiply(r, mathContext), mathContext);
		BigDecimal x2 = a.add(b.subtract(a, mathContext).multiply(r, mathContext), mathContext);

		BigDecimal fx1 = NMath.getBigDecimal(NMath.replaceAll(expression, x1.toPlainString()), adjustedPrecision);
		BigDecimal fx2 = NMath.getBigDecimal(NMath.replaceAll(expression, x2.toPlainString()), adjustedPrecision);

		logHeader();

		int iteration = 0;
		while (b.subtract(a, mathContext).abs().compareTo(accuracy) > 0) {
			logStep(iteration, a, b, x1, x2, fx1, fx2, b.subtract(a, mathContext).abs());
			if (fx1.compareTo(fx2) < 0) {
				b = x2;
				x2 = x1;
				fx2 = fx1;
				x1 = b.subtract(b.subtract(a, mathContext).multiply(r, mathContext), mathContext);
				fx1 = NMath.getBigDecimal(NMath.replaceAll(expression, x1.toPlainString()), adjustedPrecision);
			} else {
				a = x1;
				x1 = x2;
				fx1 = fx2;
				x2 = a.add(b.subtract(a, mathContext).multiply(r, mathContext), mathContext);
				fx2 = NMath.getBigDecimal(NMath.replaceAll(expression, x2.toPlainString()), adjustedPrecision);
			}
			iteration++;
		}
		BigDecimal xm = a.add(b, mathContext).divide(BigDecimal.valueOf(2), scale, RoundingMode.HALF_UP);
		BigDecimal ym = NMath.getBigDecimal(NMath.replaceAll(expression, xm.toPlainString()), precision);
		return new Minimum(xm, ym);
	}

	public static Maximum maximum(Expression expression, Interval interval, Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue() + extraPrecision, RoundingMode.HALF_UP);
		Precision adjustedPrecision = new Precision(precision.getString());
		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(extraPrecision)));

		BigDecimal accuracy = precision.getAccuracy();
		BigDecimal a = interval.getLeft();
		BigDecimal b = interval.getRight();

		int scale = precision.getPrecision().intValue() + extraPrecision;

		BigDecimal sqrt5 = new BigDecimal(Math.sqrt(5), mathContext);
		BigDecimal r = sqrt5.subtract(BigDecimal.ONE, mathContext).divide(BigDecimal.valueOf(2), mathContext);

		BigDecimal c = b.subtract(b.subtract(a, mathContext).multiply(r, mathContext), mathContext);
		BigDecimal d = a.add(b.subtract(a, mathContext).multiply(r, mathContext), mathContext);

		BigDecimal fc = NMath.getBigDecimal(NMath.replaceAll(expression, c.toPlainString()), adjustedPrecision);
		BigDecimal fd = NMath.getBigDecimal(NMath.replaceAll(expression, d.toPlainString()), adjustedPrecision);

		logHeader();

		int iteration = 0;
		while (b.subtract(a, mathContext).abs().compareTo(accuracy) > 0) {
			logStep(iteration, a, b, c, d, fc, fd, b.subtract(a, mathContext).abs());
			if (fc.compareTo(fd) > 0) {
				b = d;
				d = c;
				fd = fc;
				c = b.subtract(b.subtract(a, mathContext).multiply(r, mathContext), mathContext);
				fc = NMath.getBigDecimal(NMath.replaceAll(expression, c.toPlainString()), adjustedPrecision);
			} else {
				a = c;
				c = d;
				fc = fd;
				d = a.add(b.subtract(a, mathContext).multiply(r, mathContext), mathContext);
				fd = NMath.getBigDecimal(NMath.replaceAll(expression, d.toPlainString()), adjustedPrecision);
			}
			iteration++;
		}
		BigDecimal xm = a.add(b, mathContext).divide(BigDecimal.valueOf(2), scale, RoundingMode.HALF_UP);
		BigDecimal ym = NMath.getBigDecimal(NMath.replaceAll(expression, xm.toPlainString()), precision);
		return new Maximum(xm, ym);
	}

	private static void logHeader() {
		String header = String.format("%-10s | %-20s | %-20s | %-20s | %-20s | %-20s | %-20s | %-20s", "Iteration", "Left", "Right", "c",
				"d", "f(c)", "f(d)", "Interval Length");
		System.out.println(header);
		System.out.println(new String(new char[header.length()]).replace("\0", "-"));
	}

	private static void logStep(int iteration, BigDecimal left, BigDecimal right, BigDecimal c, BigDecimal d, BigDecimal fc, BigDecimal fd,
			BigDecimal length) {
		String line = String.format("%-10d | %-20s | %-20s | %-20s | %-20s | %-20s | %-20s | %-20s", iteration, left.toPlainString(),
				right.toPlainString(), c.toPlainString(), d.toPlainString(), fc.toPlainString(), fd.toPlainString(),
				length.toPlainString());
		System.out.println(line);
	}

}