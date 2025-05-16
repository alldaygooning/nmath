package nikita.math.solver.interpolate.newton;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.solver.interpolate.FunctionInterpolationException;
import nikita.math.solver.interpolate.FunctionInterpolationContext;
import nikita.math.solver.interpolate.FunctionInterpolator;

public class NewtonFunctionInterpolator extends FunctionInterpolator {

	private static final String FULL_NAME = "Newton Function Interpolator";
	private static final String LOGGER_NAME = "NewtonFunctionInterpolator";
	private static final String SHORT_NAME = "newton";

	public NewtonFunctionInterpolator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
	}

	@Override
	public Expression interpolate(List<Point> points, Precision precision, FunctionInterpolationContext context) {
//		if (this.isUniform(points, precision)) {
//			return finite(points, precision);
//		}
		List<BigDecimal> coefficients = divided(points, precision);
		Expression interpolated = Expression.ZERO;
		for (int i = 0; i < points.size(); i++) {
			Expression coefficient = new Expression(coefficients.get(i).toPlainString());
			for (int times = 0; times < i; times++) {
				Expression multiplier = new Expression(String.format("x-(%s)", points.get(times).getX()));
				coefficient = coefficient.multiply(multiplier, precision);
			}
			interpolated = interpolated.add(coefficient, precision);
		}
		return interpolated;
	}

	private List<BigDecimal> divided(List<Point> points, Precision precision) {
		List<List<BigDecimal>> differences = this.getDividedDifferences(points, precision);
		List<BigDecimal> coefficients = new ArrayList<BigDecimal>();
		for (List<BigDecimal> difference : differences) {
			coefficients.add(difference.get(0));
		}
		return coefficients;
	}

	@SuppressWarnings("unused")
	private List<BigDecimal> finite(List<Point> points, Precision precision) {
		throw new FunctionInterpolationException(this, "Finite Difference variation is not supported");
	}

	private List<List<BigDecimal>> getDividedDifferences(List<Point> points, Precision precision) {
		MathContext mc = precision.getMathContext();
		int n = points.size();
		List<List<BigDecimal>> differences = IntStream.range(0, n)
				.mapToObj(i -> new ArrayList<BigDecimal>()) //
				.collect(Collectors.toList()); //
		points.forEach(point -> differences.get(0).add(point.getY()));
		for (int step = 1; step < n; step++) {
			List<BigDecimal> previous = differences.get(step - 1);
			List<BigDecimal> current = differences.get(step);
			for (int set = 0; set < previous.size() - 1; set++) {
				BigDecimal fa = previous.get(set + 1);
				BigDecimal fb = previous.get(set);
				BigDecimal numerator = fa.subtract(fb, mc);

				BigDecimal a = points.get(set + step).getX();
				BigDecimal b = points.get(set).getX();
				BigDecimal denominator = a.subtract(b, mc);

				current.add(numerator.divide(denominator, mc));
			}
		}
		return differences;
	}

	public static List<List<BigDecimal>> getFiniteDifferences(List<Point> points, Precision precision) {
		MathContext mc = precision.getMathContext();
		int n = points.size();
		List<List<BigDecimal>> differences = IntStream.range(0, points.size())
				.mapToObj(i -> new ArrayList<BigDecimal>()) //
				.collect(Collectors.toList()); //
		points.forEach(point -> differences.get(0).add(point.getY()));
		for (int step = 1; step < n; step++) {
			List<BigDecimal> previous = differences.get(step - 1);
			List<BigDecimal> current = differences.get(step);
			for (int set = 0; set < previous.size() - 1; set++) {
				BigDecimal a = previous.get(set + 1);
				BigDecimal b = previous.get(set);

				current.add(a.subtract(b, mc));
			}
		}

		return differences;
	}
}
