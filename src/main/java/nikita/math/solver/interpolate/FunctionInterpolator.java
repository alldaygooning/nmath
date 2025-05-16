package nikita.math.solver.interpolate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nikita.logging.NLogger;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.solver.interpolate.FunctionInterpolationException;
import nikita.math.solver.interpolate.bessel.BesselFunctionInterpolator;
import nikita.math.solver.interpolate.gauss.GaussFunctionInterpolator;
import nikita.math.solver.interpolate.lagrange.LagrangeFunctionInterpolator;
import nikita.math.solver.interpolate.newton.NewtonFunctionInterpolator;
import nikita.math.solver.interpolate.stirling.StirlingFunctionInterpolator;

public abstract class FunctionInterpolator {

	static final String LOG_STEP_SEPARATOR = ("=").repeat(120);

	private static final String FULL_NAME_DEFAULT = "Abstract Function Interpolator";
	private static final String LOGGER_NAME_DEFAULT = "AbstractFunctionInterpolator";

	private String fullName;
	private String loggerName;
	private String shortName;

	protected int minPointsRequired = 2;
	protected boolean requiresUniformity = false;

	private static final LagrangeFunctionInterpolator LAGRANGE_INTERPOLATOR = new LagrangeFunctionInterpolator();
	private static final NewtonFunctionInterpolator NEWTON_INTERPOLATOR = new NewtonFunctionInterpolator();
	private static final GaussFunctionInterpolator GAUSS_INTERPOLATOR = new GaussFunctionInterpolator();
	private static final StirlingFunctionInterpolator STIRLING_INTERPOLATOR = new StirlingFunctionInterpolator();
	private static final BesselFunctionInterpolator BESSEL_INTERPOLATOR = new BesselFunctionInterpolator();

	public static final Map<String, FunctionInterpolator> INTERPOLATORS = Map.ofEntries(//
			Map.entry(LAGRANGE_INTERPOLATOR.getShortName(), LAGRANGE_INTERPOLATOR),
			Map.entry(NEWTON_INTERPOLATOR.getShortName(), NEWTON_INTERPOLATOR),
			Map.entry(GAUSS_INTERPOLATOR.getShortName(), GAUSS_INTERPOLATOR),
			Map.entry(STIRLING_INTERPOLATOR.getShortName(), STIRLING_INTERPOLATOR),
			Map.entry(BESSEL_INTERPOLATOR.getShortName(), BESSEL_INTERPOLATOR));

	public FunctionInterpolator(String fullName, String loggerName, String shortName) {
		this.fullName = fullName;
		this.loggerName = loggerName;
		this.shortName = shortName;
	}

	public abstract Expression interpolate(List<Point> points, Precision precision, FunctionInterpolationContext context);

	protected void check(List<Point> points, BigDecimal x, Precision precision) {
		if (!isBound(points, x)) {
			throw new FunctionInterpolationException(String.format("Interpolation Point 'x' should be bound within [%s; %s]",
					points.get(0).getX().toPlainString(), points.get(points.size() - 1).getX().toPlainString()));
		}

		if (this.minPointsRequired > points.size()) {
			throw new FunctionInterpolationException(this, String.format("Method requires at least %s points", this.minPointsRequired));
		}

		if (this.requiresUniformity && !isUniform(points, precision)) {
			throw new FunctionInterpolationException(this, "Method requires the grid to be uniform");
		}
	}

	public static FunctionInterpolation interpolate(List<Point> points, Precision precision, String method,
			FunctionInterpolationContext context) {
		FunctionInterpolator interpolator = getInterpolator(method);
		points = sort(points);
		interpolator.check(points, context.getX(), precision);

		logSeparator();
		NLogger.info(String.format("Interpolation Function using '%s' for the following data:\n%s", interpolator.getFullName(),
				getTableString(points)));
		Expression interpolated = interpolator.interpolate(points, precision, context);
		FunctionInterpolation interpolation = new FunctionInterpolation(interpolated, context.getX(), precision);
		interpolator.info(String.format("Final Interpolation: %s", interpolation.toBeautifulString()));
		logSeparator();
		return interpolation;
	}

	public static FunctionInterpolator getInterpolator(String shortName) {
		if (!INTERPOLATORS.containsKey(shortName)) {
			throw new FunctionInterpolationException(String.format("No method found but short name '%s'", shortName));
		}
		FunctionInterpolator interpolator = INTERPOLATORS.get(shortName);
		return interpolator;
	}

	protected static boolean isUniform(List<Point> points, Precision precision) {
		MathContext mc = precision.getMathContext();

		BigDecimal h = null;
		for (int i = 0; i < points.size() - 1; i++) {
			Point a = points.get(i);
			Point b = points.get(i + 1);

			BigDecimal distance = a.getX().subtract(b.getX(), mc);
			if (h == null) {
				h = distance;
				continue;
			}

			else if (h.compareTo(distance) != 0) {
				return false;
			}
		}

		return true;
	}

	protected static List<Point> sort(List<Point> points) {
		points.sort((p1, p2) -> {
			int cmp = p1.getX().compareTo(p2.getX());
			if (cmp != 0) {
				return cmp;
			}
			return p1.getY().compareTo(p2.getY());
		});

		List<Point> uniquePoints = new ArrayList<>();
		if (!points.isEmpty()) {
			uniquePoints.add(points.get(0));
			for (int i = 1; i < points.size(); i++) {
				Point previous = uniquePoints.get(uniquePoints.size() - 1);
				Point current = points.get(i);
				if (previous.getX().compareTo(current.getX()) != 0 || previous.getY().compareTo(current.getY()) != 0) {
					uniquePoints.add(current);
				}
			}
		}
		return uniquePoints;
	}

	protected static boolean isBound(List<Point> points, BigDecimal x) {
		BigDecimal left = points.get(0).getX();
		BigDecimal right = points.get(points.size() - 1).getX();
		if (x.compareTo(left) < 0 || x.compareTo(right) > 0) {
			return false;
		}
		return true;
	}

	public String getFullName() {
		if (fullName != null) {
			return fullName;
		}
		return FULL_NAME_DEFAULT;
	}

	public String getLoggerName() {
		if (loggerName != null) {
			return loggerName;
		}
		return LOGGER_NAME_DEFAULT;
	}

	public String getShortName() {
		return shortName;
	}

	protected void info(String string) {
		NLogger.info(String.format("%s: %s", this.getLoggerName(), string));
	}

	private static void logSeparator() {
		NLogger.info(LOG_STEP_SEPARATOR);
	}

	private static String getTableString(List<Point> points) {
		StringBuilder table = new StringBuilder();
		int totalPoints = points.size();

		if (totalPoints > 16) {
			for (int i = 0; i < 15; i++) {
				table.append(points.get(i).toString()).append(System.lineSeparator());
			}
			table.append("...").append(System.lineSeparator());
			table.append(points.get(totalPoints - 1).toString());
		} else {
			for (int i = 0; i < totalPoints; i++) {
				table.append(points.get(i).toString());
				if (i < totalPoints - 1) {
					table.append(System.lineSeparator());
				}
			}
		}

		return table.toString();
	}
}
