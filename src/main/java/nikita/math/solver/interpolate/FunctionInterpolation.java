package nikita.math.solver.interpolate;

import java.math.BigDecimal;

import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;

public class FunctionInterpolation {

	private Expression interpolated;
	private Precision precision;

	private Point point;

	public FunctionInterpolation(Expression interpolated, BigDecimal x, Precision precision) {
		this.interpolated = interpolated;
		this.precision = precision;

		BigDecimal y = this.interpolated.evaluateAt(new Variable("x", x), precision).toBigDecimal(precision);
		this.point = new Point(x, y);
	}

	public Expression getInterpolated() {
		return interpolated;
	}

	public void setInterpolated(Expression interpolated) {
		this.interpolated = interpolated;
	}

	public String toBeautifulString() {
		return String.format("Interpolation Polynomial: %s\nPoint: %s", interpolated.toString(precision), point.toString());
	}
}
