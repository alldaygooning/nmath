package nikita.math.exception.construct.expression;

import nikita.math.construct.equation.Equation;

public class EquationEvaluationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EquationEvaluationException(Equation equation) {
		super(String.format("Unable to solve Equation '%s'", equation));
	}

	public EquationEvaluationException(Equation equation, String reason) {
		super(String.format("Unable to solve Equation '%s' due to: %s", equation, reason));
	}
}
