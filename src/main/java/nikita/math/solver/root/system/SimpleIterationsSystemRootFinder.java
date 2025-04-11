package nikita.math.solver.root.system;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nikita.logging.NLogger;
import nikita.math.NMath;
import nikita.math.construct.Matrix;
import nikita.math.construct.Point;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.expression.ExpressionSystem;
import nikita.math.exception.construct.expression.ExpressionConversionException;
import nikita.math.exception.construct.expression.ExpressionEvaluationException;
import nikita.math.exception.construct.root.InitialApproximationException;
import nikita.math.exception.construct.root.InterruptedSearchException;
import nikita.math.solver.root.system.iteration.SimpleIterationsSystemIteration;

public class SimpleIterationsSystemRootFinder extends SystemRootFinder {

	static final int EXTRA_PRECISION = 5;
	static final int ITERATIONS_MAX = 100;

	static final String CLASS_PREFIX = "SimpleIterationsSystemRootFinder";

	static final String TABLE_TEMPLATE = "%-15s | %-15s | %-15s | %-15s | %-25s | %-25s";
	static final String LOG_STEP_SEPARATOR = ("=").repeat(50);
	static final String LOG_STEP_TEMPLATE = "Iteration %s:\n"//
			+ "(1)Evaluated Inversed Jacobian Matrix:\n%s\n"//
			+ "(2)Evaluated Function Vector:\n%s\n" //
			+ "Delta Vector(Dot product of (1) and (2)):\n%s\n" //
			+ String.format(TABLE_TEMPLATE + "\n", "x_i", "y_i", "x_i+1", "y_i+1", "|x_i - x_i+1|", "|y_i - y_i+1|") //
			+ TABLE_TEMPLATE + "\n" + LOG_STEP_SEPARATOR;

//	public static Point find(ExpressionSystem system, Point initialApproximation, Precision precision) {
//		MathContext mathContext = new MathContext(precision.getPrecision().intValue() + EXTRA_PRECISION, RoundingMode.HALF_UP);
//		Precision adjustedPrecision = new Precision(precision.getString());
//		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(EXTRA_PRECISION)));
//
//		Matrix inverseJacobiMatrix = system.jacobiMatrix().inverse();
//		info(String.format("Found inverse Jacobian matrix: \n%s", inverseJacobiMatrix.toBeautifulString()));
//		
//		BigDecimal x =  initialApproximation.getX();
//		BigDecimal y = initialApproximation.getY();
//
//		int iteration = 0;
//		while (true) {
//			iteration++;
//
//			Variable xVar = new Variable("x", x);
//			Variable yVar = new Variable("y", y);
//
//			Matrix lambda = inverseJacobiMatrix.evaluateAt(xVar, precision).evaluateAt(yVar, precision);
//			List<Expression> expressions = system.evaluateAt(xVar, precision).evaluateAt(yVar, precision).getSingles();
//
//			Matrix fVector = new Matrix(1, 2);
//			fVector.put(0, 0, expressions.get(0));
//			fVector.put(1, 0, expressions.get(1));
//			
//			Matrix deltaVector = lambda.dot(fVector);
//			
//			BigDecimal deltax = deltaVector.get(0, 0).toBigDecimal(adjustedPrecision);
//			BigDecimal deltay = deltaVector.get(1, 0).toBigDecimal(adjustedPrecision);
//			
//			BigDecimal xnew = x.subtract(deltax, mathContext);
//			BigDecimal ynew = y.subtract(deltay, mathContext);
//
//			BigDecimal xDiff = xnew.subtract(x, mathContext).abs();
//			BigDecimal yDiff = ynew.subtract(y, mathContext).abs();
//
//
//			if (xDiff.compareTo(precision.getAccuracy()) < 0 && yDiff.compareTo(precision.getAccuracy()) < 0) {
//				break;
//			}
//
//			logStep(iteration, x, y, lambda, fVector, deltaVector, xnew, ynew, xDiff, yDiff);
//
//			x = xnew;
//			y = ynew;
//		}
//
//		mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);
//		return new Point(new BigDecimal(x.toPlainString(), mathContext), new BigDecimal(y.toPlainString(), mathContext));
//	}

