package nikita.math.construct.calculus;

import java.math.BigDecimal;

import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;

public class Integral {

	Expression expression;
	Variable variable;
	BigDecimal upper;
	BigDecimal lower;

	public Integral(Expression expression, Variable variable) {
		this.setExpression(expression);
		this.setVariable(variable);
	}

	public Integral(Expression expression, Variable variable, BigDecimal upper, BigDecimal lower) {
		this(expression, variable);
		if (lower.compareTo(upper) <= 0) {
			this.setUpper(upper);
			this.setLower(lower);
		} else {
			this.setUpper(lower);
			this.setLower(upper);
		}
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public BigDecimal getUpper() {
		return upper;
	}

	public void setUpper(BigDecimal upper) {
		this.upper = upper;
	}

	public BigDecimal getLower() {
		return lower;
	}

	public void setLower(BigDecimal lower) {
		this.lower = lower;
	}

	public String toBeautifulString() {
		String string = String.format("Integral of Expression '%s' with respsect to %s", expression, variable.getName());
		if (upper != null && lower != null) {
			return (string + String.format(" from %s to %s", lower.toPlainString(), upper.toPlainString()));
		}
		return (string);
	}

}
