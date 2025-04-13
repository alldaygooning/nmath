package nikita.math.construct;

import java.math.BigDecimal;

public class Variable {
	private String name;
	private BigDecimal value;

	public Variable(String name, BigDecimal value) {
		this.setName(name);
		this.setValue(value);
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

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public int compareTo(Variable other) {
		return this.value.compareTo(other.getValue());
	}

	@Override
	public String toString() {
		if (this.value != null) {
			return String.format("%s = %s", this.name, this.value.toPlainString());
		}
		return this.name;
	}
}
