package nikita.math.construct.expression;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.ExprEvaluator;

import nikita.math.construct.Matrix;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;

public class ExpressionSystem {

	Expression a;
	Expression b;

	public ExpressionSystem(Expression a, Expression b) {
		this.a = a;
		this.b = b;
	}

	public Matrix jacobiMatrix() {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("JacobiMatrix({%s, %s}, {x, y})", a, b);
		return new Matrix(evaluator.eval(command));
	}

	public ExpressionSystem evaluateAt(Variable variable) {
		Expression anew = new Expression(a.evaluateAt(variable).toString());
		Expression bnew = new Expression(b.evaluateAt(variable).toString());
		return new ExpressionSystem(anew, bnew);
	}

	public ExpressionSystem evaluateAt(Variable variable, Precision precision) {
		Expression anew = new Expression(a.evaluateAt(variable, precision).toString());
		Expression bnew = new Expression(b.evaluateAt(variable, precision).toString());
		return new ExpressionSystem(anew, bnew);
	}

	public List<Expression> getSingles() {
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(a);
		expressions.add(b);
		return expressions;
	}
}
