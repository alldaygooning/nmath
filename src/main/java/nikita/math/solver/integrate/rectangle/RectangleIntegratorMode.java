package nikita.math.solver.integrate.rectangle;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.solver.integrate.IntegratorMode;

public enum RectangleIntegratorMode implements IntegratorMode {
	LEFT("Left", "left"),
	RIGHT("Right", "right"),
	AVG("Average", "avg");
	
	private final String prefix;
	private final String shorthand;

	private RectangleIntegratorMode(String prefix, String shorthand) {
		this.prefix = prefix;
		this.shorthand = shorthand;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public List<Point> filter(List<Point> partitionPoints, Expression expression, Precision precision) {
		int size = partitionPoints.size();

		switch (this) {
		case LEFT: {
			return new ArrayList<>(partitionPoints.subList(0, size - 1));
		}
		case RIGHT: {
			return new ArrayList<>(partitionPoints.subList(1, size));
		}
		case AVG: {
			List<Point> filtered = new ArrayList<Point>();
			MathContext mc = precision.getMathContext();
			for (int i = 0; i < size - 1; i++) {
				BigDecimal x1 = partitionPoints.get(i).getX();
				BigDecimal x2 = partitionPoints.get(i + 1).getX();

				BigDecimal xmid = x1.add(x2.subtract(x1, mc).divide(BigDecimal.valueOf(2), mc), mc);
				BigDecimal ymid = expression.evaluateAt(new Variable("x", xmid), precision).toBigDecimal(precision);
				filtered.add(new Point(xmid, ymid));
			}
			return filtered;
		}
		default:
			throw new UnsupportedOperationException("Unsupported mode: " + this);
		}
	}

	public static RectangleIntegratorMode getByShorthand(String shorthand) {
		for (RectangleIntegratorMode mode : RectangleIntegratorMode.values()) {
			if (mode.shorthand.equalsIgnoreCase(shorthand)) {
				return mode;
			}
		}
		throw new IllegalArgumentException("No RectanlgeIntegratorMode with shorthand " + shorthand);
	}
}
