package nikita.math.solver.interpolate.bessel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import org.hipparchus.util.CombinatoricsUtils;

import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.solver.interpolate.FunctionInterpolationException;
import nikita.math.solver.interpolate.FunctionInterpolationContext;
import nikita.math.solver.interpolate.FunctionInterpolator;
import nikita.math.solver.interpolate.newton.NewtonFunctionInterpolator;

public class BesselFunctionInterpolator extends FunctionInterpolator {
	private static final String FULL_NAME = "Bessel Function Interpolator";
	private static final String LOGGER_NAME = "BesselFunctionInterpolator";
	private static final String SHORT_NAME = "bessel";

	public BesselFunctionInterpolator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
		this.requiresUniformity = true;
	}

	@Override
	public Expression interpolate(List<Point> points, Precision precision, FunctionInterpolationContext context) {
		MathContext mc = precision.getMathContext();

		BigDecimal h = points.get(1).getX().subtract(points.get(0).getX(), mc);
		int midIndex = points.size() / 2 - 1;
		BigDecimal mid = points.get(midIndex).getX();

		Expression t = new Expression(String.format("(x-(%s))/(%s)", mid.toPlainString(), h.toPlainString()));

		List<List<BigDecimal>> differences = NewtonFunctionInterpolator.getFiniteDifferences(points, precision);

		BigDecimal y0 = points.get(midIndex).getY();
		BigDecimal y1 = points.get(midIndex + 1).getY();
		BigDecimal avg = y0.add(y1, mc).divide(BigDecimal.valueOf(2), mc);
		Expression interpolated = new Expression(avg.toPlainString());

		for (int i = 1; i < points.size(); i++) {
			List<BigDecimal> step = differences.get(i);
			if (i % 2 == 1) {
				Expression term = t.sub(new Expression("0.5"), precision);
				term = term.multiply(ucal(t, i - 1, precision), precision);
				term = term.multiply(new Expression(step.get(midIndex).toPlainString()), precision);
				term = term.divide(new Expression(String.valueOf(CombinatoricsUtils.factorial(i))), precision);
				interpolated = interpolated.add(term, precision);
			} else {
				BigDecimal diffSum = step.get(midIndex).add(step.get(midIndex - 1), mc);
				Expression term = ucal(t, i, precision);
				term = term.multiply(new Expression(diffSum.toPlainString()), precision);
				term = term.divide(new Expression(String.valueOf(2 * CombinatoricsUtils.factorial(i))), precision);
				interpolated = interpolated.add(term, precision);
				midIndex--;
			}
		}

		return interpolated;
	}

	private Expression ucal(Expression u, int n, Precision precision) {
		if (n == 0) {
			return new Expression("1");
		}

		Expression result = u;
		for (int i = 1; i <= n / 2; i++) {
			result = result.multiply(u.sub(new Expression(String.valueOf(i)), precision), precision);
		}

		for (int i = 1; i < n / 2; i++) {
			result = result.multiply(u.add(new Expression(String.valueOf(i)), precision), precision);
		}

		return result;
	}

	@Override
	protected void check(List<Point> points, BigDecimal x, Precision precision) {
		super.check(points, x, precision);
		if (points.size() % 2 != 0) {
			throw new FunctionInterpolationException(this, "Method requires even number of points");
		}
	}
}