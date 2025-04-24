package nikita.math.construct.point.singularity;

import java.util.ArrayList;
import java.util.List;

import nikita.math.construct.Interval;
import nikita.math.construct.expression.Expression;

public class DiscontinuityInterval extends Discontinuity {

	private static final String NAME = "Interval Discontinuity";

	Interval interval;

	public DiscontinuityInterval(Expression expression, Interval interval) {
		super(expression);
		name = NAME;

		this.setInterval(interval);
	}

	public boolean contains(DiscontinuityPoint discontinuityPoint) {
		if (!discontinuityPoint.point.isNumeric()) {
			return false;
		}
		return getInterval().contains(discontinuityPoint.point.toBigDecimal());
	}

	public boolean overlaps(DiscontinuityInterval other) {
		return this.getInterval().overlaps(other.getInterval());
	}

	// Передавать только с одинаковыми expression - ПРОВЕРКИ НА ЭТО НЕТ!
	public static List<DiscontinuityInterval> decoupleIntervals(List<DiscontinuityInterval> discontinuityIntervals) {
		// Проблема, что надо кароче список с копиями, а не референсами создавать тут!
		List<DiscontinuityInterval> decoupledIntervals = new ArrayList<DiscontinuityInterval>();
		if (discontinuityIntervals.size() <= 0) {
			return decoupledIntervals;
		}

		for (DiscontinuityInterval discInt : discontinuityIntervals) {
			boolean overlaps = false;
			for (int i = 0; i < decoupledIntervals.size(); i++) {
				DiscontinuityInterval decoupledInt = decoupledIntervals.get(i);
				if (discInt.overlaps(decoupledInt)) {
					decoupledIntervals.set(i, new DiscontinuityInterval(decoupledInt.expression,
							new Interval(discInt.getInterval().getLeft().min(decoupledInt.getInterval().getLeft()),
									discInt.getInterval().getRight().max(decoupledInt.getInterval().getRight()))));
					overlaps = true;
					break;
				}
			}
			if (!overlaps) {
				decoupledIntervals.add(discInt);
			}
		}

		return decoupledIntervals;
	}

	@Override
	public String toBeautifulString() {
		return String.format("%s of Expression '%s' on Interval %s.", name, expression, getInterval());
	}

	@Override
	public String toShortString() {
		return String.format("%s of Expression on Interval %s.", name, getInterval());
	}

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
		this.interval = interval;
	}
}
