package nikita.math.solver.approximate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;

public class FunctionApproximation {

	Expression approximated;
	Precision precision;

	BigDecimal deviation; // Мера отклонения
	BigDecimal standardDeviation; // Среднеквадратичное отклонение
	BigDecimal determination; // Коэффициент детерминации
	Optional<BigDecimal> correlation = Optional.empty(); // Коэффеициент корреляции Пирса (только для линейной аппроксимации)

	List<Point> approximatedPoints = new ArrayList<Point>();
	List<BigDecimal> epsilons = new ArrayList<BigDecimal>();

	public FunctionApproximation(Expression approximated, List<Point> points, Precision precision) {
		this.approximated = approximated;
		this.precision = precision;

		this.deviation = this.getDeviation(points); // Это должно идти первым, мне пока лень переделывать
		this.standardDeviation = this.getStandardDeviation(points);
		this.determination = this.getDetermination(points);
	}

	// Добавить обработку ошибок при evaluateAt, деление на ноль или в логарифме
	// западло
	private BigDecimal getDeviation(List<Point> points) {
		MathContext mc = precision.getMathContext();
		BigDecimal EE = BigDecimal.ZERO;
		for (Point point : points) {
			BigDecimal xInitial = point.getX();
			BigDecimal yInitial = point.getY();
			BigDecimal yApproximated = approximated.evaluateAt(new Variable("x", xInitial)).toBigDecimal(precision);
			approximatedPoints.add(new Point(xInitial, yApproximated));

			BigDecimal epsilon = (yApproximated.subtract(yInitial, mc));
			epsilons.add(epsilon);
			EE = EE.add(epsilon.pow(2, mc), mc);
		}
		return EE;
	}

	private BigDecimal getStandardDeviation(List<Point> points) {
		MathContext mc = precision.getMathContext();
		BigDecimal n = BigDecimal.valueOf(points.size());
		return (deviation.divide(n, mc)).sqrt(mc);
	}

	private BigDecimal getDetermination(List<Point> points) {
		MathContext mc = precision.getMathContext();

		BigDecimal phiSum, numerator, denominator;
		phiSum = numerator = denominator = BigDecimal.ZERO;
		
		for (Point point : approximatedPoints) {
			phiSum = phiSum.add(point.getY(), mc);
		}

		BigDecimal n = BigDecimal.valueOf(points.size());
		BigDecimal phiAvg = phiSum.divide(n, mc);

		// point.size() == approximatedPoints.size() - ЭТО ВАЖНО!
		for (int i = 0; i < points.size(); i++) {
			BigDecimal yInitial = points.get(i).getY();
			BigDecimal yApproximated = approximatedPoints.get(i).getY();

			numerator = numerator.add((yInitial.subtract(yApproximated, mc)).pow(2, mc), mc);
			denominator = denominator.add((yInitial.subtract(phiAvg, mc)).pow(2, mc), mc);
		}

		return BigDecimal.ONE.subtract(numerator.divide(denominator, mc), mc);
	}

	public BigDecimal calculateCorrelation() {
		MathContext mc = precision.getMathContext();

		BigDecimal xSum, ySum, numerator, denominator, XX, YY;
		xSum = ySum = numerator = XX = YY = BigDecimal.ZERO;
		for (Point point : approximatedPoints) {
			xSum = xSum.add(point.getX(), mc);
			ySum = ySum.add(point.getY(), mc);
		}

		BigDecimal n = BigDecimal.valueOf(approximatedPoints.size());
		BigDecimal xAvg = xSum.divide(n, mc);
		BigDecimal yAvg = ySum.divide(n, mc);

		for (Point point : approximatedPoints) {
			BigDecimal xDiff = (point.getX().subtract(xAvg, mc));
			BigDecimal yDiff = (point.getY().subtract(yAvg, mc));

			numerator = numerator.add(xDiff.multiply(yDiff, mc), mc);
			XX = XX.add(xDiff.pow(2, mc), mc);
			YY = YY.add(yDiff.pow(2, mc), mc);
		}
		denominator = XX.multiply(YY, mc).sqrt(mc);
		return numerator.divide(denominator, mc);
	}

	public void setCorrelation(BigDecimal correlation) {
		this.correlation = Optional.ofNullable(correlation);
	}

	public String toBeautifulString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s\tS = %s\tδ = %s\t R^2 = %s", approximated.toString(precision), deviation.toPlainString(),
				standardDeviation.toPlainString(), determination.toPlainString()));
		if (correlation.isPresent()) {
			builder.append(String.format("\tr = %s", correlation.get().toPlainString()));
		}
		return builder.toString();
	}

	public Expression get() {
		return this.approximated;
	}

	public Precision getPrecision() {
		return precision;
	}

	public BigDecimal getDeviation() {
		return deviation;
	}

	public BigDecimal getStandardDeviation() {
		return standardDeviation;
	}

	public BigDecimal getDetermination() {
		return determination;
	}

	public List<Point> getApproximatedPoints() {
		return approximatedPoints;
	}

	public List<BigDecimal> getEpsilons() {
		return epsilons;
	}

	public Optional<BigDecimal> getCorrelation() {
		return correlation;
	}
}
