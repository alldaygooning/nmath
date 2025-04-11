package nikita.math.exception.construct.expression;

import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.exception.construct.NMathException;

public class ExpressionEvaluationException extends NMathException {
	private static final long serialVersionUID = 1L;

	public ExpressionEvaluationException(Expression expression, Variable variable) {
		super(String.format("Unable to evaluate Expression '%s' at %s.", expression, variable));
	}
}
