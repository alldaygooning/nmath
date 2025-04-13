package nikita.math.construct.expression;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.exception.construct.expression.EquationEvaluationException;

public class Equation {

	private Expression left;
	private Expression right;

	public Equation(Expression left, Expression right) {
		this.setLeft(left);
		this.setRight(right);
	}

	public List<Expression> solve(Variable variable, Precision precision) {
		String command = String.format("Solve(%s==%s, %s)", left.toString(), right.toString(), variable.getName());

		return this.evaluate(command, precision);
	}

	public List<Expression> solve(Interval interval, Variable variable, Precision precision) {
		String varName = variable.getName();
		String domain = this.getDomain(interval, variable);
		String command = String.format("Solve({%s==%s, %s}, %s)", left.toString(), right.toString(), domain, varName);

		return this.evaluate(command, precision);
	}

	private List<Expression> evaluate(String command, Precision precision) {
//		EvalEngine engine = NMath.getEngine(precision);
		ExprEvaluator evaluator = new ExprEvaluator();

		command = String.format("N(%s, %s)", command, precision.getNPrecision());
		IExpr rootsRules = evaluator.eval(command);
		if (rootsRules.toString().contains("Solve")) {
			throw new EquationEvaluationException(this);
		}

		List<Expression> roots = new ArrayList<Expression>();
		for (int i = 1; i < rootsRules.size(); i++) {
			roots.add(new Expression(rootsRules.getAt(i).getAt(1).getAt(2).toString()));
		}
		return roots;
	}

	private String getDomain(Interval interval, Variable variable) {
		String varName = variable.getName();
		return String.format("%s>=%s, %s<=%s", varName, interval.getLeft().toPlainString(), varName, interval.getRight().toPlainString());
	}

	@Override
	public String toString() {
		return String.format("%s == %s", left, right);
	}

	// -----GETTERS & SETTERS----- //

	public Expression getLeft() {
		return left;
	}

	public void setLeft(Expression left) {
		this.left = left;
	}

	public Expression getRight() {
		return right;
	}

	public void setRight(Expression right) {
		this.right = right;
	}
}
