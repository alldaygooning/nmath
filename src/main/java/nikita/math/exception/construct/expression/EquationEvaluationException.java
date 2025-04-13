package nikita.math.exception.construct.expression;

import nikita.math.construct.expression.Equation;

public class EquationEvaluationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EquationEvaluationException(Equation equation) {
		super(String.format("Unable to solve Equation '%s' for Variable '%s'.", equation, equation.getVariable()));
	}
}
