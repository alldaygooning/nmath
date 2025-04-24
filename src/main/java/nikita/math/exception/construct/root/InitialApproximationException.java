package nikita.math.exception.construct.root;

import nikita.math.construct.Interval;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.expression.ExpressionSystem;
import nikita.math.construct.point.Point;

public class InitialApproximationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InitialApproximationException(Expression expression, Interval interval) {
		super(String.format("Unable to get initial approximation for root of Expression '%s' on Interval %s.", expression, interval));
	}

	public InitialApproximationException(ExpressionSystem system, Point initialApproximation) {
		super(String.format("Bad Initial Approximation %s for Expression System '%s'", initialApproximation, system));
	}
}
