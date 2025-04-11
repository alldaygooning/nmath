package nikita.math.construct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

import nikita.math.construct.expression.Expression;

public class Matrix {

	List<List<Expression>> expressions;
	int width;
	int height;

	private static final Precision DEFAULT_PRECISION = new Precision("0.0000001");

	private static final List<String> BROKEN_INDICATORS = Arrays.asList("NonCommutativeMultiply", "Derivative", "Not", "!", "'");

	public Matrix(IExpr matrix) {
		expressions = new ArrayList<List<Expression>>();

		this.height = matrix.size() - 1;
		if (height > 0) {
			this.width = matrix.getAt(1).size() - 1;
		}

		for (int row = 1; row < matrix.size(); row++) {
			List<Expression> rowExpressions = new ArrayList<Expression>();
			for (int column = 1; column < matrix.getAt(row).size(); column++) {
				rowExpressions.add(new Expression(matrix.getAt(row).getAt(column).toString()));
			}
			expressions.add(rowExpressions);
		}
	}

	public Matrix(int width, int height) {
		expressions = new ArrayList<List<Expression>>();

		this.width = width;
		this.height = height;

		for (int i = 0; i < height; i++) {
			List<Expression> rowExpressions = new ArrayList<>();
			for (int j = 0; j < width; j++) {
				rowExpressions.add(new Expression("0"));
			}
			expressions.add(rowExpressions);
		}
	}

	public void put(int row, int column, Expression expression) {
		this.expressions.get(row).set(column, expression);
	}

	public Expression get(int row, int column) {
		return this.expressions.get(row).get(column);
	}

	public Matrix inverse() {
		if (width != height) {
			return null;
		}

		Expression determinant = this.det();
		if (determinant.toString().equals("0")) {
			return null;
		}
		Expression multiplier = determinant.inverse();

		Matrix inverseMatrix = new Matrix(this.width, this.height); // Сори, пока так
		if (this.width == 2) {
			inverseMatrix.put(0, 0, this.get(1, 1).multiply(multiplier));
			inverseMatrix.put(0, 1, this.get(0, 1).negative().multiply(multiplier));
			inverseMatrix.put(1, 0, this.get(1, 0).negative().multiply(multiplier));
			inverseMatrix.put(1, 1, this.get(0, 0).multiply(multiplier));
		}

		return inverseMatrix;
	}

	public Matrix dot(Matrix matrix) {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("Dot(%s, %s)", this, matrix);
		return new Matrix(evaluator.eval(command));
	}

	public Expression det() {
		ExprEvaluator evaluator = new ExprEvaluator();
		String command = String.format("Det(%s)", this);
		return new Expression(evaluator.eval(command).toString());
	}

	public Matrix replaceAll(Variable variable) {
		return evaluateAt(variable, DEFAULT_PRECISION);
	}

	public Matrix evaluateAt(Variable variable, Precision precision) {
		Matrix replacedMatrix = new Matrix(this.width, this.height);
		for (int row = 0; row < this.width; row++) {
			for (int column = 0; column < this.height; column++) {
				replacedMatrix.put(row, column, this.get(row, column).evaluateAt(variable, precision));
			}
		}
		return replacedMatrix;
	}

	// ДЕБИЛЬНЫЕ МЕТОДЫ

	public boolean isBroken() {
		if (this.expressions.stream().anyMatch(expression -> BROKEN_INDICATORS.stream().anyMatch(b -> expression.toString().contains(b)))) {
			return true;
		}
		return false;
	}

	// СТРОКОВЫЕ МЕТОДЫ!

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (int i = 0; i < height; i++) {
			List<Expression> row = expressions.get(i);
			builder.append("{");
			for (int j = 0; j < width; j++) {
				builder.append(row.get(j).toString());
				if (j < row.size() - 1) {
					builder.append(", ");
				}
			}
			builder.append("}");
			if (i < expressions.size() - 1) {
				builder.append(", ");
			}
		}
		builder.append("}");
		return builder.toString();
	}

	public String toBeautifulString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\t");
		for (int i = 0; i < height; i++) {
			List<Expression> row = expressions.get(i);
			for (int j = 0; j < width; j++) {
				builder.append(row.get(j).toString());
				if (j < row.size() - 1) {
					builder.append("\t\t");
				}
			}
			if (i < expressions.size() - 1) {
				builder.append("\n\t");
			}
		}
		return builder.toString();
	}
}