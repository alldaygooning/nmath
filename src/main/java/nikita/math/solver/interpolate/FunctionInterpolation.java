package nikita.math.solver.interpolate;

import nikita.math.construct.expression.Expression;

public class FunctionInterpolation {
	private Expression interpolated;

	public FunctionInterpolation(Expression interpolated) {
		this.setInterpolated(interpolated);
	}

	public Expression getInterpolated() {
		return interpolated;
	}

	public void setInterpolated(Expression interpolated) {
		this.interpolated = interpolated;
	}
}
