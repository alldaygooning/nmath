package nikita.math.construct.point.singularity;

import java.util.ArrayList;
import java.util.List;

import nikita.math.construct.expression.Expression;

public abstract class DiscontinuityPoint extends Discontinuity {

	private static final String NAME = "Generic Discontinuity Point";

	Expression point;
	
	public DiscontinuityPoint(Expression expression, Expression point) {
		super(expression);
		name = NAME;

		this.point = point;
	}

	public boolean isRemovable() {
		return this instanceof RemovableDiscontinuity;
	}

	public boolean isJump() {
		return this instanceof JumpDiscontinuity;
	}

	public boolean isInfinite() {
		return this instanceof InfiniteDiscontinuity;
	}
	
	public boolean overlaps(DiscontinuityPoint other) {
		return this.equals(other);
	}

	@Override
	public String toBeautifulString() {
		return String.format("%s of Expression '%s' at point '%s'.", name, expression, point);
	}

	@Override
	public String toShortString() {
		return String.format("%s of Expression at point '%s'.", name, point);
	}

	public static List<DiscontinuityPoint> decouplePoints(List<DiscontinuityPoint> discontinuityPoints) {
		List<DiscontinuityPoint> decoupledPoints = new ArrayList<DiscontinuityPoint>();
		for (DiscontinuityPoint discPoint : discontinuityPoints) {
			boolean overlaps = false;
			for (DiscontinuityPoint decoupledPnt : decoupledPoints) {
				if (discPoint.overlaps(decoupledPnt)) {
					overlaps = true;
					break;
				}
			}
			if (!overlaps) {
				decoupledPoints.add(discPoint);
			}
		}
		return decoupledPoints;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DiscontinuityPoint) {
			DiscontinuityPoint other = (DiscontinuityPoint) obj;
			return (this.expression.equals(other.expression) && this.point.equals(other.point));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return expression.hashCode() + point.hashCode();
	}
}
