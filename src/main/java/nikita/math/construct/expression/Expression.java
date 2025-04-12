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

	private static final Precision DEFAULT_BIGDECIMAL_PRECISION = new Precision("0.0001");

	String string;

	public Expression(String string) {
		this.string = string.replace("e", "E");
	}

	public Expression(Expression expression) {
		this.string = expression.string;
	}

	public String toString() {
		return string;
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

		try {
			BigDecimal fLeft = this.evaluateAt(new Variable("x", left)).toBigDecimal();
			BigDecimal fRight = this.evaluateAt(new Variable("x", right)).toBigDecimal();
			if (fLeft.multiply(fRight).compareTo(BigDecimal.ZERO) > 0) {
				return false;
			}
			if(fLeft.compareTo(BigDecimal.ZERO) == 0 && fRight.compareTo(BigDecimal.ZERO) == 0) {
				return false;
			}
		} catch (ExpressionConversionException e) {
			throw new IncontinuousIntervalException(this, interval);
		}

		return true;
	}

	public void singularities() {
		denominators();
	}

	private void denominators() {
		List<Expression> denominators = new ArrayList<Expression>();
		getDenominators(this.getExpr(), denominators);
		System.out.println(denominators);
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

	public List<BigDecimal> roots(Variable variable, Interval interval, Precision precision) {
		IExpr rootsRules = NMath.solve(this, variable, "0", interval, precision);
		List<BigDecimal> roots = new ArrayList<BigDecimal>();
		for (int i = 1; i < rootsRules.size(); i++) {
			BigDecimal x = NMath.getBigDecimal(rootsRules.getAt(i).getAt(1).getAt(2), precision);
			roots.add(x);
		}
		return roots;
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
		return this.toBigDecimal(DEFAULT_BIGDECIMAL_PRECISION);
	}

	// IExpr

	public IExpr getExpr() {
		ExprEvaluator evaluator = new ExprEvaluator();
		EvalEngine engine = evaluator.getEvalEngine();
		return engine.evaluate(string);
	}

	public void getDenominators(IExpr expr, List<Expression> denominators) {
	    if (expr == null)
	        return;

		if (expr.head().equals(F.Power) && expr.getAt(2).isNegative()) {
			Expression denominator = new Expression(String.format("%s^%s", expr.getAt(1), expr.getAt(2).abs()));
			denominators.add(denominator);
		}
		else if (expr.head().equals(F.Csc)) {
			Expression denominator = new Expression(String.format("Sin(%s)", expr.getAt(1).toString()));
			denominators.add(denominator);
		}
		else if (expr.head().equals(F.Sec)) {
			Expression denominator = new Expression(String.format("Cos(%s)", expr.getAt(1).toString()));
			denominators.add(denominator);
		}
		else if (expr.head().equals(F.Cot)) {
			Expression denominator = new Expression(String.format("Tan(%s)", expr.getAt(1).toString()));
			denominators.add(denominator);
		} else if (expr.head().equals(F.Tan)) {
			Expression denominator = new Expression(String.format("Cot(%s)", expr.getAt(1).toString()));
			denominators.add(denominator);
		}
//		System.out.println(expr + " " + expr.head());
//		for (int i = 0; i <= expr.argSize(); i++) {
//			System.out.println("Argument " + i + ": " + expr.getAt(i));
//		}
		

		for (int i = 1; i <= expr.argSize(); i++) {
			getDenominators(expr.getAt(i), denominators);
		}
	}
}

