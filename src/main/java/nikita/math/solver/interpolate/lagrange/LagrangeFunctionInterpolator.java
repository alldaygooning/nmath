package nikita.math.solver.interpolate.lagrange;

import java.math.BigDecimal;
import java.util.List;

import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.solver.interpolate.FunctionInterpolationContext;
import nikita.math.solver.interpolate.FunctionInterpolator;

public class LagrangeFunctionInterpolator extends FunctionInterpolator {

	private static final String FULL_NAME = "Lagrange Function Interpolator";
	private static final String LOGGER_NAME = "LagrangeFunctionInterpolator";
	private static final String SHORT_NAME = "lagrange";

	public LagrangeFunctionInterpolator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
	}

	@Override
	public Expression interpolate(List<Point> points, Precision precision, FunctionInterpolationContext context) {
		int n = points.size();
		Expression interpolated = Expression.ZERO;
		for(int i = 0; i < n; i++) {
			Expression lagrangePolynomial = lagrangePolynomial(i, points);
			interpolated = interpolated.add(lagrangePolynomial, precision);
		}
		return interpolated;
	}

	private Expression lagrangePolynomial(int n, List<Point> points) {
		Point nPoint = points.get(n);
		BigDecimal xn = nPoint.getX();
		BigDecimal yn = nPoint.getY();

		StringBuilder numBuilder = new StringBuilder();
		StringBuilder denBuilder = new StringBuilder();
		String numTemplate = "(x-(%s))";
		String denTemplate = "(" + xn.toPlainString() + "-(%s))";
		
		for (int i = 0; i < points.size(); i++) {
			if (i == n) {
				continue;
			}

			String xCurStr = points.get(i).getX().toPlainString();
			numBuilder.append(String.format(numTemplate, xCurStr));
			denBuilder.append(String.format(denTemplate, xCurStr));
		}
		return new Expression(String.format("((%s)/(%s))*(%s)", numBuilder.toString(), denBuilder.toString(), yn.toPlainString()));
	}
}
