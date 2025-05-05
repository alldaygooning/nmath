package nikita.math.solver.optimize;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.equation.Equation;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;

public class CoordinateOptimizer extends Optimizer {

	private static final String NAME = "Coordinate Optimizer";
	private static final int EXTRA_PRECISION = 5;

	public CoordinateOptimizer(){
		super();
		name = NAME;
	}

	@Override
	public void optimize(Expression expression, Point initialApproximation, Precision precision) {
		Precision adjustedPrecision = precision.getAdjustedPrecision(EXTRA_PRECISION);
		MathContext mc = adjustedPrecision.getMathContext();

		Variable xVar = new Variable("x", initialApproximation.getX());
		Variable yVar = new Variable("y", initialApproximation.getY());

		while (true) {

			Expression yFixedExpression = expression.evaluateAt(yVar, adjustedPrecision);
			BigDecimal fxi = yFixedExpression.evaluateAt(xVar, adjustedPrecision).toBigDecimal(adjustedPrecision);
			List<Expression> solutions = (new Equation(yFixedExpression.derivative(xVar), Expression.ZERO)).solve(xVar, adjustedPrecision);
			xVar.setNumericValue(solutions.get(0).toBigDecimal(adjustedPrecision));
			BigDecimal fxi1 = yFixedExpression.evaluateAt(xVar, adjustedPrecision).toBigDecimal(adjustedPrecision);

			System.out.println(xVar + " " + yVar); // Спросить челов вот за эту тему!
			if (fxi1.subtract(fxi, mc).abs().compareTo(precision.getAccuracy()) <= 0) {
				break;
			}

			Expression xFixedExpression = expression.evaluateAt(xVar, adjustedPrecision);
			BigDecimal fyi = xFixedExpression.evaluateAt(yVar, adjustedPrecision).toBigDecimal(adjustedPrecision);
			solutions = (new Equation(xFixedExpression.derivative(yVar), Expression.ZERO).solve(yVar, adjustedPrecision));
			yVar.setNumericValue(solutions.get(0).toBigDecimal(adjustedPrecision));
			BigDecimal fyi1 = xFixedExpression.evaluateAt(yVar, adjustedPrecision).toBigDecimal(adjustedPrecision);

			System.out.println(xVar + " " + yVar);
			if (fyi1.subtract(fyi, mc).abs().compareTo(precision.getAccuracy()) <= 0) {
				break;
			}

		}
	}
}
