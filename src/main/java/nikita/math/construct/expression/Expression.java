package nikita.math.construct.expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.trigonometry.NTrigonometry;

public class Expression {
	String string;

	public Expression(String string) {
		this.string = string;
	}

	public Expression(Expression expression) {
		this.string = expression.string;
	}

	public String toString() {
		return string;
	}

	public Expression derivative(String variable) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("D(%s, %s)", this.string, variable);
		return new Expression(evaluator.eval(command).toString());
	}

	public IExpr evaluateAt(String variable, String value) {
		return NMath.replaceAll(this, variable, value);
	}
	
	public IExpr evaluateAt(Variable variable) {
		return this.evaluateAt(variable.getName(), variable.getValue().toPlainString());
	}

	public IExpr evaluateAt(Variable variable, Precision precision) {
		return NMath.replaceAll(this, variable.getName(), variable.getValue().toPlainString(), precision);
	}

	public IExpr evaluateAt(String value) {
		return this.evaluateAt("x", value);
	}

	public BigDecimal toBigDecimal(Precision precision) {
		return NMath.getBigDecimal(string, precision);
	}

	public List<BigDecimal> roots(String variable, Precision precision) {
		IExpr rootsRules = NMath.solve(this, variable, "0", precision);
		List<BigDecimal> roots = new ArrayList<BigDecimal>();
		for (int i = 1; i < rootsRules.size(); i++) {
			BigDecimal x = NMath.getBigDecimal(rootsRules.getAt(i).getAt(1).getAt(2), precision);
			roots.add(x);
		}
		return roots;
	}

	public boolean isTrigonometric() {
		ExprEvaluator evaluator = new ExprEvaluator();
		return NTrigonometry.containsTrigFunction(evaluator.eval(string));
	}

	public String getWolframString() {
		ExprEvaluator evaluator = new ExprEvaluator();
		return evaluator.eval(string).toMMA();
	}

	public boolean isContinious(Interval interval) { // Пока вот так
		return true;
	}

	public boolean checkIVT(Interval interval) {
		if (!this.isContinious(interval)) {
			return false;
		}

		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();

		BigDecimal fLeft = NMath.getBigDecimal(this.evaluateAt(left.toPlainString()));
		BigDecimal fRight = NMath.getBigDecimal(this.evaluateAt(right.toPlainString()));

		if (fLeft.multiply(fRight).compareTo(BigDecimal.ZERO) > 0) {
			return false;
		}
		return true;
	}

	// ОПЕРАЦИИ

	public Expression inverse() {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("(1/(%s))", string);
		return (new Expression(evaluator.eval(command).toString()));
	}

	public Expression multiply(Expression expression) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("((%s)*(%s))", string, expression);
		return (new Expression(evaluator.eval(command).toString()));
	}

	public Expression negative() {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("-(%s)", string);
		return (new Expression(evaluator.eval(command).toString()));
	}
}
