package nikita.math.solver.approximate.polynomial;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import nikita.math.construct.Precision;
import nikita.math.construct.Variable;
import nikita.math.construct.equation.Equation;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;
import nikita.math.exception.solver.approximate.FunctionApproximationException;
import nikita.math.solver.approximate.FunctionApproximator;
import nikita.math.solver.approximate.MultimodalApproximator;

public class PolynomialFunctionApproximator extends FunctionApproximator implements MultimodalApproximator {

	private static final String FULL_NAME = "Polynomial Function Approximator";
	private static final String LOGGER_NAME = "PolynomialFunctionApproximator";
	private static final String SHORT_NAME = "polynomial";

	private int mode = 1; // По дефолту, будет искать полином первой степени, но вообще пока там стоит
							// исключение, если режим не задан

	public PolynomialFunctionApproximator() {
		super(FULL_NAME, LOGGER_NAME, SHORT_NAME);
	}

	@Override
	public Expression approximate(List<Point> points, Precision precision) {
		Precision adjustedPrecision = precision.getAdjustedPrecision(EXTRA_PRECISION);
		MathContext mc = adjustedPrecision.getMathContext();

		int power = 2 * mode;
		int lineCount = mode + 1;
		// X^(2n), X^(2n-1), ..., X
		List<BigDecimal> xSums = IntStream.range(0, power).mapToObj(i -> BigDecimal.ZERO).collect(Collectors.toList());
		// X^(n+1)Y, X^(n)Y, ..., Y
		List<BigDecimal> ySums = IntStream.range(0, lineCount).mapToObj(i -> BigDecimal.ZERO).collect(Collectors.toList());
		// Число точек
		BigDecimal n = BigDecimal.valueOf(points.size());

		for (Point point : points) {
			BigDecimal x = point.getX();
			BigDecimal y = point.getY();

			int xCurrentPower = power;
			for (int i = 0; i < xSums.size(); i++) {
				xSums.set(i, xSums.get(i).add(x.pow(xCurrentPower, mc)));
				xCurrentPower--;
			}

			int yCurrentPower = mode;
			for (int i = 0; i < ySums.size(); i++) {
				ySums.set(i, ySums.get(i).add((x.pow(yCurrentPower, mc)).multiply(y, mc), mc));
				yCurrentPower--;
			}
		}

		List<Expression> lines = new ArrayList<Expression>();
		for (int i = 0; i < lineCount; i++) {
			List<BigDecimal> coefficients;
			if (i + lineCount - 1 > xSums.size() - 1) {
				coefficients = new ArrayList<>(xSums.subList(i, i + (lineCount - 1)));
				coefficients.add(n);
			} else {
				coefficients = new ArrayList<>(xSums.subList(i, i + lineCount));
			}
			lines.add(getExpression(coefficients));
			System.out.println(getExpression(coefficients) + " = " + ySums.get(i));
		}

		List<Variable> vars = new ArrayList<Variable>();
		for (int i = 0; i < lines.size(); i++) {
			Expression expression = lines.get(i);
			for (Variable var : vars) {
				expression = expression.evaluateAt(var, precision);
			}
			Equation equation = new Equation(expression.simplify(adjustedPrecision), new Expression(ySums.get(i).toPlainString()));
			String varName = String.format("x%s", i + 1);
			try {
				Variable var = new Variable(varName, equation.solve(new Variable(varName), adjustedPrecision).get(0));
				updateVariables(vars, var, adjustedPrecision);
				vars.add(var);
			} catch (IndexOutOfBoundsException e) {
				throw new FunctionApproximationException(this, "Points most likely do not represent a real function");
			}
		}
		System.out.println(vars);
		return this.getApproximated(vars, adjustedPrecision);
	}

	private Expression getExpression(List<BigDecimal> coefficients) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < coefficients.size(); i++) {
			BigDecimal coefficient = coefficients.get(i);
			if (i != 0) {
				builder.append("+");
			}
			String varName = String.format("x%s", i + 1);
			builder.append(String.format("\"%s\"*(%s)", varName, coefficient.toPlainString()));
		}
		return new Expression(builder.toString());
	}

	private Expression getApproximated(List<Variable> coefficients, Precision precision) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < coefficients.size(); i++) {
			if (i != 0) {
				builder.append("+");
			}
			BigDecimal coefficient = coefficients.get(i).getSymbolicValue().toBigDecimal(precision);
			builder.append(String.format("(%s)", coefficient.toPlainString()));
			if (i != coefficients.size() - 1) {
				builder.append(String.format("*(x^(%s))", coefficients.size() - i - 1));
			}
		}
		return new Expression(builder.toString());
	}

	private void updateVariables(List<Variable> vars, Variable var, Precision precision) {
		for (int i = vars.size() - 1; i >= 0; i--) {
			Variable current = vars.get(i);
			current.setSymbolicValue(current.getSymbolicValue().evaluateAt(var, precision));
		}
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public int getMode() {
		return mode;
	}
}
