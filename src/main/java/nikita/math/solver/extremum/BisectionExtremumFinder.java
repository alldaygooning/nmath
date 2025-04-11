package nikita.math.solver.extremum;

import java.math.BigDecimal;

public class BisectionExtremumFinder extends ExtremumFinder {

	private static final int extraPrecision = 5;

//	public static Minimum minimum(Expression expression, Interval interval, Precision precision) {
//		BigDecimal accuracy = precision.getAccuracy();
//		BigDecimal left = interval.getLeft();
//		BigDecimal right = interval.getRight();
//		BigDecimal length = left.subtract(right).abs();
//
//		int scale = precision.getPrecision().intValue() + extraPrecision;
//		BigDecimal interruptCondition = accuracy.multiply(BigDecimal.valueOf(2));
//
//		logHeader();
//
//		int iteration = 0;
//
//		while (length.compareTo(interruptCondition) >= 0) {
//			BigDecimal sum = left.add(right);
//
//			BigDecimal x1 = (sum.subtract(accuracy)).divide(BigDecimal.valueOf(2), scale, RoundingMode.HALF_UP);
//			BigDecimal x2 = (sum.add(accuracy)).divide(BigDecimal.valueOf(2), scale, RoundingMode.HALF_UP);
//
//			BigDecimal y1 = NMath.getBigDecimal(NMath.replaceAll(expression, x1.toPlainString()), precision, extraPrecision);
//			BigDecimal y2 = NMath.getBigDecimal(NMath.replaceAll(expression, x2.toPlainString()), precision, extraPrecision);
//
//			logStep(iteration, left, right, x1, x2, y1, y2, length);
//
//			if (y1.compareTo(y2) > 0) {
//				left = x1;
//			} else {
//				right = x2;
//			}
//
//			length = left.subtract(right).abs();
//			iteration++;
//		}
//
//		BigDecimal xm = left.add(right).divide(BigDecimal.valueOf(2), precision.getPrecision().intValue(), RoundingMode.HALF_UP);
//		BigDecimal ym = NMath.getBigDecimal(NMath.replaceAll(expression, xm.toPlainString()), precision);
//
//		return new Minimum(xm, ym);
//	}

	private static void logHeader() {
		String header = String.format("%-10s | %-20s | %-20s | %-20s | %-20s | %-20s | %-20s | %-20s", "Iteration", "Left", "Right", "x1",
				"x2", "y1", "y2", "Length");
		System.out.println(header);
		System.out.println(new String(new char[header.length()]).replace("\0", "-"));
	}

	private static void logStep(int iteration, BigDecimal left, BigDecimal right, BigDecimal x1, BigDecimal x2, BigDecimal y1,
			BigDecimal y2, BigDecimal length) {
		String line = String.format("%-10d | %-20s | %-20s | %-20s | %-20s | %-20s | %-20s | %-20s", iteration, left.toPlainString(),
				right.toPlainString(), x1.toPlainString(), x2.toPlainString(), y1.toPlainString(), y2.toPlainString(),
				length.toPlainString());
		System.out.println(line);
	}
}
