package nikita;

import java.math.BigDecimal;

import org.matheclipse.core.basic.Config;

import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.solver.approximate.differential.DifferentialFunctionApproximator;

public class Main {
	static {
		Config.MAX_PRECISION_APFLOAT = 8192;
		Config.DOUBLE_TOLERANCE = 1.0E-64;
	}

	public static void main(String[] args) {

		Precision precision = new Precision("0.001");
		Expression differential = new Expression("y+(1+x)y^2");
		Point initial = new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(-1));
		Interval interval = new Interval(BigDecimal.valueOf(1), BigDecimal.valueOf(1.5));
		BigDecimal step = BigDecimal.valueOf(0.1);

//		Expression differential = new Expression("x+y");
//		Point initial = new Point(BigDecimal.valueOf(0), BigDecimal.valueOf(1));
//		Interval interval = new Interval(BigDecimal.valueOf(0), BigDecimal.valueOf(2));
//		BigDecimal step = BigDecimal.valueOf(0.1);

//		System.out.println(DifferentialFunctionApproximator.dSolve(differential, initial, interval, step, "milne", precision)
//				.getDifferentialApproximated());
		System.out.println(DifferentialFunctionApproximator.dSolve(differential, initial, interval, step, "deuler", precision)
				.getActualApproximated());
		System.out.println(DifferentialFunctionApproximator.dSolve(differential, initial, interval, step, "moddeuler", precision)
				.getDifferentialApproximated());
	}
}
