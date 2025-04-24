package nikita.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;

import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.extremum.Maximum;
import nikita.math.construct.point.extremum.Minimum;
import nikita.math.solver.refine.MaximumRefiner;
import nikita.math.solver.refine.MinimumRefiner;

public class NMath {

	public static final Precision DEFAULT_BIGDECIMAL_PRECISION = new Precision("0.0000000000001");
	public static final Precision DEFAULT_PRECISION = new Precision("0.0000001");

	public static final Precision DEFAULT_EXPRESSION_PRECISION = new Precision("0.0000000000000001");

	public static IExpr replaceAll(Expression expression, String variable, String replacement) {
		ExprEvaluator evaluator = new ExprEvaluator();
		EvalEngine engine = evaluator.getEvalEngine();

		Precision precision = new Precision(replacement);
		engine.setNumericMode(true, Integer.valueOf(precision.getNPrecision()), -1);
		String formattedReplacement = engine.evaluate(replacement).toString();

		String command = String.format("ReplaceAll(%s, %s->(%s))", expression.toString(), variable, formattedReplacement);
		return engine.evaluate(command);
	}

	public static IExpr replaceAll(Expression expression, String variable, String replacement, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		EvalEngine engine = evaluator.getEvalEngine();

		engine.setNumericMode(true, Integer.valueOf(precision.getNPrecision()), -1);
		String formattedReplacement = engine.evaluate(replacement).toString();

		String command = String.format("ReplaceAll(%s, %s->(%s))", expression.toString(), variable, formattedReplacement);
		return engine.evaluate(command);
	}

	public static Maximum maximum(Expression expression, Interval interval, String var, Precision precision) {
		return MaximumRefiner.refine(expression, interval, var, precision);
	}

	public static Minimum minimum(Expression expression, Interval interval, String var, Precision precision) {
		return MinimumRefiner.refine(expression, interval, var, precision);
	}

	// UTILITY

	public static EvalEngine getEngine(Precision precision) {
		return getEngine(precision.getNPrecision());
	}

	public static EvalEngine getEngine(int nPrecision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		EvalEngine engine = evaluator.getEvalEngine();
		engine.setNumericMode(true, nPrecision, -1);
		return engine;
	}

	public static OutputFormFactory getOutputFormFactory(Precision precision) {
		int significantDigits = precision.getNPrecision();
		int exponentFigures = significantDigits + 2;
		return OutputFormFactory.get(true, false, exponentFigures, significantDigits);
	}

	// ВСЕ ПРО BIGDECIMAL

	public static BigDecimal getBigDecimal(String value, Precision precision) {
		int scale = precision.getPrecision().intValue();
		MathContext mc = new MathContext(scale);
		BigDecimal bd = new BigDecimal(bigDecimalNormalize(value), mc);
		bd = bd.setScale(scale, RoundingMode.HALF_UP);
		return bd;
	}

	public static BigDecimal getBigDecimal(IExpr expr, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		EvalEngine engine = evaluator.getEvalEngine();

		String nPrecision = String.valueOf(precision.getNPrecision());
		String command = String.format("N(%s, %s)", expr, nPrecision);
		engine.setNumericMode(true, Integer.valueOf(precision.getNPrecision()), -1);
		String formatted = engine.evaluate(command).toString();

		return (getBigDecimal(formatted, precision));
	}

	public static BigDecimal getBigDecimal(IExpr expr, Precision precision, int extraPrecision) { // Legacy method for extremum finders
		Precision adjustedPrecision = new Precision(precision.getString());
		adjustedPrecision.setPrecision(precision.getPrecision().add(new BigDecimal(extraPrecision)));
		return getBigDecimal(expr, adjustedPrecision);
	}

	public static BigDecimal getBigDecimal(IExpr expr) {
		return getBigDecimal(expr, DEFAULT_BIGDECIMAL_PRECISION);
	}

	public static BigDecimal getBigDecimal(String value) {
		return getBigDecimal(value, DEFAULT_BIGDECIMAL_PRECISION);
	}

	public static String bigDecimalNormalize(String string) {
		string = string.replace("*10^", "E");
		return string;
	}

	public static boolean equal(BigDecimal bd1, BigDecimal bd2, Precision precision) {
		int scale = precision.getPrecision().intValue();
		MathContext mc = new MathContext(scale);

		return (bd1.subtract(bd2, mc).abs().compareTo(precision.getAccuracy()) < 0);
	}
}
