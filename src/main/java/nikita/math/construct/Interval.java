package nikita.math.construct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.construct.interval.IntervalParametersException;

public class Interval {

	BigDecimal left;
	BigDecimal right;

	public Interval() {

	}

	// Интервал должен быть непрерымным, но пока на это проверок нет.
	// Еще left < right
	public Interval(BigDecimal left, BigDecimal right) {
		if (left.compareTo(right) >= 0) {
			throw new IntervalParametersException(String.format("Interval Right endpoint must be greater than Left endpoint [%s; %s].",
					left.toPlainString(), right.toPlainString()));
		}
		this.left = left;
		this.right = right;
	}

	public boolean isFinite() { // Пока так
		return (this.left != null && this.right != null);
	}

	public boolean contains(Point point) {
		return ((point.getX().compareTo(left) >= 0) && (point.getX().compareTo(right) <= 0));
	}

	public boolean contains(BigDecimal x) {
		return (x.compareTo(left) >= 0) && (x.compareTo(right) <= 0);
	}

	public void setLeft(BigDecimal left) {
		if (right != null && left.compareTo(right) >= 0) {
			throw new IntervalParametersException(String.format("Interval Right endpoint must be greater than Left endpoint [%s; %s].",
					left.toPlainString(), right.toPlainString()));
		}
		this.left = left;
	}

	public void setRight(BigDecimal right) {
		if (left != null && left.compareTo(right) >= 0) {
			throw new IntervalParametersException(String.format("Interval Right endpoint must be greater than Left endpoint [%s; %s].",
					left.toPlainString(), right.toPlainString()));
		}
		this.right = right;
	}

	public List<Expression> getPeriodicRoots(Expression start, Expression step, Precision precision) {
		List<Expression> roots = new ArrayList<Expression>();

		String expression = String.format("(%s)+(%s)", start, step);
		String command = String.format("Reduce(%s<=%s && %s<=%s, %s)", left.toPlainString(), expression, expression, right.toPlainString(),
				"n");
		ExprEvaluator evaluator = new ExprEvaluator();
		IExpr evaluation = evaluator.eval(command);

		Expression nLeft = null;
		Expression nRight = null;
		for (int i = 1; i <= evaluation.argSize(); i++) {
			IExpr comparison = evaluation.getAt(i);
			IExpr lhs = evaluation.getAt(i).getAt(1);
			IExpr rhs = evaluation.getAt(i).getAt(2);
			if (comparison.head().equals(F.GreaterEqual)) {
				if (lhs.toString().equals("n")) {
					nLeft = new Expression(rhs.toString());
				} else if (rhs.toString().equals("n")) {
					nRight = new Expression(lhs.toString());
				}
			}
			else if (comparison.head().equals(F.LessEqual)) {
				if (lhs.toString().equals("n")) {
					nRight = new Expression(rhs.toString());
				} else if (rhs.toString().equals("n")) {
					nLeft = new Expression(lhs.toString());
				}

			}
		}

		if (nLeft == null || nRight == null) {
			return roots;
		}
		nLeft = nLeft.toNumeric();
		nRight = nRight.toNumeric();

		BigDecimal left = nLeft.toBigDecimal().setScale(0, RoundingMode.CEILING);
		BigDecimal right = nRight.toBigDecimal();

		while (left.compareTo(right) <= 0) {
			roots.add(new Expression(expression).evaluateAt(new Variable("n", left)));
			left = left.add(BigDecimal.ONE);
		}
		return roots;
	}

	public boolean overlaps(Interval other) {
		Interval leftInterval = this.getLeft().compareTo(other.getLeft()) <= 0 ? this : other;
		Interval rightInterval = this.getRight().compareTo(other.getRight()) >= 0 ? this : other;
		return leftInterval.getRight().compareTo(rightInterval.getLeft()) >= 0;
	}

	public BigDecimal getLeft() {
		return left;
	}

	public BigDecimal getRight() {
		return right;
	}

	@Override
	public String toString() {
		String leftEndpoint = left != null ? left.toPlainString() : "-Infinity";
		String rightEndpoint = right != null ? right.toPlainString() : "+Infinity";
		return String.format("[%s; %s]", leftEndpoint, rightEndpoint);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Interval) {
			Interval other = (Interval) obj;
			return this.left.compareTo(other.left) == 0 && this.right.compareTo(other.right) == 0;
		}
		return false;
	}

	public BigDecimal getLength() {
		return left.subtract(right).abs();
	}
}
