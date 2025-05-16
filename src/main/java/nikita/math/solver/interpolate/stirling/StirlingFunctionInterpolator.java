package nikita.math.solver.interpolate.stirling;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.solver.interpolate.FunctionInterpolationException;
import nikita.math.solver.interpolate.FunctionInterpolationContext;
import nikita.math.solver.interpolate.FunctionInterpolator;
import nikita.math.solver.interpolate.gauss.GaussFunctionInterpolator;
import nikita.math.solver.interpolate.newton.NewtonFunctionInterpolator;

public class StirlingFunctionInterpolator extends FunctionInterpolator {

	private static final String FULL_NAME = "Stirling Function Interpolator";
	private static final String LOGGER_NAME = "StirlingFunctionInterpolator";
	private static final String SHORT_NAME = "stirling";

	private GaussFunctionInterpolator gauss = new GaussFunctionInterpolator();

	public StirlingFunctionInterpolator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
		this.requiresUniformity = true;
	}

	@Override
	public Expression interpolate(List<Point> points, Precision precision, FunctionInterpolationContext context) {
		MathContext mc = precision.getMathContext();

		BigDecimal h = points.get(1).getX().subtract(points.get(0).getX(), mc);
		BigDecimal mid = points.get(points.size() / 2).getX();
		Expression t = new Expression(String.format("(x-(%s))/(%s)", mid.toPlainString(), h.toPlainString()));
		List<List<BigDecimal>> differences = NewtonFunctionInterpolator.getFiniteDifferences(points, precision);

		Expression forward = gauss.forward(differences, points, t, precision);
		Expression backward = gauss.backward(differences, points, t, precision);

		Expression interpolated = forward.add(backward, precision);
		interpolated = interpolated.divide(Expression.TWO, precision);

		return interpolated;
	}

	@Override
	protected void check(List<Point> points, BigDecimal x, Precision precision) {
		super.check(points, x, precision);
		if (points.size() % 2 == 0) {
			throw new FunctionInterpolationException(this, "Method requires odd number of points");
		}
	}

}
