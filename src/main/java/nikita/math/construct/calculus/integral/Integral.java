package nikita.math.construct.calculus.integral;

import java.math.BigDecimal;

import nikita.math.construct.Interval;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;

public class Integral {

	Expression integrand;
	Variable variable;
	Interval interval;

	public Integral(Expression expression, Variable variable) {
		this.setIntegrand(expression);
		this.setVariable(variable);
	}

	public Integral(Expression expression, Variable variable, BigDecimal upper, BigDecimal lower) {
		this(expression, variable);
		if (lower.compareTo(upper) <= 0) {
			this.interval = new Interval(upper, lower);
		} else {
			this.interval = new Interval(lower, upper);
		}
	}

	public boolean isProper() {
		return (interval.isFinite() && integrand.isContinious(interval));
	}

	public Expression getIntegrand() {
		return integrand;
	}

	public void setIntegrand(Expression expression) {
		this.integrand = expression;
	}

	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public BigDecimal getLower() {
		return interval.getLeft();
	}

	public BigDecimal getUpper() {
		return interval.getRight();
	}

	public String toBeautifulString() {
		BigDecimal lower = interval.getLeft();
		BigDecimal upper = interval.getRight();

		String string = String.format("Integral of Expression '%s' with respsect to %s", integrand, variable.getName());
		if (upper != null && lower != null) {
			return (string + String.format(" from %s to %s", lower.toPlainString(), upper.toPlainString()));
		}
		return (string);
	}


}
