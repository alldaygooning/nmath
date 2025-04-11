package nikita.math.solver.integrate.trapazoid;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import nikita.math.construct.Interval;
import nikita.math.construct.Point;
import nikita.math.construct.Precision;
import nikita.math.construct.calculus.Integral;
import nikita.math.exception.construct.expression.ExpressionConversionException;
import nikita.math.exception.construct.expression.ExpressionEvaluationException;
import nikita.math.exception.construct.integral.IntegrationException;
import nikita.math.solver.integrate.Integrator;

public class TrapazoidIntegrator extends Integrator {

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

	private BigDecimal integrate(Integral integral, int n, Precision precision) {
		Precision adjustedPrecision = precision.getAdjustedPrecision(EXTRA_PRECISION);
		MathContext mc = adjustedPrecision.getMathContext();

		Interval interval = new Interval(integral.getLower(), integral.getUpper());

		List<Point> partitionPoints;
		try {
			partitionPoints = getPartitionPoints(integral.getExpression(), interval, n, adjustedPrecision);
		} catch (ExpressionEvaluationException | ExpressionConversionException e) {
			// Значит, что не смогли во всех точках оценить функцию, потому что в каких-то
			// точках функция не определена
			throw new IntegrationException(integral, this.getName(),
					String.format("function should be continuous on Integration Interval %s.", interval.toString()));
		}

		BigDecimal sum = partitionPoints.get(0).getY().add(partitionPoints.get(partitionPoints.size() - 1).getY(), mc)
				.divide(BigDecimal.valueOf(2), mc);
		for (int i = 1; i < partitionPoints.size() - 1; i++) {
			sum = sum.add(partitionPoints.get(i).getY(), mc);
		}

		BigDecimal subintervalLength = interval.getLength().divide(BigDecimal.valueOf(n), mc);
		sum = sum.multiply(subintervalLength, mc);
		this.logStep(n, subintervalLength, partitionPoints, sum);
		return sum;
	}

	@Override
	public String getName() {
		return "Trapazoid Integration Method";
	}

	@Override
	public String getLogName() {
		return "TrapazoidIntegrator";
	}

	@Override
	public String getShorthand() {
		return "trapazoid";
	}
}
