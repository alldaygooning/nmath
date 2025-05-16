package nikita.math.solver.interpolate.bessel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.solver.interpolate.FunctionInterpolationException;
import nikita.math.solver.interpolate.FunctionInterpolationContext;
import nikita.math.solver.interpolate.FunctionInterpolator;
import nikita.math.solver.interpolate.gauss.GaussFunctionInterpolator;
import nikita.math.solver.interpolate.newton.NewtonFunctionInterpolator;

public class BesselFunctionInterpolator extends FunctionInterpolator {
	private static final String FULL_NAME = "Bessel Function Interpolator";
	private static final String LOGGER_NAME = "BesselFunctionInterpolator";
	private static final String SHORT_NAME = "bessel";

	private GaussFunctionInterpolator gauss = new GaussFunctionInterpolator();

	public BesselFunctionInterpolator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
		this.requiresUniformity = true;
	}

	@Override
	public Expression interpolate(List<Point> points, Precision precision, FunctionInterpolationContext context) {
		MathContext mc = precision.getMathContext();

		Expression t = new Expression("t");
		List<List<BigDecimal>> differences = NewtonFunctionInterpolator.getFiniteDifferences(points, precision);
		Expression forward = gauss.forward(differences, points, t, precision);
		Expression backward = gauss.backward(differences, points, t, +1, precision).evaluateAt(new Variable("t", new Expression("t-1")),
				precision);

		Expression interpolated = forward.add(backward, precision);
		interpolated = interpolated.divide(Expression.TWO, precision);

		int midIndex = points.size() / 2 - 1;
		Point a = points.get(midIndex);
		Point b = points.get(midIndex + 1);

		BigDecimal h = points.get(1).getX().subtract(points.get(0).getX(), mc);
		BigDecimal mid = a.getX().add(b.getX(), mc).divide(BigDecimal.valueOf(2), mc);
		Expression x0 = new Expression(String.format("(x-(%s))/(%s)", mid.toPlainString(), h.toPlainString()));

		interpolated = interpolated.evaluateAt(new Variable("t", x0), precision);

		return interpolated;
	}

	@Override
	protected void check(List<Point> points, BigDecimal x, Precision precision) {
		super.check(points, x, precision);
		if (points.size() % 2 != 0) {
			throw new FunctionInterpolationException(this, "Method requires even number of points");
		}
	}

}
