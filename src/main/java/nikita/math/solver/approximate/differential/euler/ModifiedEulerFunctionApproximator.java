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

public class ModifiedEulerFunctionApproximator extends EulerFunctionApproximator {
	private static final String FULL_NAME = "Modified Differential Euler Function Approximator";
	private static final String LOGGER_NAME = "ModifiedDifferentialEulerFunctionApproximator";
	private static final String SHORT_NAME = "moddeuler";

	public ModifiedEulerFunctionApproximator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
		order = 2;
	}

	@Override
	public List<Point> dSolve(Expression differential, Point initial, Interval interval, BigDecimal step, Precision precision) {
		MathContext mc = precision.getMathContext();

		List<Point> points = new ArrayList<Point>();
		points.add(initial);
		BigDecimal halfStep = step.divide(BigDecimal.valueOf(2), mc);

		while (true) {
			Point previous = points.get(points.size() - 1);
			Point next = next(differential, previous, step, precision);

			BigDecimal xCurrent = previous.getX().add(step, mc);
			if (xCurrent.compareTo(interval.getRight()) > 0) {
				break;
			}
			BigDecimal xNext = next.getX();
			BigDecimal yCurrent = previous.getY();
			BigDecimal yNext = next.getY();

			Expression fNext = differential.evaluateAt(new Variable("x", xNext), precision).evaluateAt(new Variable("y", yNext), precision);
			Expression fCurrent = differential.evaluateAt(new Variable("x", xCurrent), precision).evaluateAt(new Variable("y", yCurrent),
					precision);
			BigDecimal fTotal = fNext.add(fCurrent, precision).toBigDecimal(precision);

			BigDecimal yCorrected = yCurrent.add(fTotal.multiply(halfStep, mc), mc);

			Point point = new Point(xNext, yCorrected);
			points.add(point);
		}

		return points;
	}
}
