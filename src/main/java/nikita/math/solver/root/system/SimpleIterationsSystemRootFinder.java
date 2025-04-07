package nikita.math.solver.root.system;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import nikita.logging.NLogger;
import nikita.math.construct.Matrix;
import nikita.math.construct.Point;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.expression.ExpressionSystem;

public class SimpleIterationsSystemRootFinder extends SystemRootFinder {

	static final int extraPrecision = 5;

	static final String prefix = "SimpleIterationsSystemRootFinder";

	static final String tableTemplate = "%-15s | %-15s | %-15s | %-15s | %-25s | %-25s";
	static final String logStepSeparator = ("=").repeat(50);
	static final String logStepTemplate = "Iteration %s:\n"//
			+ "(1)Evaluated Inversed Jacobian Matrix:\n%s\n"//
			+ "(2)Evaluated Function Vector:\n%s\n" //
			+ "Delta Vector(Dot product of (1) and (2)):\n%s\n" //
			+ String.format(tableTemplate + "\n", "x_i", "y_i", "x_i+1", "y_i+1", "|x_i - x_i+1|", "|y_i - y_i+1|") //
			+ tableTemplate
			+ "\n"
			+ logStepSeparator;

	public static Point find(ExpressionSystem system, Point initialApproximation, Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue() + extraPrecision, RoundingMode.HALF_UP);
		Precision adjustedPrecision = new Precision(precision.getString());
		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(extraPrecision)));

		Matrix inverseJacobiMatrix = system.jacobiMatrix().inverse();
		info(String.format("Found inverse Jacobian matrix: \n%s", inverseJacobiMatrix.toBeautifulString()));
		
		BigDecimal x =  initialApproximation.getX();
		BigDecimal y = initialApproximation.getY();

		int iteration = 0;
		while (true) {
			iteration++;

			Variable xVar = new Variable("x", x);
			Variable yVar = new Variable("y", y);

			Matrix lambda = inverseJacobiMatrix.replaceAll(xVar, precision).replaceAll(yVar, precision);
			List<Expression> expressions = system.evaluateAt(xVar, precision).evaluateAt(yVar, precision).getSingles();

			Matrix fVector = new Matrix(1, 2);
			fVector.put(0, 0, expressions.get(0));
			fVector.put(1, 0, expressions.get(1));
			
			Matrix deltaVector = lambda.dot(fVector);
			
			BigDecimal deltax = deltaVector.get(0, 0).toBigDecimal(adjustedPrecision);
			BigDecimal deltay = deltaVector.get(1, 0).toBigDecimal(adjustedPrecision);
			
			BigDecimal xnew = x.subtract(deltax, mathContext);
			BigDecimal ynew = y.subtract(deltay, mathContext);

			BigDecimal xDiff = xnew.subtract(x, mathContext).abs();
			BigDecimal yDiff = ynew.subtract(y, mathContext).abs();


			if (xDiff.compareTo(precision.getAccuracy()) < 0 && yDiff.compareTo(precision.getAccuracy()) < 0) {
				break;
			}

			logStep(iteration, x, y, lambda, fVector, deltaVector, xnew, ynew, xDiff, yDiff);

			x = xnew;
			y = ynew;
		}

		mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);
		return new Point(new BigDecimal(x.toPlainString(), mathContext), new BigDecimal(y.toPlainString(), mathContext));
	}

	// -----LOGGING----- //
	private static void info(String message) {
		NLogger.info(String.format("%s: %s", prefix, message));
	}

	private static void logStep(int iteration, BigDecimal x, BigDecimal y, Matrix lambda, Matrix fVector, Matrix deltaVector,
			BigDecimal xnew, BigDecimal ynew, BigDecimal xDiff, BigDecimal yDiff) {
		info(String.format(logStepTemplate, iteration, lambda.toBeautifulString(), fVector.toBeautifulString(),
				deltaVector.toBeautifulString(), x, y, xnew, ynew, xDiff, yDiff));
	}
}
