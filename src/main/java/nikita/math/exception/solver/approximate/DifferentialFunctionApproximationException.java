package nikita.math.exception.solver.approximate;

import nikita.math.solver.approximate.differential.DifferentialFunctionApproximator;

public class DifferentialFunctionApproximationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DifferentialFunctionApproximationException(DifferentialFunctionApproximator approximator, String reason) {
		super(String.format("Failed to approximate differential function using '%s' due to: %s.", approximator.getFullName(), reason));
	}

	public DifferentialFunctionApproximationException(String reason) {
		super(String.format("Failed to approximate differential function using due to: %s.", reason));
	}
}
