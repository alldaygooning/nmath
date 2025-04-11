package nikita.math.exception.construct.root;

import java.util.List;

import nikita.math.construct.expression.Expression;

public class ExpressionSyntaxError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ExpressionSyntaxError(Expression expression) {
		super(String.format("Expression ('%s') contains syntax error(s).", expression.toString()));
	}

	public ExpressionSyntaxError(List<Expression> expressions) {
		super(String.format("One of the following Expressions contains syntax error(s): %s", expressionsToString(expressions)));
	}

	private static String expressionsToString(List<Expression> expressions) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < expressions.size(); i++) {
			builder.append(expressions.get(i).toString());
			if (i != expressions.size() - 1) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
}
