package nikita.math.construct.expression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;

import nikita.math.NMath;
import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.equation.Equation;
import nikita.math.construct.expression.util.ExpressionUtils;
import nikita.math.construct.point.singularity.Discontinuity;
import nikita.math.construct.point.singularity.DiscontinuityInterval;
import nikita.math.construct.point.singularity.DiscontinuityPoint;
import nikita.math.exception.construct.expression.ExpressionConversionException;
import nikita.math.exception.construct.interval.IncontinuousIntervalException;
import nikita.math.trigonometry.NTrigonometry;

public class Expression {

	public static final Expression ZERO = new Expression("0");
	public static final Expression ONE = new Expression("1");
	public static final Expression TWO = new Expression("2");
	public static final Expression E = new Expression("E");

	String string;

	public Expression(String string) {
		this.string = string.replaceAll("(?<![A-Za-z])e(?![A-Za-z])", "E");
	}

	public Expression(Expression expression) {
		this.string = expression.string;
	}

	public boolean isTrigonometric() {
		ExprEvaluator evaluator = new ExprEvaluator();
		return NTrigonometry.containsTrigFunction(evaluator.eval(string));
	}

	public boolean isContinious(Interval interval) { // Пока вот так
		return (this.getSingularities(interval).size() == 0);
	}

	public boolean isComplex() {
		IExpr expr = getEvaluatedExpr();
		return (expr.isComplex() || expr.isComplexNumeric());
	}

	public boolean isIndeterminate() {
		return this.getEvaluatedExpr().isIndeterminate();
	}

	public boolean isInfinity() {
		IExpr expr = this.getEvaluatedExpr();
		return expr.isInfinity() || expr.isNegativeInfinity() || expr.isComplexInfinity();
	}

	public boolean isNumeric() {
		try {
			this.getEvaluatedExpr().evalf();
		}catch(ArgumentTypeException e) {
			return false;
		}
		return true;
	}

	public boolean isNegative() {
		return this.getEvaluatedExpr().isNegative();
	}

	public boolean checkIVT(Interval interval) {
		if (!this.isContinious(interval)) {
			return false;
		}

		BigDecimal left = interval.getLeft();
		BigDecimal right = interval.getRight();

		try {
			BigDecimal fLeft = this.evaluateAt(new Variable("x", left)).toBigDecimal();
			BigDecimal fRight = this.evaluateAt(new Variable("x", right)).toBigDecimal();
			if (fLeft.multiply(fRight).compareTo(BigDecimal.ZERO) > 0) {
				return false;
			}
			if (fLeft.compareTo(BigDecimal.ZERO) == 0 && fRight.compareTo(BigDecimal.ZERO) == 0) {
				return false;
			}
		} catch (ExpressionConversionException e) {
			throw new IncontinuousIntervalException(this, interval);
		}

		return true;
	}

	public boolean containsSymbol(Variable variable) {
		return ExpressionUtils.containsSymbol(this.getExpr(true), variable);
	}

	// ОПЕРАЦИИ

	public Expression inverse() {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("(1/(%s))", string);
		return (new Expression(evaluator.eval(command).toString()));
	}

	public Expression multiply(Expression other) {
		return this.multiply(other, NMath.DEFAULT_EXPRESSION_PRECISION);
	}

	public Expression multiply(Expression other, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("((%s)*(%s))", string, other.string);
		OutputFormFactory off = NMath.getOutputFormFactory(precision);
		return (new Expression(off.toString(evaluator.eval(command))));
	}

	public Expression divide(Expression other, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("((%s)/(%s))", string, other.string);
		OutputFormFactory off = NMath.getOutputFormFactory(precision);
		return (new Expression(off.toString(evaluator.eval(command))));
	}

	public Expression sub(Expression other) {
		return this.sub(other, NMath.DEFAULT_EXPRESSION_PRECISION);
	}

	public Expression sub(Expression other, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("(%s)-(%s)", string, other.string);
		OutputFormFactory off = NMath.getOutputFormFactory(precision);
		return (new Expression(off.toString(evaluator.eval(command))));
	}
	
