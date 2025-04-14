package nikita.math.construct.expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.exception.construct.expression.ExpressionConversionException;
import nikita.math.exception.construct.interval.IncontinuousIntervalException;
import nikita.math.trigonometry.NTrigonometry;

public class Expression {

	String string;

	public Expression(String string) {
		this.string = string.replace("e", "E");
	}

	public Expression(Expression expression) {
		this.string = expression.string;
	}

	public boolean isTrigonometric() {
		ExprEvaluator evaluator = new ExprEvaluator();
		return NTrigonometry.containsTrigFunction(evaluator.eval(string));
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

		try {
			BigDecimal fLeft = this.evaluateAt(new Variable("x", left)).toBigDecimal();
			BigDecimal fRight = this.evaluateAt(new Variable("x", right)).toBigDecimal();
			if (fLeft.multiply(fRight).compareTo(BigDecimal.ZERO) > 0) {
				return false;
			}
			if (fLeft.compareTo(BigDecimal.ZERO) == 0 && fRight.compareTo(BigDecimal.ZERO) == 0) {
				return false;
			}
		} catch (ExpressionConversionException e) {
			throw new IncontinuousIntervalException(this, interval);
		}

		return true;
	}

	public List<Expression> getDenominators() {
		List<Expression> denominators = new ArrayList<Expression>();
		searchDenominators(this.getExpr(true), denominators);
		return denominators;
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

	public Expression derivative(String variable) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("D(%s, %s)", this.string, variable);
		return new Expression(evaluator.eval(command).toString());
	}

	// ВЫЧИСЛЕНИЕ

	public Expression evaluateAt(Variable variable) {
		return new Expression(NMath.replaceAll(this, variable.getName(), variable.getValue().toPlainString()).toString());
	}

	public Expression evaluateAt(Variable variable, Precision precision) {
		return new Expression(NMath.replaceAll(this, variable.getName(), variable.getValue().toPlainString(), precision).toString());
	}

	// BIGDECIMAL!

	public BigDecimal toBigDecimal(Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		EvalEngine engine = evaluator.getEvalEngine();

		String nPrecision = String.valueOf(precision.getNPrecision());
		String command = String.format("N(%s, %s)", this.toString(), nPrecision);
		engine.setNumericMode(true, Integer.valueOf(precision.getNPrecision()), -1);
		String formatted = NMath.bigDecimalNormalize(engine.evaluate(command).toString());

		try {
			return NMath.getBigDecimal(formatted, precision);
		} catch (NumberFormatException e) {
			throw new ExpressionConversionException(this);
		}
	}

	public BigDecimal toBigDecimal() {
		return this.toBigDecimal(NMath.DEFAULT_BIGDECIMAL_PRECISION);
	}

	// IExpr

	private void searchDenominators(IExpr expr, List<Expression> denominators) {
		if (expr == null)
			return;

		// Деление реализовано через отрицательные степени
		if (expr.head().equals(F.Power) && expr.getAt(2).isNegative()) {
			IExpr base = expr.getAt(1);
			IExpr power = expr.getAt(2).abs();
			Expression denominator = new Expression(String.format("%s%s", base, power.isOne() ? "" : String.format("^%s", power)));
			denominators.add(denominator);
		}

		for (int i = 1; i <= expr.argSize(); i++) {
			searchDenominators(expr.getAt(i), denominators);
		}
	}

	// GETTERS & SETTERS //

	public IExpr getExpr() {
		return this.getExpr(false);
	}

	public IExpr getExpr(boolean holdForm) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = string;
		if (holdForm) {
			command = String.format("HoldForm(%s)", string);
		}
		return evaluator.eval(command);
	}

	public String getWolframString() {
		ExprEvaluator evaluator = new ExprEvaluator();
		return evaluator.eval(string).toMMA();
	}

	public Equation getEquation(Expression other) {
		return new Equation(this, other);
	}

	public String getString() {
		return string;
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Expression) {
			return this.string.equals(((Expression) obj).getString());
		}
		return false;
	}
}
