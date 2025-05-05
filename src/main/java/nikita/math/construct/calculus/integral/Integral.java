package nikita.math.construct.calculus.integral;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nikita.math.construct.Interval;
import nikita.math.construct.Variable;
import nikita.math.construct.calculus.limit.Limit;
import nikita.math.construct.calculus.limit.LimitDirection;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.singularity.Discontinuity;
import nikita.math.construct.point.singularity.DiscontinuityInterval;
import nikita.math.construct.point.singularity.DiscontinuityPoint;

public class Integral {

	Expression integrand;
	Variable variable;
	Interval interval;
	List<BigDecimal> partitionPoints;

	public Integral(Expression expression, Variable variable) {
		this.setIntegrand(expression);
		this.setVariable(variable);
	}

	public Integral(Expression expression, Variable variable, BigDecimal upper, BigDecimal lower) {
		this(expression, variable);
		if (lower.compareTo(upper) <= 0) {
			this.interval = new Interval(lower, upper);
		} else {
			this.interval = new Interval(upper, lower);
		}
	}

	public boolean isProper() {
		return (interval.isFinite() && integrand.isContinious(interval));
	}

	public IntegralKind getKind() {
		if (!interval.isFinite()) {
			return IntegralKind.FIRST;
		} else if (!integrand.isContinious(interval)) {
			return IntegralKind.SECOND;
		}
		return null;
	}

	public boolean isConvergent() {
		List<Discontinuity> discontinuities = integrand.getSingularities(interval);
		List<DiscontinuityInterval> intervals = Discontinuity.getIntervals(discontinuities);
		if (intervals.size() > 0) {
			return false;
		}

		List<DiscontinuityPoint> points = Discontinuity.getPoints(discontinuities);
		List<BigDecimal> partitionPoints = new ArrayList<BigDecimal>();
		for (DiscontinuityPoint point : points) {
			Expression expression = point.getPoint();
			if (!expression.isNumeric()) {
				continue;
			}
			partitionPoints.add(expression.toBigDecimal());
		}
		Collections.sort(partitionPoints);
		if(partitionPoints.get(0).compareTo(interval.getLeft()) != 0) {
			partitionPoints.add(interval.getLeft());
		}
		Collections.sort(partitionPoints);
		if(partitionPoints.get(partitionPoints.size() - 1).compareTo(interval.getRight()) != 0) {
			partitionPoints.add(interval.getRight());
		}
		Collections.sort(partitionPoints);
		this.partitionPoints = partitionPoints;

		Variable xtSub = new Variable("x", new Expression("t"));
		Expression antiderivative = integrand.antiderivative(variable);

		Expression adLeftEval = antiderivative.evaluateAt(new Variable("x", partitionPoints.get(1)));
		if (adLeftEval.isIndeterminate()) {
			adLeftEval = antiderivative.evaluateAt(new Variable("x", partitionPoints.get(1).negate()));
		}
		Expression adLeftT = antiderivative.evaluateAt(xtSub);
		Expression limitLeft = new Limit(adLeftEval.sub(adLeftT), new Expression(partitionPoints.get(0).toPlainString()), new Variable("t"))
				.evaluate(LimitDirection.LEFT_TO_RIGHT);
		if (limitLeft.isInfinity() || limitLeft.isComplex()) {
			return false;
		}

		int lastIndex = partitionPoints.size() - 1;
		Expression adRightEval = antiderivative.evaluateAt(new Variable("x", partitionPoints.get(lastIndex - 1)));
		if (adRightEval.isIndeterminate()) {
			adRightEval = antiderivative.evaluateAt(new Variable("x", partitionPoints.get(lastIndex - 1).negate()));
		}
		Expression adRightT = antiderivative.evaluateAt(xtSub);
		Expression limitRight = new Limit(adRightT.sub(adRightEval), new Expression(partitionPoints.get(lastIndex).toPlainString()),
				new Variable("t")).evaluate(LimitDirection.RIGHT_TO_LEFT);
		if (limitRight.isInfinity() || limitRight.isComplex()) {
			return false;
		}

		BigDecimal total = BigDecimal.ZERO;
		for (int i = 1; i < partitionPoints.size() - 1; i++) {
			Interval leftInterval = new Interval(partitionPoints.get(i - 1), partitionPoints.get(i));
			Expression adEval1 = antiderivative.evaluateAt(new Variable("x", leftInterval.getLeft()));
			if (adEval1.isIndeterminate()) {
				return false;
			}
			Expression adt1 = antiderivative.evaluateAt(xtSub);

			Expression limit1 = new Limit(adEval1.sub(adt1), new Expression(leftInterval.getRight().toPlainString()), new Variable("t"))
					.evaluate(LimitDirection.LEFT_TO_RIGHT);
			if (limit1.isInfinity() || limit1.isComplex()) {
				return false;
			}

			total = total.add(limit1.toBigDecimal());

			Interval rightInterval = new Interval(partitionPoints.get(i), partitionPoints.get(i + 1));
			Expression adt2 = antiderivative.evaluateAt(xtSub);
			Expression adEval2 = antiderivative.evaluateAt(new Variable("x", rightInterval.getRight()));
			if (adEval2.isIndeterminate()) {
				return false;
			}

			Expression limit2 = new Limit(adt2.sub(adEval2), new Expression(rightInterval.getRight().toPlainString()), new Variable("t"))
					.evaluate(LimitDirection.RIGHT_TO_LEFT);
			if (limit2.isInfinity() || limit2.isComplex()) {
				return false;
			}

			total = total.add(limit2.toBigDecimal());
		}
		System.out.println("Estimated value of Integral (might be incorrect): " + total);
		return true;
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

	public List<BigDecimal> getPartitionPoints() {
		return partitionPoints;
	}
}
