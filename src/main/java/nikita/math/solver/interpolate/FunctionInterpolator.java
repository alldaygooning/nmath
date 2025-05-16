package nikita.math.solver.interpolate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;

public abstract class FunctionInterpolator {

	private static final String FULL_NAME_DEFAULT = "Abstract Function Interpolator";
	private static final String LOGGER_NAME_DEFAULT = "AbstractFunctionInterpolator";

	private String fullName;
	private String loggerName;
	private String shortName;

	public FunctionInterpolator(String fullName, String loggerName, String shortName) {
		this.fullName = fullName;
		this.loggerName = loggerName;
		this.shortName = shortName;
	}

	public abstract Expression interpolate(List<Point> points, Precision precision, FunctionInterpolationContext context);

	protected boolean isUniform(List<Point> points, Precision precision) {
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
}
