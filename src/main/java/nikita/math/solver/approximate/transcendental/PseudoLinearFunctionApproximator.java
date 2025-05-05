package nikita.math.solver.approximate.transcendental;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;

import nikita.math.NMath;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.solver.approximate.FunctionApproximationException;
import nikita.math.solver.approximate.FunctionApproximator;
import nikita.math.solver.approximate.polynomial.PolynomialFunctionApproximator;

public abstract class PseudoLinearFunctionApproximator extends FunctionApproximator {

	public PseudoLinearFunctionApproximator(String fullName, String loggerName, String shortName) {
		super(fullName, loggerName, shortName);
	}

	protected Expression getConstant(Expression linearApproximated, Precision precision) {
		return this.coefficient(linearApproximated, 0, precision);
	}

	protected Expression getCoefficient(Expression linearApproximated, Precision precision) {
		return this.coefficient(linearApproximated, 1, precision);
	}

	private Expression coefficient(Expression expression, int exponent, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		OutputFormFactory off = NMath.getOutputFormFactory(precision);
		String coefficientCommand = String.format("Coefficient(%s, x, %s)", expression.getString(), exponent);
		return new Expression(off.toString(evaluator.eval(coefficientCommand)));
	}

	protected abstract Point substitutePoint(Point point, Precision precision);

	protected abstract boolean satisfies(Point point);

	protected abstract Expression getApproximated(Expression coefficient, Expression constant, Precision precision);

	protected List<Point> substitutePoints(List<Point> points, Precision precision) {
		List<Point> substituted = new ArrayList<Point>();
		for (Point point : points) {
			if (!this.satisfies(point)) {
				throw new FunctionApproximationException(this,
						String.format("This method requires (x; y) to be a set of positive numbers '%s'", point.toString()));
			}
			substituted.add(this.substitutePoint(point, precision));
		}
		return substituted;
	}

	@Override
	public Expression approximate(List<Point> points, Precision precision) {
		Precision adjustedPrecision = precision.getAdjustedPrecision(EXTRA_PRECISION);
		List<Point> substituted = substitutePoints(points, adjustedPrecision);

		PolynomialFunctionApproximator approximator = new PolynomialFunctionApproximator();
		approximator.setMode(1);
		Expression linearApproximated = approximator.approximate(substituted, precision);

		Expression coefficient = this.getCoefficient(linearApproximated, adjustedPrecision);
		Expression constant = this.getConstant(linearApproximated, adjustedPrecision);

		return this.getApproximated(coefficient, constant, adjustedPrecision);
	}
}
