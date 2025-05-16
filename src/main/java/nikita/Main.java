package nikita;

import java.math.BigDecimal;
import java.util.List;

import org.matheclipse.core.basic.Config;

import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.solver.interpolate.FunctionInterpolationContext;
import nikita.math.solver.interpolate.gauss.GaussFunctionInterpolator;

public class Main {
	static {
		Config.MAX_PRECISION_APFLOAT = 8192;
		Config.DOUBLE_TOLERANCE = 1.0E-64;
	}

	public static void main(String[] args) {

		Precision precision = new Precision("0.000001");

		List<Point> points = List.of(
				new Point(BigDecimal.valueOf(0.1), BigDecimal.valueOf(1.25)),
				new Point(BigDecimal.valueOf(0.2), BigDecimal.valueOf(2.38)),
				new Point(BigDecimal.valueOf(0.3), BigDecimal.valueOf(3.79)),
				new Point(BigDecimal.valueOf(0.4), BigDecimal.valueOf(5.44)),
				new Point(BigDecimal.valueOf(0.5), BigDecimal.valueOf(7.14))
				);
		
				BigDecimal x = BigDecimal.valueOf(0.35);
				GaussFunctionInterpolator interpolator = new GaussFunctionInterpolator();
				Expression interpolated = interpolator.interpolate(points, precision, new FunctionInterpolationContext(x));

	}
}
