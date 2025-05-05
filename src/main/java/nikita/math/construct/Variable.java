package nikita.math.construct;

import java.math.BigDecimal;

import nikita.math.construct.expression.Expression;

public class Variable {
	private String name;
	private BigDecimal numericValue;
	private Expression symbolicValue;

	public Variable(String name, BigDecimal value) {
		this.setName(name);
		this.setNumericValue(value);
	}

	public Variable(String name, Expression expression) {
		this.setName(name);
		this.setSymbolicValue(expression);
	}

	public Variable(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(BigDecimal value) {
		this.numericValue = value;
	}

	public Expression getSymbolicValue() {
		return symbolicValue;
	}

	public void setSymbolicValue(Expression symbolicValue) {
		this.symbolicValue = symbolicValue;
	}

	public String getStringValue() {
		if (numericValue != null) {
			return numericValue.toPlainString();
		} else if (symbolicValue != null) {
			return symbolicValue.toString();
		}
		return null;
	}

	public String getStringValue(Precision precision) {
		if (numericValue != null) {
			return numericValue.toPlainString();
		} else if (symbolicValue != null) {
			return symbolicValue.toString(precision);
		}
		return null;
	}

	public int compareTo(Variable other) {
		return this.getStringValue().compareTo(other.getStringValue());
	}

	@Override
	public String toString() {
		if (numericValue != null || symbolicValue != null) {
			return String.format("%s = %s", this.name, getStringValue());
		}
		return this.name;
	}
}
