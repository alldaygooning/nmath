package nikita.math.solver.root.single;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.matheclipse.core.interfaces.IExpr;
import org.xml.sax.SAXException;

import nikita.external.api.WolframAPI;
import nikita.logging.NLogger;
import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Point;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.exception.construct.root.RootsNumberEstimationException;
import nikita.math.solver.root.RootFinder;

public abstract class SingleRootFinder extends RootFinder {

	static final String prefix = "SingleRootFinder";

	public static int estimateRootsNumber(Expression expression, Interval interval, Precision precision) {
		if (!expression.checkIVT(interval)) {
			return 0;
		}
		
		Expression derivative = expression.derivative("x");

		List<Point> criticalPoints = new ArrayList<Point>();

		if (expression.isTrigonometric()) {
			info(String.format("%s is a trigonometric function. Querying Wolfram for solutions.", derivative));
			try {
				criticalPoints.addAll(getWolframPoints(derivative, interval, precision));
			} catch (ParserConfigurationException | SAXException | IOException | InterruptedException | XPathExpressionException e) {
				throw new RootsNumberEstimationException(String.format("Error occured trying to estimate number of %s roots on %s: %s",
						expression, interval, e.getMessage()));
			}
		}
		else {
			criticalPoints.addAll(getSymjaPoints(derivative, interval, precision));
		}

		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();

		Point leftEndpoint = new Point(left, NMath.getBigDecimal(expression.evaluateAt(left.toPlainString()), precision));
		Point rightEndpoint = new Point(right, NMath.getBigDecimal(expression.evaluateAt(right.toPlainString()), precision));

		List<Point> partitionPoints = new ArrayList<Point>();
		partitionPoints.add(leftEndpoint);
		for (Point criticalPoint : criticalPoints) {
			BigDecimal x = criticalPoint.getX();
			BigDecimal y = NMath.getBigDecimal(expression.evaluateAt(x.toPlainString()), precision);
			Point partitionPoint = new Point(x, y);
			partitionPoints.add(partitionPoint);
		}
		partitionPoints.add(rightEndpoint);
		partitionPoints.sort((p1, p2) -> p1.getX().compareTo(p2.getX()));

		info(String.format("Found partitions: %s", partitionPoints));

		int count = 0;
		for (int i = 0; i < partitionPoints.size() - 1; i++) {
			Point point1 = partitionPoints.get(i);
			Point point2 = partitionPoints.get(i + 1);

			if (point1.getY().signum() * point2.getY().signum() <= 0) {
				count++;
			}
		}

		for (int i = 1; i < partitionPoints.size() - 2; i++) {
			Point point = partitionPoints.get(i);

			if (isZero(point.getY(), precision)) {
				count--;
			}
		}

		return count;
	}

	private static List<Point> getWolframPoints(Expression expression, Interval interval, Precision precision)
			throws ParserConfigurationException, SAXException, IOException, InterruptedException, XPathExpressionException {

		List<Point> points = new ArrayList<Point>();
		String query = String.format("solve %s=0 for x in [%s, %s]", expression.getWolframString(), interval.getLeft().toPlainString(),
				interval.getRight().toPlainString());

		List<String> wolframRoots = WolframAPI.getSolutions(WolframAPI.query(query, precision));
		for (String wolframRoot : wolframRoots) {
			List<IExpr> roots = WolframAPI.getRules(wolframRoot);
			for (IExpr root : roots) {
				BigDecimal x = NMath.getBigDecimal(root.getAt(2), precision);
				BigDecimal y = NMath.getBigDecimal(expression.evaluateAt(x.toPlainString()), precision);

				Point point = new Point(x, y);
				points.add(point);
			}
		}

		return points;
	}

	private static List<Point> getSymjaPoints(Expression expression, Interval interval, Precision precision) {
		List<Point> points = new ArrayList<Point>();

		List<BigDecimal> roots = expression.roots("x", precision);
		for (BigDecimal x : roots) {
			BigDecimal y = NMath.getBigDecimal(expression.evaluateAt(x.toPlainString()), precision);

			Point point = new Point(x, y);
			points.add(point);
		}

		return points;
	}

	private static boolean isZero(BigDecimal number, Precision precision) {
		return number.abs().compareTo(precision.getAccuracy()) <= 0;
	}

	private static void info(String message) {
		NLogger.info(String.format("%s: %s", prefix, message));
	}
}