	public Expression add(Expression other, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("(%s)+(%s)", string, other.string); 
		OutputFormFactory off = NMath.getOutputFormFactory(precision);
		return (new Expression(off.toString(evaluator.eval(command))));
	}

	public Expression negative() {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("-(%s)", string);
		return (new Expression(evaluator.eval(command).toString()));
	}

	public Expression derivative(String variable) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("D(%s, %s)", this.string, variable);
		return new Expression(evaluator.eval(command).toString());
	}

	public Expression antiderivative(Variable variable) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("Integrate(%s, %s)", string, variable.getName());
		return new Expression(evaluator.eval(command).toString());
	}

	public Expression derivative(Variable variable) {
		return this.derivative(variable.getName());
	}

	public Expression simplify(Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("FullSimplify(%s)", this.rationalize(precision).getString());
		IExpr expr = evaluator.eval(command);
		if (expr.isIndeterminate() || expr.head().equals(F.FullSimplify)) {
			return this;
		}
		OutputFormFactory off = NMath.getOutputFormFactory(precision);
		return new Expression(off.toString(expr));
	}

	// Добавить сюда проверку на пропавшие переменные. Он не должен делать так,
	// чтобы они пропали!!
	public Expression rationalize(Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("Rationalize(%s)", string); // ну может сюда ноль воткнуть?
		IExpr expr = evaluator.eval(command);
		if (expr.isIndeterminate() || expr.head().equals(F.Rationalize)) {
			return this;
		}
		OutputFormFactory off = NMath.getOutputFormFactory(precision);
		return new Expression(off.toString(expr));
	}

	public Expression power(Expression other, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("(%s)^(%s)", string, other.string);
		return new Expression(NMath.getOutputFormFactory(precision).toString(evaluator.eval(command)));
	}

	// ВЫЧИСЛЕНИЕ

	public Expression evaluateAt(Variable variable) {
		Precision precision = new Precision(variable.getStringValue());
		return this.evaluateAt(variable, precision);
	}

	public Expression evaluateAt(Variable variable, Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		OutputFormFactory off = NMath.getOutputFormFactory(precision);

		String command = String.format("ReplaceAll(%s, %s->(%s))", this.toString(precision), variable.getName(),
				variable.getStringValue(precision));
		IExpr expr = evaluator.eval(command);
		try {
			String result = off.toString(evaluator.eval(F.N(expr, (long) precision.getNPrecision())));
			return new Expression(result);
		} catch (IterationLimitExceeded e) {
			return new Expression(off.toString(expr));
		}
	}

	// BIGDECIMAL!

	public BigDecimal toBigDecimal(Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		OutputFormFactory off = NMath.getOutputFormFactory(precision);

		String nPrecision = String.valueOf(precision.getNPrecision());
		String command = String.format("N(%s, %s)", this.toString(), nPrecision);
		String formatted = NMath.bigDecimalNormalize(off.toString(evaluator.eval(command)));

		try {
			return NMath.getBigDecimal(formatted, precision);
		} catch (NumberFormatException e) {
			throw new ExpressionConversionException(this);
		}
	}

	public BigDecimal toBigDecimal() {
		return this.toBigDecimal(NMath.DEFAULT_BIGDECIMAL_PRECISION);
	}

	public Expression toNumeric() {
		return this.toNumeric(NMath.DEFAULT_PRECISION);
	}

	public Expression toNumeric(Precision precision) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("N(%s, %s)", string, precision.getNPrecision());
		return new Expression(evaluator.eval(command).toString());
	}

	// GETTERS & SETTERS //

	public IExpr getExpr() {
		return this.getExpr(false);
	}

	public IExpr getExpr(boolean holdForm) {
		ExprEvaluator evaluator = new ExprEvaluator();
		if (holdForm) {
			String command = String.format("HoldForm(%s)", string);
			return evaluator.eval(command);
		}
		return evaluator.parse(string);
	}

	public IExpr getEvaluatedExpr() {
		return (new ExprEvaluator()).eval(this.getExpr());
	}

	public List<Expression> getDenominators() {
		List<Expression> denominators = new ArrayList<Expression>();
		ExpressionUtils.searchDenominators(this.getExpr(true), denominators);
		return denominators;
	}

	public List<Expression> getTanParams() {
		List<Expression> tanParams = new ArrayList<Expression>();
		ExpressionUtils.searchTan(getExpr(true), tanParams);
		return tanParams;
	}

	public List<Expression> getCotParams() {
		List<Expression> cotParams = new ArrayList<Expression>();
		ExpressionUtils.searchCot(getExpr(true), cotParams);
		return cotParams;
	}

	public List<Expression> getLogBases() {
		List<Expression> logBases = new ArrayList<Expression>();
		ExpressionUtils.searchLogBases(getExpr(true), logBases);
		return logBases;
	}

	public List<Expression> getLogValues() {
		List<Expression> logValues = new ArrayList<Expression>();
		ExpressionUtils.searchLogValues(getExpr(true), logValues);
		return logValues;
	}

	public List<Expression> getEvenRootParams() {
		List<Expression> eventRootParams = new ArrayList<Expression>();
		ExpressionUtils.searchEvenRootParams(getExpr(true), eventRootParams);
		return eventRootParams;
	}

	public String getWolframString() {
		return this.getExpr().toMMA();
	}

	public Equation getEquation(Expression other) {
		return new Equation(this, other);
	}

	public List<Discontinuity> getSingularities(Interval interval) {

		List<Discontinuity> discontinuities = new ArrayList<Discontinuity>();

		// СИГНУЛЯРНОСТИ ПО ЛОГАРИФМАМ
		List<Expression> logBases = this.getLogBases();
		final List<DiscontinuityPoint> lbDiscontinuityPoints = new ArrayList<DiscontinuityPoint>();
		final List<DiscontinuityInterval> lbDiscontinuityIntervals = new ArrayList<DiscontinuityInterval>();

		logBases.forEach(base -> {
			if (!base.containsSymbol(new Variable("x"))) {
				return;
			}
			Equation equationOne = new Equation(base, ONE);
			List<Expression> points = equationOne.solve(interval, new Variable("x"), NMath.DEFAULT_EXPRESSION_PRECISION);
			points.forEach(point -> {
				lbDiscontinuityPoints.add(Discontinuity.atPoint(this, point));
			});

			Equation equationZero = new Equation(base, ZERO);

			List<Expression> partitionPoints = new ArrayList<Expression>();
			partitionPoints.add(new Expression(interval.getLeft().toPlainString()));
			partitionPoints.addAll(equationZero.solve(interval, new Variable("x"), NMath.DEFAULT_EXPRESSION_PRECISION));
			partitionPoints.add(new Expression(interval.getRight().toPlainString()));

			for (int i = 0; i < partitionPoints.size() - 1; i++) {
				Expression pp1 = partitionPoints.get(i);
				Expression pp2 = partitionPoints.get(i + 1);
				if (!pp1.isNumeric() || !pp2.isNumeric()) {
					continue;
				}

				MathContext mc = NMath.DEFAULT_BIGDECIMAL_PRECISION.getMathContext();
				BigDecimal ppLeft = pp1.toBigDecimal();
				BigDecimal ppRight = pp2.toBigDecimal();
				if (ppLeft.compareTo(ppRight) > 0) {
					BigDecimal tmp = ppLeft;
					ppLeft = ppRight;
					ppRight = tmp;
				}
				BigDecimal ppMid = ppRight.add(ppLeft, mc).divide(BigDecimal.valueOf(2), mc);
				Expression evaluation = base.evaluateAt(new Variable("x", ppMid));
				if (evaluation.isComplex() || evaluation.isNegative() || evaluation.equals(ZERO)) {
					if (!ppLeft.equals(ppRight)) {
						lbDiscontinuityIntervals.add(Discontinuity.atInterval(this, new Interval(ppLeft, ppRight)));
					} else {
						lbDiscontinuityPoints.add(Discontinuity.atPoint(this, pp1));
					}
				}

			}
		});

		List<Expression> logValues = this.getLogValues();
		final List<DiscontinuityInterval> lvDiscontinuityIntervals = new ArrayList<DiscontinuityInterval>();
		final List<DiscontinuityPoint> lvDiscontinuityPoints = new ArrayList<DiscontinuityPoint>();

		logValues.forEach(value -> {
			if (!value.containsSymbol(new Variable("x"))) {
				return;
			}

			Equation equationZero = new Equation(value, ZERO);
			List<Expression> partitionPoints = new ArrayList<Expression>();
			partitionPoints.add(new Expression(interval.getLeft().toPlainString()));
			partitionPoints.addAll(equationZero.solve(interval, new Variable("x"), NMath.DEFAULT_EXPRESSION_PRECISION));
			partitionPoints.add(new Expression(interval.getRight().toPlainString()));

			for (int i = 0; i < partitionPoints.size() - 1; i++) {
				Expression pp1 = partitionPoints.get(i);
				Expression pp2 = partitionPoints.get(i + 1);
				if (!pp1.isNumeric() || !pp2.isNumeric()) {
					continue;
				}

				MathContext mc = NMath.DEFAULT_BIGDECIMAL_PRECISION.getMathContext();
				BigDecimal ppLeft = pp1.toBigDecimal();
				BigDecimal ppRight = pp2.toBigDecimal();
				if (ppLeft.compareTo(ppRight) > 0) {
					BigDecimal tmp = ppLeft;
					ppLeft = ppRight;
					ppRight = tmp;
				}
				BigDecimal ppMid = ppRight.add(ppLeft, mc).divide(BigDecimal.valueOf(2), mc);
				Expression evaluation = value.evaluateAt(new Variable("x", ppMid));
				if (evaluation.isComplex() || evaluation.isNegative() || evaluation.equals(ZERO)) {
					if (!ppLeft.equals(ppRight)) {
						lvDiscontinuityIntervals.add(Discontinuity.atInterval(this, new Interval(ppLeft, ppRight)));
					} else {
						lvDiscontinuityPoints.add(Discontinuity.atPoint(this, pp1));
					}
				}
			}
		});

		List<Discontinuity> lDecoupledDiscontinuities = new ArrayList<Discontinuity>();
		// Log(b, a), b == 0
		lDecoupledDiscontinuities.addAll(lbDiscontinuityIntervals); // Log(b, a), b < 0
		lDecoupledDiscontinuities.addAll(lbDiscontinuityPoints); // Log(b, a), b == 1
		lDecoupledDiscontinuities.addAll(lvDiscontinuityIntervals);// Log(b, a), a < 0
		lDecoupledDiscontinuities.addAll(lvDiscontinuityPoints); // Log(b, a), a == 0
		lDecoupledDiscontinuities = Discontinuity.decouple(lDecoupledDiscontinuities);

		// СИНГУЛЯРНОСТИ ПО КОРНЯМ

		List<Expression> evenRootParams = this.getEvenRootParams();
		List<DiscontinuityInterval> erpDiscontinuityIntervals = new ArrayList<DiscontinuityInterval>();

		evenRootParams.forEach(rootParam -> {
			if (!rootParam.containsSymbol(new Variable("x"))) {
				return;
			}

			Equation equationZero = new Equation(rootParam, ZERO);
			List<Expression> partitionPoints = new ArrayList<Expression>();
			partitionPoints.add(new Expression(interval.getLeft().toPlainString()));
			partitionPoints.addAll(equationZero.solve(interval, new Variable("x"), NMath.DEFAULT_EXPRESSION_PRECISION));
			partitionPoints.add(new Expression(interval.getRight().toPlainString()));

			for (int i = 0; i < partitionPoints.size() - 1; i++) {
				Expression pp1 = partitionPoints.get(i);
				Expression pp2 = partitionPoints.get(i + 1);
				if (!pp1.isNumeric() || !pp2.isNumeric()) {
					continue;
				}

				MathContext mc = NMath.DEFAULT_BIGDECIMAL_PRECISION.getMathContext();
				BigDecimal ppLeft = pp1.toBigDecimal();
				BigDecimal ppRight = pp2.toBigDecimal();
				if (ppLeft.compareTo(ppRight) > 0) {
					BigDecimal tmp = ppLeft;
					ppLeft = ppRight;
					ppRight = tmp;
				}
				BigDecimal ppMid = ppRight.add(ppLeft, mc).divide(BigDecimal.valueOf(2), mc);
				Expression evaluation = rootParam.evaluateAt(new Variable("x", ppMid));
				if (evaluation.isComplex() || evaluation.isNegative()) {
					erpDiscontinuityIntervals.add(Discontinuity.atInterval(this, new Interval(ppLeft, ppRight)));
				}
			}
		});
		
		
		// СИНГУЛЯРНОСТИ ПО ЧИСЛИТЕЛЯМ

		List<Expression> denominators = this.getDenominators();
		List<Discontinuity> denomDiscontinuities = new ArrayList<Discontinuity>();
		for (Expression denominator : denominators) {
			Equation equation = new Equation(denominator, ZERO);
			List<Expression> points = equation.solve(interval, new Variable("x"), NMath.DEFAULT_EXPRESSION_PRECISION);
			points.forEach(point -> denomDiscontinuities.add(Discontinuity.atPoint(this, point)));
		}
		
		// СИНГУЛЯРНОСТИ ПО ТАНГЕНСАМ

		List<Expression> tanParams = this.getTanParams();
		List<Expression> tanParamValues = interval.getPeriodicRoots(new Expression("Pi/2"), new Expression("Pi*n"),
				NMath.DEFAULT_EXPRESSION_PRECISION);
		List<Discontinuity> tanDiscontinuities = new ArrayList<Discontinuity>();
		for (Expression tanParam : tanParams) {
			for (Expression tanParamValue : tanParamValues) {
				Equation equation = new Equation(tanParam, tanParamValue);
				List<Expression> points = equation.solve(interval, new Variable("x"), NMath.DEFAULT_EXPRESSION_PRECISION);
				points.forEach(point -> tanDiscontinuities.add(Discontinuity.atPoint(this, point)));
			}
		}

		// СИГНУЛЯРНОСТИ ПО КОТАНГЕНСАМ

		List<Expression> cotParams = this.getCotParams();
		List<Expression> cotParamValues = interval.getPeriodicRoots(Expression.ZERO, new Expression("Pi*n"),
				NMath.DEFAULT_EXPRESSION_PRECISION);
		List<Discontinuity> cotDiscontinuities = new ArrayList<Discontinuity>();
		for (Expression cotParam : cotParams) {
			for (Expression cotParamValue : cotParamValues) {
				Equation equation = new Equation(cotParam, cotParamValue);
				List<Expression> points = equation.solve(interval, new Variable("x"), NMath.DEFAULT_EXPRESSION_PRECISION);
				points.forEach(point -> cotDiscontinuities.add(Discontinuity.atPoint(this, point)));
			}
		}

		discontinuities.addAll(lDecoupledDiscontinuities);
		discontinuities.addAll(erpDiscontinuityIntervals);
		discontinuities.addAll(denomDiscontinuities);
		discontinuities.addAll(tanDiscontinuities);
		discontinuities.addAll(cotDiscontinuities);
		discontinuities = Discontinuity.decouple(discontinuities);

		return (discontinuities);
	}

	// OVERRIDES //

	public String toString(Precision precision) {
		OutputFormFactory outputFormFactory = NMath.getOutputFormFactory(precision);

		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("N(%s, %s)", string, precision.getNPrecision());

		return outputFormFactory.toString(evaluator.eval(command), true);
	}

	@Override
	public String toString() {
		return this.toString(NMath.DEFAULT_EXPRESSION_PRECISION);
	}

	public String getString() {
		return string;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Expression) {
			Expression other = (Expression) obj;
			return this.getExpr().toString().equals(other.getExpr().toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return string != null ? string.hashCode() : 0;
	}
}
