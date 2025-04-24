package nikita.math.construct.equation;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

import nikita.external.api.wolfram.WolframAPI;
import nikita.external.api.wolfram.query.WolframQuery;
import nikita.external.api.wolfram.query.WolframQueryBuilder;
import nikita.external.api.wolfram.query.param.WolframQueryFormat;
import nikita.external.api.wolfram.query.param.WolframQueryOutput;
import nikita.external.api.wolfram.query.param.WolframQueryPod;
import nikita.external.api.wolfram.query.param.WolframQueryPodState;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.exception.construct.expression.EquationEvaluationException;

public class Equation {

	private Expression left;
	private Expression right;

	public Equation(Expression left, Expression right) {
		this.setLeft(left);
		this.setRight(right);
	}

	// НЕ КОРМИТЬ СЮДА ТРИГОНОМЕТРИЮ!
	public List<Expression> solve(Variable variable, Precision precision) {
		String command = String.format("Solve(%s==%s, %s)", left.toString(precision), right.toString(precision), variable.getName());
		return this.evaluate(command, precision);
	}

	public List<Expression> solve(Interval interval, Variable variable, Precision precision) {
		if (this.isTrigonometric()) {
			return this.wolframEvaluate(interval, variable, precision);
		}

		String varName = variable.getName();
		String domain = this.getDomain(interval, variable);
		String command = String.format("Solve({%s==%s, %s}, %s)", left.toString(precision), right.toString(precision), domain, varName);
		return this.evaluate(command, precision);
	}

	private List<Expression> evaluate(String command, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();

		List<Expression> roots = new ArrayList<Expression>();

		command = String.format("N(%s, %s)", command, precision.getNPrecision());
		IExpr rootsRules = evaluator.eval(command);
		if (rootsRules.toString().contains("Solve")) {
			return roots;
		}

		for (int i = 1; i < rootsRules.size(); i++) {
			IExpr expr = rootsRules.getAt(i).getAt(1).getAt(2);
			if (expr.isComplex() || expr.isComplexNumeric()) {
				continue;
			}
			roots.add(new Expression(expr.toString()));
		}
		return roots;
	}

	private List<Expression> wolframEvaluate(Interval interval, Variable variable, Precision precision) {
		String command = String.format("solve %s=%s for %s<=%s<=%s", left, right, interval.getLeft().toPlainString(), variable.getName(),
				interval.getRight().toPlainString());

		WolframQuery query = new WolframQueryBuilder(command).format(WolframQueryFormat.PLAINTEXT).output(WolframQueryOutput.JSON)
				.include(WolframQueryPod.RESULT).add(WolframQueryPodState.MORE_SOLUTIONS, 1).toQuery();
		try {
			Set<Expression> solutions = WolframAPI.getJsonSolutions(WolframAPI.query(query));
			return new ArrayList<Expression>(solutions);
		} catch (IOException | InterruptedException e) {
			throw new EquationEvaluationException(this, e.getMessage());
		}
	}

	private String getDomain(Interval interval, Variable variable) {
		String varName = variable.getName();
		return String.format("%s>=%s, %s<=%s", varName, interval.getLeft().toPlainString(), varName, interval.getRight().toPlainString());
	}

	public boolean isTrigonometric() {
		return (left.isTrigonometric() || right.isTrigonometric());
	}

	private Expression normalize(Expression expression) {
		if (expression.getExpr().isInexactNumber()) {
			BigDecimal number = expression.toBigDecimal().round(new MathContext(16, RoundingMode.HALF_UP));
			expression = new Expression(number.toPlainString());
		}
		return expression;
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
		this.left = normalize(left);
	}

	public Expression getRight() {
		return right;
	}

	public void setRight(Expression right) {
		this.right = normalize(right);
	}
}
