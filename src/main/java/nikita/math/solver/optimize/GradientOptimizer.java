package nikita.math.solver.optimize;

import java.math.BigDecimal;
import java.math.MathContext;

import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;

public class GradientOptimizer extends Optimizer {

	private static final String NAME = "Gradient Optimizer";
	private static final int EXTRA_PRECISION = 5;

	private static final BigDecimal INITIAL_STEP = BigDecimal.valueOf(0.25);
	private static final BigDecimal STEP_MULTIPLIER = BigDecimal.valueOf(0.5);

	public GradientOptimizer() {
		super();
		name = NAME;
	}

	@Override
	public void optimize(Expression expression, Point initialApproximation, Precision precision) {
		Precision adjustedPrecision = precision.getAdjustedPrecision(EXTRA_PRECISION);
		MathContext mc = adjustedPrecision.getMathContext();
		
		Variable xVar = new Variable("x", initialApproximation.getX());
		Variable yVar = new Variable("y", initialApproximation.getY());
		
		Expression partialX = expression.derivative(xVar);
		Expression partialY = expression.derivative(yVar);

		BigDecimal step = INITIAL_STEP;
		while (true) {
			BigDecimal partialXEval = partialX.evaluateAt(xVar, adjustedPrecision).evaluateAt(yVar, adjustedPrecision)
					.toBigDecimal(adjustedPrecision);
			BigDecimal partialYEval = partialY.evaluateAt(yVar, adjustedPrecision).evaluateAt(xVar, adjustedPrecision)
					.toBigDecimal(adjustedPrecision);

			Variable xNew = new Variable("x", xVar.getNumericValue().subtract(step.multiply(partialXEval, mc), mc));
			Variable yNew = new Variable("y", yVar.getNumericValue().subtract(step.multiply(partialYEval, mc), mc));

			BigDecimal fMi = expression.evaluateAt(xVar, adjustedPrecision).evaluateAt(yVar, adjustedPrecision)
					.toBigDecimal(adjustedPrecision);
			BigDecimal fMi1 = expression.evaluateAt(xNew, adjustedPrecision).evaluateAt(yNew, adjustedPrecision)
					.toBigDecimal(adjustedPrecision);

			if (partialXEval.pow(2, mc).add(partialYEval.pow(2, mc), mc).sqrt(mc).compareTo(precision.getAccuracy()) <= 0) {
				break;
			}

			if (fMi.compareTo(fMi1) <= 0) {
				step = step.multiply(STEP_MULTIPLIER, mc);
				continue;
			}

			xVar = xNew;
			yVar = yNew;

			System.out.println(xVar + " " + yVar);

			if (fMi.subtract(fMi1, mc).abs().compareTo(precision.getAccuracy()) < 0) {
				break;
			}
		}
	}
}
