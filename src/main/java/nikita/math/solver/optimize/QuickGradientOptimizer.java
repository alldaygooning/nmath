package nikita.math.solver.optimize;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.equation.Equation;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.construct.expression.ExpressionEvaluationException;
import nikita.math.solver.extremum.GoldenRationExtremumFinder;

public class QuickGradientOptimizer extends Optimizer {

	private static final String NAME = "Quick Gradient Optimizer";
	private static final int EXTRA_PRECISION = 5;

	public QuickGradientOptimizer() {
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

		while (true) {
			System.out.println(xVar + " " + yVar);
			BigDecimal partialXEval = partialX.evaluateAt(xVar, adjustedPrecision).evaluateAt(yVar, adjustedPrecision).toBigDecimal(adjustedPrecision);
			BigDecimal partialYEval = partialY.evaluateAt(yVar, adjustedPrecision).evaluateAt(xVar, adjustedPrecision).toBigDecimal(adjustedPrecision);
//			System.out.println("GRAD: " + partialXEval + " " + partialYEval);

			Expression xi = new Expression(
					String.format("(%s)-(h)*(%s)", xVar.getNumericValue().toPlainString(), partialXEval.toPlainString()));
			Expression yi = new Expression(
					String.format("(%s)-(h)*(%s)", yVar.getNumericValue().toPlainString(), partialYEval.toPlainString()));

//			System.out.println(xi + " " + yi);
			Variable hVar = new Variable("h");
			Expression fh = expression.evaluateAt(new Variable("x", xi), adjustedPrecision).evaluateAt(new Variable("y", yi),
					adjustedPrecision);
//			ExprEvaluator evaluator = new ExprEvaluator();
//			System.out.println("fh: " + evaluator.eval(String.format("Collect(%s, h)", fh)));
			Expression dfh = fh.derivative(hVar);
//			System.out.println("dfh:" + evaluator.eval(String.format("Collect(%s, h)", dfh)));
			try {
				List<Expression> solutions = (new Equation(dfh, Expression.ZERO)).solve(hVar, adjustedPrecision);
				hVar.setNumericValue(solutions.get(0).toBigDecimal(adjustedPrecision));
			} catch (ExpressionEvaluationException e) {
				hVar.setNumericValue(GoldenRationExtremumFinder.minimum(expression, null, adjustedPrecision).getX());
			}

//			System.out.println("h:" + hVar);
			
			BigDecimal fMi = expression.evaluateAt(xVar, adjustedPrecision).evaluateAt(yVar, adjustedPrecision)
					.toBigDecimal(adjustedPrecision);

			xVar.setNumericValue(xi.evaluateAt(hVar, adjustedPrecision).toBigDecimal(adjustedPrecision));
			yVar.setNumericValue(yi.evaluateAt(hVar, adjustedPrecision).toBigDecimal(adjustedPrecision));
			BigDecimal fMi1 = expression.evaluateAt(xVar, adjustedPrecision).evaluateAt(yVar, adjustedPrecision)
					.toBigDecimal(adjustedPrecision);

			if (fMi.subtract(fMi1, mc).abs().compareTo(precision.getAccuracy()) < 0) {
				break;
			}

		}
	}
}