	public static List<SimpleIterationsSystemIteration> searchIterations(ExpressionSystem system, Point initialApproximation,
			Precision precision) {
		MathContext mathContext = new MathContext(precision.getPrecision().intValue() + EXTRA_PRECISION, RoundingMode.HALF_UP);
		Precision adjustedPrecision = new Precision(precision.getString());
		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(EXTRA_PRECISION)));

		List<SimpleIterationsSystemIteration> iterations = new ArrayList<SimpleIterationsSystemIteration>();

		Matrix inverseJacobiMatrix = system.jacobiMatrix().inverse();
		info(String.format("Found inverse Jacobian matrix: \n%s", inverseJacobiMatrix.toBeautifulString()));

		BigDecimal x = initialApproximation.getX();
		BigDecimal y = initialApproximation.getY();

		if (!initialIsGood(system, new Variable("x", x), new Variable("y", y), adjustedPrecision)) {
			throw new InitialApproximationException(system, initialApproximation);
		}

		int iteration = 0;
		while (true) {
			iteration++;
			if (iteration > ITERATIONS_MAX) {
				throw new InterruptedSearchException(
						"Exceeded maximum number of iteration steps. This happened most likely due to bad Initial Approximation.");
			}

			Variable xVar = new Variable("x", x);
			Variable yVar = new Variable("y", y);

			try {
				if (system.check(xVar, yVar, precision)) {
					info(String.format("Approximation %s, %s is a root.", xVar, yVar));
					return iterations;
				}
			} catch (ExpressionConversionException e) {
			}

			Matrix lambda;
			List<Expression> expressions;
			try {
				lambda = inverseJacobiMatrix.evaluateAt(xVar, adjustedPrecision).evaluateAt(yVar, adjustedPrecision);
				expressions = system.evaluateAt(xVar, adjustedPrecision).evaluateAt(yVar, adjustedPrecision).getSingles();
			} catch (ExpressionEvaluationException e) {
				throw new InterruptedSearchException(Arrays.asList(xVar, yVar), CLASS_PREFIX, e.getMessage());
			}

			Matrix fVector = new Matrix(1, 2);
			fVector.put(0, 0, expressions.get(0));
			fVector.put(1, 0, expressions.get(1));

			Matrix deltaVector = lambda.dot(fVector);

			BigDecimal deltax;
			BigDecimal deltay;
			try {
				deltax = deltaVector.get(0, 0).toBigDecimal(adjustedPrecision);
				deltay = deltaVector.get(1, 0).toBigDecimal(adjustedPrecision);
			} catch (ExpressionConversionException e) {
				throw new InterruptedSearchException(Arrays.asList(xVar, yVar), CLASS_PREFIX, e.getMessage());
			}

			BigDecimal xnew = x.subtract(deltax, mathContext);
			BigDecimal ynew = y.subtract(deltay, mathContext);

			BigDecimal xDiff = xnew.subtract(x, mathContext).abs();
			BigDecimal yDiff = ynew.subtract(y, mathContext).abs();

			iterations.add(new SimpleIterationsSystemIteration(iteration, x, y, xnew, ynew, xDiff, yDiff));
			if (xDiff.compareTo(precision.getAccuracy()) < 0 && yDiff.compareTo(precision.getAccuracy()) < 0) {
				return iterations;
			}

			logStep(iteration, x, y, lambda, fVector, deltaVector, xnew, ynew, xDiff, yDiff);

			x = xnew;
			y = ynew;
		}
	}

	private static boolean initialIsGood(ExpressionSystem system, Variable xVar, Variable yVar, Precision precision) {
		Matrix jacobiMatrix = system.jacobiMatrix();
		Matrix evaluatedMatrix = jacobiMatrix.evaluateAt(xVar, precision).evaluateAt(yVar, precision);
		BigDecimal det = evaluatedMatrix.det().toBigDecimal(precision);
		return (!NMath.equal(det, BigDecimal.ZERO, precision));
	}

	// -----LOGGING----- //
	private static void info(String message) {
		NLogger.info(String.format("%s: %s", CLASS_PREFIX, message));
	}

	private static void logStep(int iteration, BigDecimal x, BigDecimal y, Matrix lambda, Matrix fVector, Matrix deltaVector,
			BigDecimal xnew, BigDecimal ynew, BigDecimal xDiff, BigDecimal yDiff) {
		info(String.format(LOG_STEP_TEMPLATE, iteration, lambda.toBeautifulString(), fVector.toBeautifulString(),
				deltaVector.toBeautifulString(), x, y, xnew, ynew, xDiff, yDiff));
	}
}
