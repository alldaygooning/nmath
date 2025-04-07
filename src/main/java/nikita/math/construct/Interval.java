package nikita.math.construct;

import java.math.BigDecimal;

import nikita.math.exception.construct.interval.IntervalParametersException;

public class Interval {

	BigDecimal left;
	BigDecimal right;

	// Интервал должен быть непрерымным, но пока на это проверок нет.
	// Еще left < right
	public Interval(BigDecimal left, BigDecimal right) {
		if (left.compareTo(right) >= 0) {
			throw new IntervalParametersException(
					String.format("Interval Right endpoint must be greater than Left endpoint %s.", this.toString()));
		}
		this.left = left;
		this.right = right;
	}

	public BigDecimal getLeft() {
		return left;
	}

	public BigDecimal getRight() {
		return right;
	}

	public String toString() {
		return String.format("[%s; %s]", left.toPlainString(), right.toPlainString());
	}

	public BigDecimal getLength() {
		return left.subtract(right).abs();
	}
}
