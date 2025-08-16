package nikita.math.solver.approximate.differential;

import java.math.BigDecimal;
import java.util.List;

import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;

public class DifferentialFunctionApproximation {

	private Expression differentialApproximated;
	private Expression actualApproximated;
	private Precision precision;

	private List<Point> points;

	private BigDecimal epsilon;

	public DifferentialFunctionApproximation(Expression differentialApproximated, Expression actualApproximated, List<Point> points,
			BigDecimal epsilon,
			Precision precision) {
		this.setDifferentialApproximated(differentialApproximated);
		this.setActualApproximated(actualApproximated);
		this.setPoints(points);
		this.setEpsilon(epsilon);
		this.setPrecision(precision);
	}

	public Expression getDifferentialApproximated() {
		return differentialApproximated;
	}

	public void setDifferentialApproximated(Expression differentialApproximated) {
		this.differentialApproximated = differentialApproximated;
	}

	public Expression getActualApproximated() {
		return actualApproximated;
	}

	public void setActualApproximated(Expression actualApproximated) {
		this.actualApproximated = actualApproximated;
	}

	public Precision getPrecision() {
		return precision;
	}

	public void setPrecision(Precision precision) {
		this.precision = precision;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public BigDecimal getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(BigDecimal epsilon) {
		this.epsilon = epsilon;
	}

}
