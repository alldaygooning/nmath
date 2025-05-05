package nikita;

import java.math.BigDecimal;
import java.util.List;

import org.matheclipse.core.basic.Config;

import nikita.math.construct.Precision;
import nikita.math.construct.point.Point;
import nikita.math.solver.approximate.FunctionApproximator;

public class Main {
	static {
		Config.MAX_PRECISION_APFLOAT = 8192;
		Config.DOUBLE_TOLERANCE = 1.0E-64;
	}

	public static void main(String[] args) {

		List<Point> points = List.of(new Point(BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0000)),
				new Point(BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.0889)),
				new Point(BigDecimal.valueOf(0.4), BigDecimal.valueOf(0.1773)),
				new Point(BigDecimal.valueOf(0.6), BigDecimal.valueOf(0.2629)),
				new Point(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3401)),
				new Point(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.4000)),
				new Point(BigDecimal.valueOf(1.2), BigDecimal.valueOf(0.4335)),
				new Point(BigDecimal.valueOf(1.4), BigDecimal.valueOf(0.4361)),
				new Point(BigDecimal.valueOf(1.6), BigDecimal.valueOf(0.4115)),
				new Point(BigDecimal.valueOf(1.8), BigDecimal.valueOf(0.3693)),
				new Point(BigDecimal.valueOf(2.0), BigDecimal.valueOf(0.3200))
//				new Point(BigDecimal.valueOf(2.2), BigDecimal.valueOf(0.2714))
		);
//		List<Point> points = List.of(new Point(BigDecimal.valueOf(0.5), BigDecimal.valueOf(1)),
//				new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)), new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
//				new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)), new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
//				new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)), new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
//				new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)), new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
//				new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)), new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
//				new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(1)));

		FunctionApproximator.approximate(points, new Precision("0.000000000000000000001"), "polynomial-2").toBeautifulString();
	}
}
