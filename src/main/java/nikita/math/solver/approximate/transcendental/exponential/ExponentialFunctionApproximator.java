package nikita.math.solver.approximate.transcendental.exponential;

import java.math.MathContext;

import ch.obermuhlner.math.big.BigDecimalMath;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.solver.approximate.transcendental.PseudoLinearFunctionApproximator;

public class ExponentialFunctionApproximator extends PseudoLinearFunctionApproximator {

	private static final String FULL_NAME = "Exponential Function Approximator";
	private static final String LOGGER_NAME = "ExponentialFunctionApproximator";
	private static final String SHORT_NAME = "exp";

	public ExponentialFunctionApproximator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
	}

	@Override
	protected Point substitutePoint(Point point, Precision precision) {
		MathContext mc = precision.getMathContext();
		return new Point(point.getX(), BigDecimalMath.log(point.getY(), mc));
	}

	@Override
	protected boolean satisfies(Point point) {
		return (point.getY().signum() == 1);
	}

	@Override
	protected Expression getApproximated(Expression coefficient, Expression constant, Precision precision) {
		Expression a = Expression.E.power(constant, precision);
		Expression b = coefficient;
		return new Expression(String.format("(%s)*(e^((%s)*x))", a.toString(precision), b.toString(precision)));
	}

}
