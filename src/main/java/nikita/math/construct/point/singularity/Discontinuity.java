package nikita.math.construct.point.singularity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nikita.math.construct.Interval;
import nikita.math.construct.Variable;
import nikita.math.construct.calculus.limit.Limit;
import nikita.math.construct.calculus.limit.LimitDirection;
import nikita.math.construct.expression.Expression;

public abstract class Discontinuity extends Singularity {

	protected String name = "Generic Discontinuity";

	Expression expression;

	public Discontinuity(Expression expression) {
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}


	// f(point) does not exist here!
	public static DiscontinuityPoint atPoint(Expression expression, Expression point) {
		Variable xVar = new Variable("x");

		Expression leftLimit = (new Limit(expression, point, xVar)).evaluate(LimitDirection.LEFT_TO_RIGHT);
		Expression rightLimit = (new Limit(expression, point, xVar)).evaluate(LimitDirection.RIGHT_TO_LEFT);
//			Expression limit = (new Limit(expression, point, xVar)).evaluate(LimitDirection.NONE);

		if (leftLimit.isInfinity() || rightLimit.isInfinity() || leftLimit.isIndeterminate() || rightLimit.isIndeterminate()) {
			return new InfiniteDiscontinuity(expression, point);
		} else if (!leftLimit.equals(rightLimit)) {
			return new JumpDiscontinuity(expression, point);
		}
		return new InfiniteDiscontinuity(expression, point); // Ну просто вот так)
	}

	public static DiscontinuityInterval atInterval(Expression expression, Interval interval) {
		return new DiscontinuityInterval(expression, interval);
	}

	public static List<Discontinuity> decouple(List<Discontinuity> discontinuities) {
		List<DiscontinuityPoint> decoupledPoints = DiscontinuityPoint.decouplePoints(discontinuities.stream()
				.filter(Discontinuity::isPoint)
				.map(disc -> (DiscontinuityPoint)disc)
				.collect(Collectors.toList()));
		List<DiscontinuityInterval> decoupledIntervals = DiscontinuityInterval.decoupleIntervals(discontinuities.stream()
				.filter(Discontinuity::isInterval)
				.map(disc -> (DiscontinuityInterval) disc)
				.collect(Collectors.toList()));
		
		List<Discontinuity> decoupledDiscontinuities = new ArrayList<Discontinuity>();

		for (DiscontinuityPoint decoupledPnt : decoupledPoints) {
			boolean contained = false;
			for (DiscontinuityInterval decoupledInt : decoupledIntervals) {
				if (decoupledInt.contains(decoupledPnt)) {
					contained = true;
					break;
				}
			}

			if (!contained) {
				decoupledDiscontinuities.add(decoupledPnt);
			}
		}
		decoupledDiscontinuities.addAll(decoupledIntervals);
		
		
		return decoupledDiscontinuities;
	}

	public static List<DiscontinuityInterval> getIntervals(List<Discontinuity> discontinuities) {
		return discontinuities.stream().filter(Discontinuity::isInterval).map(disc -> (DiscontinuityInterval) disc)
				.collect(Collectors.toList());
	}

	public static List<DiscontinuityPoint> getPoints(List<Discontinuity> discontinuities) {
		return discontinuities.stream().filter(Discontinuity::isPoint).map(disc -> (DiscontinuityPoint) disc).collect(Collectors.toList());
	}

	public boolean isInterval() {
		return this instanceof DiscontinuityInterval;
	}

	public boolean isPoint() {
		return this instanceof DiscontinuityPoint;
	}

	public abstract String toBeautifulString();

	public abstract String toShortString();
}
