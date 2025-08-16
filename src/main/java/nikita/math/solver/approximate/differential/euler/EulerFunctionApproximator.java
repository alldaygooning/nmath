package nikita.math.solver.approximate.differential.euler;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.solver.approximate.differential.DifferentialFunctionApproximator;

public class EulerFunctionApproximator extends DifferentialFunctionApproximator {

	private static final String FULL_NAME = "Differential Euler Function Approximator";
	private static final String LOGGER_NAME = "DifferentialEulerFunctionApproximator";
	private static final String SHORT_NAME = "deuler";

	public EulerFunctionApproximator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
	}

	public EulerFunctionApproximator(String fullName, String loggerName, String shortName) {
		super(fullName, loggerName, shortName);
	}

	@Override
	public List<Point> dSolve(Expression differential, Point initial, Interval interval, BigDecimal step, Precision precision) {
		MathContext mc = precision.getMathContext();

		List<Point> points = new ArrayList<Point>();
		points.add(initial);

		Point previous = initial;
		while (true) {
			BigDecimal x = previous.getX().add(step, mc);
			if (x.compareTo(interval.getRight()) > 0) {
				break;
			}
			Point point = next(differential, previous, step, precision);
			points.add(point);
			previous = point;
		}
		return points;
	}

	public static Point next(Expression differential, Point previous, BigDecimal step, Precision precision) {
		MathContext mc = precision.getMathContext();

		BigDecimal x = previous.getX().add(step, mc);
		BigDecimal y = differential.evaluateAt(new Variable("x", previous.getX()), precision)
				.evaluateAt(new Variable("y", previous.getY()), precision).toBigDecimal(precision).multiply(step, mc);
		y = previous.getY().add(y, mc);

		return new Point(x, y);
	}
}
