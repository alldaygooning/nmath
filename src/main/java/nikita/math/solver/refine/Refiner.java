package nikita.math.solver.refine;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.hipparchus.optim.InitialGuess;
import org.hipparchus.optim.MaxEval;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.SimpleBounds;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunction;
import org.hipparchus.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.hipparchus.optim.nonlinear.scalar.noderiv.CMAESOptimizer.PopulationSize;
import org.hipparchus.optim.nonlinear.scalar.noderiv.CMAESOptimizer.Sigma;
import org.hipparchus.random.RandomDataGenerator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.MultiVariateNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.extremum.Extremum;
import nikita.math.construct.extremum.Maximum;
import nikita.math.construct.extremum.Minimum;

public abstract class Refiner {

	static final int EXTRA_PRECISION = 5;

	static final BigDecimal STEP_SIZE_MINIMUM = BigDecimal.valueOf(0.1d);
	static final BigDecimal INTERVAL_LENGTH_MINIMUM = BigDecimal.valueOf(0.2d);
	static final BigDecimal INTERVAL_LENGTH_MAXIMUM = BigDecimal.valueOf(9.5f);

	static final int CMAES_STEPS_MAX = 30000;
	static final int SIGMA_LENGTH = 1;
	static final double SIGMA_VALUE_MINIMUM = 1e-300;

	public static Extremum hipparchusRefine(Expression expression, Interval interval, Precision precision, GoalType goal) {
		ExprEvaluator evaluator = new ExprEvaluator();
		EvalEngine engine = evaluator.getEvalEngine();
		engine.setNumericMode(true, precision.getNPrecision(), -1);

		MathContext mathContext = new MathContext(precision.getPrecision().intValue(), RoundingMode.HALF_UP);

		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();
		BigDecimal length = interval.getLength();

		double[] initialApproximation = new double[] { left.doubleValue() };

		double[] lowerBound = new double[] { left.doubleValue() };
		double[] upperBound = new double[] { right.doubleValue() };
		SimpleBounds simpleBounds = new SimpleBounds(lowerBound, upperBound);

		CMAESOptimizer optimizer = new CMAESOptimizer(CMAES_STEPS_MAX, //
				0, //
				true, //
				10, //
				0, //
				new RandomDataGenerator(), //
				true, //
				null);

		double sigma = getSigma(length);
		double[] sigmaValues = new double[] { sigma };
		IExpr function = engine.evaluate(expression.toString());
		IAST variables = F.List(F.symbol("x"));

		PointValuePair optimum = optimizer.optimize( //
				new MaxEval(CMAES_STEPS_MAX), // maximum evaluations
				new ObjectiveFunction(new MultiVariateNumerical(function, variables)), // our function to minimize
				goal,
				new InitialGuess(initialApproximation), //
				simpleBounds, //
				new PopulationSize(5), //
				new Sigma(sigmaValues));

		if (optimum != null) {
			double optimumX = optimum.getPoint()[0];
			double optimumY = optimum.getValue();

			if (Double.isNaN(optimumX) || Double.isNaN(optimumY)) {
				return null;
			}

			BigDecimal x = new BigDecimal(Double.toString(optimumX), mathContext);
			BigDecimal y = new BigDecimal(Double.toString(optimumY), mathContext);
			if (goal == GoalType.MAXIMIZE) {
				return new Maximum(x, y);
			} else if (goal == GoalType.MINIMIZE) {
				return new Minimum(x, y);
			}
		}

		return null;
	}

	private static double getSigma(BigDecimal intervalLength) {
		double sigma = intervalLength.doubleValue() / 2f;
		if (sigma == 0) {
			sigma = SIGMA_VALUE_MINIMUM;
		}
		return sigma;
	}
}
