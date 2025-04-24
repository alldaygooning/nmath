package nikita.math.construct.point.singularity;

import nikita.math.construct.expression.Expression;

public class JumpDiscontinuity extends DiscontinuityPoint {

	private static final String NAME = "Jump Discontinuity";

	public JumpDiscontinuity(Expression expression, Expression point) {
		super(expression, point);
		name = NAME;
	}
}
