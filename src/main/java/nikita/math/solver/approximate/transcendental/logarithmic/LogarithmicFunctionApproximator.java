package nikita.math.solver.approximate.transcendental.logarithmic;

import java.math.MathContext;

import ch.obermuhlner.math.big.BigDecimalMath;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.solver.approximate.transcendental.PseudoLinearFunctionApproximator;

public class LogarithmicFunctionApproximator extends PseudoLinearFunctionApproximator {

	private static final String FULL_NAME = "Logarithmic Function Approximator";
	private static final String LOGGER_NAME = "LogarithmicFunctionApproximator";
	private static final String SHORT_NAME = "log";

	public LogarithmicFunctionApproximator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
	}

	@Override
	protected Point substitutePoint(Point point, Precision precision) {
		MathContext mc = precision.getMathContext();
		return new Point(BigDecimalMath.log(point.getX(), mc), point.getY());
	}

	@Override
	protected boolean satisfies(Point point) {
		return (point.getX().signum() == 1);
	}

	@Override
	protected Expression getApproximated(Expression coefficient, Expression constant, Precision precision) {
		Expression a = coefficient;
		Expression b = constant;
		return new Expression(String.format("(%s)*ln(x)+(%s)", a.toString(precision), b.toString(precision)));
	}
}
