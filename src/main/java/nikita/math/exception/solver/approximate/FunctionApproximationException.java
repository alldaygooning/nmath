package nikita.math.exception.solver.approximate;

import nikita.math.solver.approximate.FunctionApproximator;

public class FunctionApproximationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FunctionApproximationException(FunctionApproximator approximator, String reason) {
		super(String.format("Failed to approximate function using '%s' due to: %s.", approximator.getFullName(), reason));
	}

	public FunctionApproximationException(String reason) {
		super(String.format("Failed to approximate function due to: %s.", reason));
	}
}
