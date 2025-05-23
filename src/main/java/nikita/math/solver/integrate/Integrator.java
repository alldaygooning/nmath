package nikita.math.solver.integrate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nikita.logging.NLogger;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.calculus.integral.Integral;
import nikita.math.construct.calculus.integral.IntegralKind;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.construct.integral.IntegrationException;
import nikita.math.solver.integrate.rectangle.RectangleIntegrator;
import nikita.math.solver.integrate.simpson.SimpsonIntegrator;
import nikita.math.solver.integrate.trapazoid.TrapazoidIntegrator;

public abstract class Integrator {

	public static final int EXTRA_PRECISION = 5;

	static final String LOG_STEP_SEPARATOR = ("=").repeat(50);

	private static final Integrator SIMPSON = new SimpsonIntegrator();
	private static final Integrator TRAPAZOID = new TrapazoidIntegrator();
	private static final Integrator RECTANGLE = new RectangleIntegrator();

	public static final Map<String, Integrator> INTEGRATORS = Map.ofEntries(
			Map.entry(SIMPSON.getShortName(), SIMPSON),
			Map.entry(TRAPAZOID.getShortName(), TRAPAZOID),
			Map.entry(RECTANGLE.getShortName(), RECTANGLE)
	);

	public abstract BigDecimal evaluate(Integral integral, int n, Precision precision);

	public abstract String getFullName();

	public abstract String getLogName();

	public abstract String getShortName();

	public int getOrder() {
		return 2;
	}

	public static void evaluate(Integral integral, int n, Precision precision, String method) {
		String mode = "placeholder";
		if (method.contains("-")) {
			String[] input = method.split("\\-");
			method = input[0];
			if (input.length > 1) {
				mode = input[1];
			}
		}

		if (!INTEGRATORS.containsKey(method)) {
			throw new IntegrationException(integral, method, "method not found");
		}

		Integrator integrator = INTEGRATORS.get(method);
		if (integrator instanceof Multimodal) {
			((Multimodal) integrator).setMode(mode);
		}
		integrator.info(String.format("Evaluating %s using %s.", integral.toBeautifulString(), integrator.getFullName()));
		if (integral.getKind() == IntegralKind.SECOND) {
			if (!integral.isConvergent()) {
				throw new IntegrationException(integral, method, "Integral does not converge");
			}
			List<BigDecimal> pp = integral.getPartitionPoints();
			BigDecimal total = BigDecimal.ZERO;
			MathContext mc = precision.getMathContext();
			for (int i = 0; i < pp.size() - 1; i++) {
				Integral subintegral = new Integral(integral.getIntegrand(), integral.getVariable(),
						pp.get(i).add(precision.getAccuracy(), mc), pp.get(i + 1).subtract(precision.getAccuracy(), mc));
				integrator.info(String.format("Evaluating Sub%s using %s.", subintegral.toBeautifulString(), integrator.getFullName()));
				total = total.add(integrator.evaluate(subintegral, n, precision), mc);
			}
			integrator.info(String.format("I = %s", total.toPlainString()));
			return;
		}
		integrator.evaluate(integral, n, precision);
	}

	public static List<Point> getPartitionPoints(Expression expression, Interval interval, int n, Precision precision) {
		MathContext mc = precision.getMathContext();

		List<Point> partitionPoints = new ArrayList<Point>();

		BigDecimal subintervalLength = interval.getLength().divide(BigDecimal.valueOf(n), mc);
		BigDecimal x = interval.getLeft();
		BigDecimal y = expression.evaluateAt(new Variable("x", x)).toBigDecimal();
		partitionPoints.add(new Point(x, y));
		for (int i = 1; i <= n; i++) {
			x = x.add(subintervalLength, mc);
			y = expression.evaluateAt(new Variable("x", x)).toBigDecimal();
			partitionPoints.add(new Point(x, y));
		}
		return partitionPoints;
	}

	public boolean rungeCheck(BigDecimal int1, BigDecimal int2, Precision precision) {
		MathContext mc = precision.getMathContext();
		BigDecimal numerator = int1.subtract(int2, mc).abs();
		BigDecimal denominator = new BigDecimal(Math.pow(2, this.getOrder()) - 1, mc);
		BigDecimal result = numerator.divide(denominator, mc);

		return result.compareTo(precision.getAccuracy()) < 0;
	}

	public void info(String message) {
		NLogger.info(String.format("%s : %s", this.getLogName(), message));
	}

	protected void logStep(String message) {
		info(message + "\n" + LOG_STEP_SEPARATOR);
	}

	protected void logStep(int n, BigDecimal subintervalLength, List<Point> partitionPoints, BigDecimal value) {
		StringBuilder builder = new StringBuilder();
		int size = partitionPoints.size();
		if (size > 16) {
			for (int i = 0; i < 15; i++) {
				builder.append(partitionPoints.get(i).toString());
				builder.append(", ");
			}
			builder.append("..., ");
			builder.append(partitionPoints.get(size - 1).toString());
		} else {
			for (int i = 0; i < size; i++) {
				builder.append(partitionPoints.get(i).toString());
				if (i != size - 1) {
					builder.append(", ");
				}
			}
		}
		String ppString = builder.toString();
		this.logStep(String.format("Integration step n = %s h = %s\nPartition points:\n%s\nI = %s", n, subintervalLength.toPlainString(),
				ppString, value));
	}

}
