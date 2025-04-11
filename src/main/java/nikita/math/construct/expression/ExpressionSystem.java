package nikita.math.construct.expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.matheclipse.core.eval.ExprEvaluator;

import nikita.math.NMath;
import nikita.math.construct.Matrix;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.exception.construct.root.ExpressionSyntaxError;

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
		Matrix jacobiMatrix = new Matrix(evaluator.eval(command));
		if (jacobiMatrix.isBroken()) {
			throw new ExpressionSyntaxError(Arrays.asList(this.a, this.b));
		}
		return jacobiMatrix;
	}

	public ExpressionSystem evaluateAt(Variable variable) {
		Expression anew = new Expression(a.evaluateAt(variable).toString());
		Expression bnew = new Expression(b.evaluateAt(variable).toString());
		return new ExpressionSystem(anew, bnew);
	}

	public ExpressionSystem evaluateAt(Variable variable, Precision precision) {
		Expression anew = a.evaluateAt(variable, precision);
		Expression bnew = b.evaluateAt(variable, precision);
		return new ExpressionSystem(anew, bnew);
	}

	public List<Expression> getSingles() {
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(a);
		expressions.add(b);
		return expressions;
	}

	public boolean check(Variable xVar, Variable yVar, Precision precision) {
		BigDecimal aValue = a.evaluateAt(xVar).evaluateAt(yVar).toBigDecimal(precision);
		BigDecimal bValue = b.evaluateAt(xVar).evaluateAt(yVar).toBigDecimal(precision);

		return NMath.equal(aValue, BigDecimal.ZERO, precision) && NMath.equal(bValue, BigDecimal.ZERO, precision);
	}

	@Override
	public String toString() {
		return String.format("{%s, %s}", a, b);
	}
}
