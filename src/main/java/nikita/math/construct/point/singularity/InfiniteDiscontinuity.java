package nikita.math.construct.point.singularity;

import nikita.math.construct.expression.Expression;

public class InfiniteDiscontinuity extends DiscontinuityPoint {

	private static final String NAME = "Infinite Discontinuity";

	public InfiniteDiscontinuity(Expression expression, Expression point) {
		super(expression, point);
		name = NAME;
	}

}
