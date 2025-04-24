package nikita.math.construct.point.singularity;

import nikita.math.construct.expression.Expression;

public class RemovableDiscontinuity extends DiscontinuityPoint {
	
	private static final String NAME = "Removable Discontinuity";

	public RemovableDiscontinuity(Expression expression, Expression point) {
		super(expression, point);
		name = NAME;
	}

}
