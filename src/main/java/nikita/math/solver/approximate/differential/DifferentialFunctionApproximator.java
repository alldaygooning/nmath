package nikita.math.solver.approximate.differential;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.matheclipse.parser.client.SyntaxError;

import nikita.external.api.wolfram.WolframAPI;
import nikita.external.api.wolfram.query.WolframQuery;
import nikita.external.api.wolfram.query.WolframQueryBuilder;
import nikita.external.api.wolfram.query.param.WolframQueryFormat;
import nikita.external.api.wolfram.query.param.WolframQueryOutput;
import nikita.external.api.wolfram.query.param.WolframQueryPod;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.construct.expression.ExpressionConversionException;
import nikita.math.exception.construct.expression.ExpressionEvaluationException;
import nikita.math.exception.solver.approximate.DifferentialFunctionApproximationException;
import nikita.math.solver.approximate.FunctionApproximator;
import nikita.math.solver.approximate.differential.euler.EulerFunctionApproximator;
import nikita.math.solver.approximate.differential.euler.ModifiedEulerFunctionApproximator;
import nikita.math.solver.approximate.differential.milne.MilneFunctionApproximator;
import nikita.math.solver.approximate.polynomial.PolynomialFunctionApproximator;

public abstract class DifferentialFunctionApproximator extends FunctionApproximator {

	private static final String FULL_NAME_DEFAULT = "Abstract Differential Function Approximator";
	private static final String LOGGER_NAME_DEFAULT = "AbstractDifferentialFunctionApproximator";

	private static final EulerFunctionApproximator EULER_APPROXIMATOR = new EulerFunctionApproximator();
	private static final ModifiedEulerFunctionApproximator MODIFIED_EULER_APPROXIMATOR = new ModifiedEulerFunctionApproximator();
	private static final MilneFunctionApproximator MILNE_APPROXIMATOR = new MilneFunctionApproximator();

	public static final Map<String, DifferentialFunctionApproximator> DIFFERENTIAL_APPROXIMATORS = Map.ofEntries(//
			Map.entry(EULER_APPROXIMATOR.getShortName(), EULER_APPROXIMATOR), //
			Map.entry(MODIFIED_EULER_APPROXIMATOR.getShortName(), MODIFIED_EULER_APPROXIMATOR), //
			Map.entry(MILNE_APPROXIMATOR.getShortName(), MILNE_APPROXIMATOR)
	);

	private static final int EXTRA_PRECISION = 5;

	private PolynomialFunctionApproximator approximator;
	protected int minPointsRequired = 1;
	protected int order = 1;

	public DifferentialFunctionApproximator(String fullName, String loggerName, String shortName) {
		super(fullName, loggerName, shortName);
		approximator = new PolynomialFunctionApproximator();
		approximator.setMode(5);
	}

	public DifferentialFunctionApproximator() {
		super(FULL_NAME_DEFAULT, LOGGER_NAME_DEFAULT);
	}

	public abstract List<Point> dSolve(Expression differential, Point initial, Interval interval, BigDecimal step, Precision precision);

