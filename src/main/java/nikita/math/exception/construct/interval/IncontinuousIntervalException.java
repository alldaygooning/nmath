package nikita.math.exception.construct.interval;

import nikita.math.construct.Interval;
import nikita.math.construct.expression.Expression;

public class IncontinuousIntervalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IncontinuousIntervalException(Expression expression, Interval interval) {
		super(String.format("Function %s should be continuous on Interval %s.", expression, interval));
	}
}
