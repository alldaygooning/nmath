package nikita.math.exception.construct.expression;

import nikita.math.construct.expression.Expression;

public class ExpressionSolutionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ExpressionSolutionException(Expression expression, String equals) {
		super(String.format("Unable to solve: %s = %s.", expression, equals));
	}
}
