package nikita.math.exception.solver.interpolate;

import nikita.math.solver.interpolate.FunctionInterpolator;

public class FunctionInterpolationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FunctionInterpolationException(FunctionInterpolator interpolator, String reason) {
		super(String.format("Interpolation by '%s' failued due to: %s.", interpolator.getFullName(), reason));
	}
}
