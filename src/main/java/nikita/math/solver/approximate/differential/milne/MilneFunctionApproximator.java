package nikita.math.solver.approximate.differential.milne;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.solver.approximate.differential.DifferentialFunctionApproximator;
import nikita.math.solver.approximate.differential.euler.ModifiedEulerFunctionApproximator;

public class MilneFunctionApproximator extends DifferentialFunctionApproximator {
	private static final String FULL_NAME = "Differential Milne Function Approximator";
	private static final String LOGGER_NAME = "DifferentialMilneFunctionApproximator";
	private static final String SHORT_NAME = "milne";

	private ModifiedEulerFunctionApproximator euler = new ModifiedEulerFunctionApproximator();

	public MilneFunctionApproximator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
		this.minPointsRequired = 4;
		order = 4;
	}

	@Override
	public List<Point> dSolve(Expression differential, Point initial, Interval interval, BigDecimal step, Precision precision) {
		MathContext mc = precision.getMathContext();

		Interval mock = new Interval(interval.getLeft(), interval.getLeft().add(step.multiply(BigDecimal.valueOf(3), mc), mc));
		List<Point> points = euler.dSolve(differential, initial, mock, step, precision);

		BigDecimal quadrupleStep = step.multiply(BigDecimal.valueOf(4), mc);
		BigDecimal oneThirdStep = step.divide(BigDecimal.valueOf(3), mc);
		BigDecimal fourThirdStep = quadrupleStep.divide(BigDecimal.valueOf(3), mc);

		while (true) {
			int index = points.size() - 1;
			Point oneBack = points.get(index--);
			Point twoBack = points.get(index--);
			Point threeBack = points.get(index--);
			Point fourBack = points.get(index--);

			BigDecimal x = oneBack.getX().add(step, mc);
			if (x.compareTo(interval.getRight()) > 0) {
				break;
			}

			BigDecimal yCorrected, yPredicted, fPredicted;

			Expression fOneBack = differential.evaluateAt(new Variable("x", oneBack.getX()), precision)
					.evaluateAt(new Variable("y", oneBack.getY()), precision);
			Expression fTwoBack = differential.evaluateAt(new Variable("x", twoBack.getX()), precision)
					.evaluateAt(new Variable("y", twoBack.getY()), precision);
			Expression fThreeBack = differential.evaluateAt(new Variable("x", threeBack.getX()), precision)
					.evaluateAt(new Variable("y", threeBack.getY()), precision);

			BigDecimal fPredictedTotal = fThreeBack //
					.multiply(Expression.TWO, precision) //
					.sub(fTwoBack, precision) //
					.add(fOneBack.multiply(Expression.TWO, precision), precision).toBigDecimal(precision);
			fPredictedTotal = fPredictedTotal.multiply(fourThirdStep, mc);

			yPredicted = fourBack.getY().add(fPredictedTotal, mc);
			fPredicted = differential.evaluateAt(new Variable("x", x), precision).evaluateAt(new Variable("y", yPredicted), precision)
					.toBigDecimal(precision);

			while (true) {
				BigDecimal fCorrectedTotal = fTwoBack //
						.add(fOneBack.multiply(new Expression("4"), precision), precision) //
						.add(new Expression(fPredicted.toPlainString()), precision).toBigDecimal(precision);
				fCorrectedTotal = fCorrectedTotal.multiply(oneThirdStep, mc);

				yCorrected = twoBack.getY().add(fCorrectedTotal, mc);
				if (yPredicted.subtract(yCorrected, mc).abs().compareTo(precision.getAccuracy()) <= 0) {
					break;
				}
				yPredicted = yCorrected;
			}

			Point point = new Point(x, yCorrected);
			points.add(point);
		}

		return points;
	}
}
