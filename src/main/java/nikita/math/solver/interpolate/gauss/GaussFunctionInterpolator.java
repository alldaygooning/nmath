package nikita.math.solver.interpolate.gauss;

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

public class GaussFunctionInterpolator extends FunctionInterpolator {
	private static final String FULL_NAME = "Gauss Function Interpolator";
	private static final String LOGGER_NAME = "GaussFunctionInterpolator";
	private static final String SHORT_NAME = "gauss";

	public GaussFunctionInterpolator() {
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

		if (context.getX().compareTo(mid) >= 0) {
			return forward(differences, points, t, precision);
		}
		return backward(differences, points, t, precision);
	}

	public Expression forward(List<List<BigDecimal>> differences, List<Point> points, Expression t, Precision precision) {
		return this.forward(differences, points, t, 0, precision);
	}

	public Expression forward(List<List<BigDecimal>> differences, List<Point> points, Expression t, int shift, Precision precision) {
		int midIndex = points.size() / 2;
		Expression interpolated = new Expression(points.get(midIndex).getY().toPlainString());

		int set = midIndex;
		for (int i = 1; i < points.size(); i++) {
			Expression numerator = this.tForward(t, i, precision);
			Expression denominator = new Expression(String.valueOf(CombinatoricsUtils.factorial(i)));
			Expression part = numerator.divide(denominator, precision);

			List<BigDecimal> step = differences.get(i);
			BigDecimal difference = step.get(set + shift);
			part = part.multiply(new Expression(difference.toPlainString()), precision);

			interpolated = interpolated.add(part, precision);
			if (i % 2 == 1) {
				set--;
			}
		}

		return interpolated;
	}

	public Expression backward(List<List<BigDecimal>> differences, List<Point> points, Expression t, Precision precision) {
		return this.backward(differences, points, t, 0, precision);
	}

	public Expression backward(List<List<BigDecimal>> differences, List<Point> points, Expression t, int shift, Precision precision) {
		int midIndex = points.size() / 2;
		Expression interpolated = new Expression(points.get(midIndex).getY().toPlainString());

		int set = midIndex - 1;
		for (int i = 1; i < points.size(); i++) {
			Expression numerator = this.tBackward(t, i, precision);
			Expression denominator = new Expression(String.valueOf(CombinatoricsUtils.factorial(i)));
			Expression part = numerator.divide(denominator, precision);

			List<BigDecimal> step = differences.get(i);
			BigDecimal difference = step.get(set + shift);
			part = part.multiply(new Expression(difference.toPlainString()), precision);

			interpolated = interpolated.add(part, precision);
			if (i % 2 == 0) {
				set--;
			}
		}

		return interpolated;
	}

	private Expression tForward(Expression initial, int order, Precision precision) {
		Expression result = initial;
		int delta = 1;
		for (int i = 2; i < order + 1; i++) {
			if (i % 2 == 0) {
				result = result.multiply(initial.sub(new Expression(String.valueOf(delta)), precision), precision);
			} else if (i % 2 == 1) {
				result = result.multiply(initial.add(new Expression(String.valueOf(delta)), precision), precision);
				delta++;
			}
		}
		return result;
	}

	private Expression tBackward(Expression initial, int order, Precision precision) {
		Expression result = initial;
		int delta = 1;
		for (int i = 2; i < order + 1; i++) {
			if (i % 2 == 0) {
				result = result.multiply(initial.add(new Expression(String.valueOf(delta)), precision), precision);
			} else if (i % 2 == 1) {
				result = result.multiply(initial.sub(new Expression(String.valueOf(delta)), precision), precision);
				delta++;
			}
		}
		return result;
	}

	@Override
	protected void check(List<Point> points, BigDecimal x, Precision precision) {
		super.check(points, x, precision);
		if (points.size() % 2 == 0) {
			throw new FunctionInterpolationException(this, "Method requires odd number of points");
		}
	}

}
