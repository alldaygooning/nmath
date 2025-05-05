package nikita.math.solver.approximate.transcendental.power;

import java.math.MathContext;

import ch.obermuhlner.math.big.BigDecimalMath;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.solver.approximate.transcendental.PseudoLinearFunctionApproximator;

public class PowerFunctionApproximator extends PseudoLinearFunctionApproximator {

	private static final String FULL_NAME = "Power Function Approximator";
	private static final String LOGGER_NAME = "PowerFunctionApproximator";
	private static final String SHORT_NAME = "power";

	public PowerFunctionApproximator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
	}

	@Override
	protected Point substitutePoint(Point point, Precision precision) {
		MathContext mc = precision.getMathContext();
		return new Point(BigDecimalMath.log(point.getX(), mc), BigDecimalMath.log(point.getY(), mc));
	}

	@Override
	protected boolean satisfies(Point point) {
		return (point.getX().signum() == 1 && point.getY().signum() == 1);
	}

	@Override
	protected Expression getApproximated(Expression coefficient, Expression constant, Precision precision) {
		Expression a = Expression.E.power(constant, precision);
		Expression b = coefficient;
		return new Expression(String.format("(%s)*(x^(%s))", a.toString(precision), b.toString(precision)));
	}
}
