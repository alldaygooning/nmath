package nikita.math.solver.interpolate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;

public class FunctionInterpolation {

	private Expression interpolated;
	private Precision precision;

	private Point point;
	Optional<List<List<BigDecimal>>> differences;

	public FunctionInterpolation(Expression interpolated, BigDecimal x, Precision precision) {
		this.interpolated = interpolated;
		this.setPrecision(precision);

		BigDecimal y = this.interpolated.evaluateAt(new Variable("x", x), precision).toBigDecimal(precision);
		this.setPoint(new Point(x, y));

		this.differences = Optional.empty();
	}

	public Expression getInterpolated() {
		return interpolated;
	}

	public void setInterpolated(Expression interpolated) {
		this.interpolated = interpolated;
	}

	public String toBeautifulString() {
		return String.format("Interpolation Polynomial: %s\nPoint: %s", interpolated.toString(getPrecision()), getPoint().toString());
	}

	public Precision getPrecision() {
		return precision;
	}

	public void setPrecision(Precision precision) {
		this.precision = precision;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public void setDifferences(List<List<BigDecimal>> differences) {
		this.differences = Optional.of(differences);
	}

	public Optional<List<List<BigDecimal>>> getDifferences() {
		return differences;
	}
}
