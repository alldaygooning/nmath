package nikita.math.construct.calculus.limit;

import org.matheclipse.core.eval.ExprEvaluator;

import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.expression.IndeterminateExpression;

public class Limit {

	Expression expression;
	Expression point;
	Variable variable;


	private static final String DIRECTIONAL_TEMPLATE = "Limit(%s, %s->%s, Direction->%s)";
	private static final String DEFAULT_TEMPLATE = "Limit(%s, %s->%s)";

	public Limit(Expression expression, Expression point, Variable variable) {
		this.expression = expression;
		this.point = point;
		this.variable = variable;
	}

	public Expression evaluate(LimitDirection direction) {
		String command;
		if (direction == LimitDirection.NONE) {
			command = String.format(DEFAULT_TEMPLATE, expression, variable.getName(), point);
		} else {
			command = String.format(DIRECTIONAL_TEMPLATE, expression, variable.getName(), point, direction.getDirection());
		}

		ExprEvaluator evaluator = new ExprEvaluator();
		String evaluation = evaluator.eval(command).toString();
		if (evaluation.equals("Indeterminate")) {
			return new IndeterminateExpression(evaluation);
		}
		return new Expression(evaluation);
	}
}
