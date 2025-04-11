package nikita.math.exception.construct.expression;

import nikita.math.construct.expression.Expression;

public class ExpressionConversionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ExpressionConversionException(Expression expression) {
		super(String.format("Unable to convert '%s' into BigDecimal value.", expression));
	}
}
