package nikita.math.solver.approximate;

import java.util.List;
import java.util.Map;

import nikita.logging.NLogger;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.solver.approximate.FunctionApproximationException;
import nikita.math.solver.approximate.polynomial.PolynomialFunctionApproximator;
import nikita.math.solver.approximate.transcendental.exponential.ExponentialFunctionApproximator;
import nikita.math.solver.approximate.transcendental.logarithmic.LogarithmicFunctionApproximator;
import nikita.math.solver.approximate.transcendental.power.PowerFunctionApproximator;

public abstract class FunctionApproximator {

	protected static final int EXTRA_PRECISION = 5;

	static final String LOG_STEP_SEPARATOR = ("=").repeat(120);

	private static final String FULL_NAME_DEFAULT = "Abstract Function Approximator";
	private static final String LOGGER_NAME_DEFAULT = "AbstractFunctionApproximator";

	private String fullName;
	private String loggerName;
	private String shortName;

	private static final PolynomialFunctionApproximator POLYNOMIAL_APPROXIMATOR = new PolynomialFunctionApproximator();
	private static final PowerFunctionApproximator POWER_APPROXIMATOR = new PowerFunctionApproximator();
	private static final LogarithmicFunctionApproximator LOG_APPROXIMATOR = new LogarithmicFunctionApproximator();
	private static final ExponentialFunctionApproximator EXP_APPROXIMATOR = new ExponentialFunctionApproximator();

	public static final Map<String, FunctionApproximator> APPROXIMATORS = Map.ofEntries( //
			Map.entry(POLYNOMIAL_APPROXIMATOR.getShortName(), POLYNOMIAL_APPROXIMATOR),
			Map.entry(POWER_APPROXIMATOR.getShortName(), POWER_APPROXIMATOR), Map.entry(LOG_APPROXIMATOR.getShortName(), LOG_APPROXIMATOR),
			Map.entry(EXP_APPROXIMATOR.getShortName(), EXP_APPROXIMATOR));

	public FunctionApproximator(String fullName, String loggerName, String shortName) {
		this.fullName = fullName;
		this.loggerName = loggerName;
		this.shortName = shortName;
	}

	public abstract Expression approximate(List<Point> points, Precision precision);

	public static FunctionApproximation approximate(List<Point> points, Precision precision, String method) {
		String[] input = method.split("\\-");
		String shortName = input[0];
		String mode = null;
		if (input.length > 1) {
			mode = input[1];
		}

		FunctionApproximator approximator = getApproximator(shortName, mode);

		logSeparator();
		NLogger.info(String.format("Approximating Function using '%s' for the following data:\n%s", approximator.getFullName(),
				getTableString(points)));

		Expression approximated = approximator.approximate(points, precision);
		FunctionApproximation approximation = new FunctionApproximation(approximated, points, precision);
		if (approximator instanceof PolynomialFunctionApproximator && ((PolynomialFunctionApproximator) approximator).getMode() == 1) {
			approximation.setCorrelation(approximation.calculateCorrelation());
		}

		approximator.info(String.format("Final Approximation: %s", approximation.toBeautifulString()));
		logSeparator();
		return approximation;
	}

	public static FunctionApproximator getApproximator(String shortName, String mode) {
		int modeInt = 0;

		if (!APPROXIMATORS.containsKey(shortName)) {
			throw new FunctionApproximationException(String.format("No method found but short name '%s'", shortName));
		}

		FunctionApproximator approximator = APPROXIMATORS.get(shortName);
		if (approximator.isMultimodal()) {
			if (mode == null) {
				throw new FunctionApproximationException(approximator, "This method requires 'mode' parameter to work");
			}
			MultimodalApproximator multimodal = (MultimodalApproximator) approximator;
			try {
				modeInt = Integer.parseInt(mode);
				if (modeInt <= 0) {
					throw new NumberFormatException();
				}
				multimodal.setMode(modeInt);
			} catch (NumberFormatException e) {
				throw new FunctionApproximationException(approximator,
						String.format("'mode' paramter ('%s') has to be a Natural number", mode));
			}
		}
		return approximator;
	}

	public String getFullName() {
		if (fullName != null) {
			if (this.isMultimodal()) {
				MultimodalApproximator multimodal = (MultimodalApproximator) this;
				return String.format("%s %s", multimodal.getFullNamePrefix(multimodal.getMode()), fullName);
			}
			return fullName;
		}
		return FULL_NAME_DEFAULT;
	}

	public String getLoggerName() {
		if (loggerName != null) {
			if (this.isMultimodal()) {
				MultimodalApproximator multimodal = (MultimodalApproximator) this;
				return String.format("%s%s", multimodal.getLoggerPrefix(multimodal.getMode()), loggerName);
			}
			return loggerName;
		}
		return LOGGER_NAME_DEFAULT;
	}

	public String getShortName() {
		return shortName;
	}

	public boolean isMultimodal() {
		return this instanceof MultimodalApproximator;
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

	protected void info(String string) {
		NLogger.info(String.format("%s: %s", this.getLoggerName(), string));
	}

	private static void logSeparator() {
		NLogger.info(LOG_STEP_SEPARATOR);
	}
}