	public static DifferentialFunctionApproximation dSolve(Expression differential, Point initial, Interval interval, BigDecimal stepLength,
			String method, Precision precision) {
		MathContext mc = precision.getMathContext();
		Precision adjustedPrecision = precision.getAdjustedPrecision(EXTRA_PRECISION);

		BigDecimal intervalLength = interval.getLength();
		if (intervalLength.compareTo(stepLength) <= 0) {
			throw new DifferentialFunctionApproximationException(
					String.format("Length of step (%s) should not be greater than length of interval", stepLength.toPlainString()));
		}
		DifferentialFunctionApproximator approximator = getDifferentialApproximator(method);

		BigDecimal steps = intervalLength.divide(stepLength, new MathContext(mc.getPrecision(), RoundingMode.FLOOR));
		if (steps.compareTo(BigDecimal.valueOf(approximator.minPointsRequired)) <= 0) {
			throw new DifferentialFunctionApproximationException(approximator, String
					.format("Method requires at least %s points to reside within Interval %s", approximator.minPointsRequired,
							interval.toString()));
		}

		Expression wolframApproximated = approximator.wolframSolve(differential, initial, interval);

		List<Point> points = approximator.dSolve(differential, initial, interval, stepLength, adjustedPrecision);
		BigDecimal epsilon = BigDecimal.ZERO;
		if (approximator instanceof EulerFunctionApproximator || approximator instanceof ModifiedEulerFunctionApproximator) {
			int count = 0;
			while (true) {
				count++;
				if (count > 5) {
					break;
				}
				BigDecimal halfStep = stepLength.divide(BigDecimal.valueOf(2), mc);
				List<Point> controlPoints = approximator.dSolve(differential, initial, interval, halfStep, adjustedPrecision);
				if (approximator.rungeCheck(points.get(points.size() - 1), controlPoints.get(controlPoints.size() - 1), precision)) {
					BigDecimal ay = points.get(points.size() - 1).getY();
					BigDecimal by = controlPoints.get(controlPoints.size() - 1).getY();

					BigDecimal numerator = ay.subtract(by, mc).abs(mc);
					BigDecimal denominator = BigDecimal.valueOf(2).pow(approximator.order, mc).subtract(BigDecimal.ONE, mc);
					epsilon = numerator.divide(denominator, mc);

					points = controlPoints;
					break;
				}
				points = controlPoints;
				stepLength = halfStep;
			}
		} else {
			BigDecimal max = null;
			for (Point point : points) {
				BigDecimal x = point.getX();
				BigDecimal yApproximated = point.getY();
				BigDecimal yActual = wolframApproximated.evaluateAt(new Variable("x", x), precision).toBigDecimal(precision);

				BigDecimal diff = yApproximated.subtract(yActual, mc).abs(mc);
				if (max == null) {
					max = diff;
				} else {
					max = max.max(diff);
				}
			}
			epsilon = max;
		}

		Expression approximated = approximator.approximate(points, precision);

		DifferentialFunctionApproximation approximation = new DifferentialFunctionApproximation(approximated, wolframApproximated, points,
				epsilon,
				precision);

		return approximation;
	}

	public static DifferentialFunctionApproximator getDifferentialApproximator(String shortName) {
		if (!DIFFERENTIAL_APPROXIMATORS.containsKey(shortName)) {
			throw new DifferentialFunctionApproximationException(String.format("No method found but short name '%s'", shortName));
		}
		return DIFFERENTIAL_APPROXIMATORS.get(shortName);
	}

	private Expression wolframSolve(Expression differential, Point initial, Interval interval) {
		System.out.println("SOLVING WOLFRAM");
		String command = String.format("DSolve[y'[x]==[%s],y[x], x]", differential.getWolframString().replace("y", "y[x]"),
				initial.getX().toPlainString(),
				initial.getY().toPlainString());
		WolframQuery query = new WolframQueryBuilder(command).format(WolframQueryFormat.PLAINTEXT).output(WolframQueryOutput.JSON)
				.include(WolframQueryPod.RESULT).toQuery();

		try {
			Expression approximated = WolframAPI.getDifferentialResult(WolframAPI.query(query)).get(0);
			if (approximated.isComplex()) {
				throw new DifferentialFunctionApproximationException("Unable to find analytic solution");
			}
//			Expression left = approximated.evaluateAt(new Variable("x", initial.getX()));
//			Expression right = new Expression(initial.getX().toPlainString());
//			Equation equation = new Equation(left, right);
//			BigDecimal c = equation.solve(new Variable("c"), NMath.DEFAULT_EXPRESSION_PRECISION).get(0).toBigDecimal();
			
			approximated = approximated.evaluateAt(new Variable("c", Expression.ZERO));
			System.out.println("DONE");
			return approximated;
		} catch (IOException | InterruptedException e) {
			return null;
		} catch (SyntaxError | ExpressionEvaluationException | ExpressionConversionException e) {
			throw new DifferentialFunctionApproximationException("Unable to find analytic solution");
		}
	}

	@Override
	public Expression approximate(List<Point> points, Precision precision) {
		return approximator.approximate(points, precision);
	}

	protected boolean rungeCheck(Point a, Point b, Precision precision) {
		MathContext mc = precision.getMathContext();
		BigDecimal ay = a.getY();
		BigDecimal by = b.getY();
		
		BigDecimal numerator = ay.subtract(by, mc).abs(mc);
		BigDecimal denominator = BigDecimal.valueOf(2).pow(order, mc).subtract(BigDecimal.ONE, mc);
		
		System.out.println("RUNGE: " + numerator.divide(denominator, mc) + " VS " + precision.getAccuracy());

		return (numerator.divide(denominator, mc).compareTo(precision.getAccuracy()) <= 0);
	}
}
