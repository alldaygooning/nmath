package nikita.math.solver.refine;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

import nikita.logging.NLogger;
import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.extremum.Maximum;

public class MaximumRefiner extends Refiner {

	static final String prefix = "MaximumRefiner";

	public static Maximum refine(Expression expression, Interval interval, String var, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		EvalEngine engine = evaluator.getEvalEngine();
		engine.setNumericMode(true, precision.getNPrecision(), -1);

		MathContext mathContext = new MathContext(precision.getPrecision().intValue() + extraPrecision, RoundingMode.HALF_UP);
		Precision adjustedPrecision = new Precision(precision.getString());
		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(extraPrecision)));

		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();
		BigDecimal length = interval.getLength();

		if (length.compareTo(intervalMinLength) >= 0) {
			String function = String.format("%s, %s>=%s && %s<=%s", expression, var, left.toPlainString(), var, right.toPlainString());
			String domain = String.format("%s, %s", var, left.toPlainString());
			String command = String.format("FindMaximum({%s}, {%s}, Method -> \"CMAES\")", function, domain);
			IExpr rule = engine.evaluate(command);
			BigDecimal y = NMath.getBigDecimal(rule.getAt(1), precision);
			BigDecimal x = NMath.getBigDecimal(rule.getAt(2).getAt(1).getAt(2), precision);
			return new Maximum(x, y);
		}

		info(String.format("Interval is shorter than Symja iteration step for Local Maximum Search: %s < %s.", length.toPlainString(),
				intervalMinLength.toPlainString()));

//		if (!NTrigonometry.containsTrigFunction(engine.parse(expression.toString()))) {
//			Expression derivative = expression.derivative(var);
//			IExpr solution = NMath.solve(derivative, "0", adjustedPrecision).getAt(1).getAt(1).getAt(2);
//			System.out.println(solution);
//			return null;
//		}

//		info("Interval contains trigonometric function(s). Cannot solve using Symja.");

		Maximum maximum = (Maximum) hipparchusRefine(expression, interval, adjustedPrecision, GoalType.MAXIMIZE);
		if (maximum != null) {
			return maximum;
		}

		info("Hipparchus CMAES Optimization failed.");

		info("Resorting to brute force iterations.");
		BigDecimal x = left;
		BigDecimal step = precision.getAccuracy().min(minimalStep);

		maximum = null;
		while (x.compareTo(right) <= 0) {
			BigDecimal y = NMath.getBigDecimal(NMath.replaceAll(expression, x.toPlainString()), adjustedPrecision);
			if (maximum == null || maximum.getY().compareTo(y) < 0) {
				maximum = new Maximum(x, y);
			}
			x = x.add(step, mathContext);
		}
		return maximum;
	}

	private static void info(String message) {
		NLogger.info(String.format("%s: %s", prefix, message));
	}
}
