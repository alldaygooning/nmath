package nikita.math.solver.integrate.rectangle;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.calculus.integral.Integral;
import nikita.math.construct.point.Point;
import nikita.math.exception.construct.expression.ExpressionConversionException;
import nikita.math.exception.construct.expression.ExpressionEvaluationException;
import nikita.math.exception.construct.integral.IntegrationException;
import nikita.math.solver.integrate.Integrator;
import nikita.math.solver.integrate.IntegratorMode;
import nikita.math.solver.integrate.Multimodal;

public class RectangleIntegrator extends Integrator implements Multimodal {

	RectangleIntegratorMode mode;

	public RectangleIntegrator() {
	}

	public RectangleIntegrator(RectangleIntegratorMode mode) {
		this.mode = mode;
	}

	public BigDecimal evaluate(Integral integral, int n, Precision precision) {

		BigDecimal int1 = this.integrate(integral, n, precision);
		n = n * 2;
		BigDecimal int2 = this.integrate(integral, n, precision);

		while (!rungeCheck(int1, int2, precision)) {
			int1 = int2;
			n = n * 2;
			int2 = this.integrate(integral, n, precision);
		}

		MathContext mc = precision.getMathContext();
		return new BigDecimal(int2.toPlainString(), mc);
	}

	public BigDecimal integrate(Integral integral, int n, Precision precision) {
		Precision adjustedPrecision = precision.getAdjustedPrecision(EXTRA_PRECISION);
		MathContext mc = adjustedPrecision.getMathContext();

		Interval interval = new Interval(integral.getLower(), integral.getUpper());

		List<Point> partitionPoints;
		try {
			partitionPoints = this.mode.filter(getPartitionPoints(integral.getIntegrand(), interval, n, adjustedPrecision),
					integral.getIntegrand(), adjustedPrecision);
		} catch (ExpressionEvaluationException | ExpressionConversionException e) {
			// Значит, что не смогли во всех точках оценить функцию, потому что в каких-то
			// точках функция не определена
			throw new IntegrationException(integral, this.getName(),
					String.format("function should be continuous on Integration Interval %s.", interval.toString()));
		}

		BigDecimal sum = BigDecimal.ZERO;
		for (int i = 0; i < partitionPoints.size(); i++) {
			sum = sum.add(partitionPoints.get(i).getY(), mc);
		}

		BigDecimal subintervalLength = interval.getLength().divide(BigDecimal.valueOf(n), mc);
		sum = sum.multiply(subintervalLength, mc);
		this.logStep(n, sum, partitionPoints, sum);
		return sum;
	}

	public void setMode(String shorthand) {
		RectangleIntegratorMode mode = RectangleIntegratorMode.getByShorthand(shorthand);
		this.setMode(mode);
	}

	public void setMode(IntegratorMode mode) {
		if (mode instanceof RectangleIntegratorMode) {
			this.mode = (RectangleIntegratorMode) mode;
		}
	}

	@Override
	public String getName() {
		return String.format("%s-Rectangle Integration Method", this.mode.getPrefix());
	}

	@Override
	public String getLogName() {
		return String.format("%sRectangleIntegrator", this.mode.getPrefix());
	}

	@Override
	public String getShorthand() {
		return "rectangles";
	}
}
