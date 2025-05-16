package nikita.math.solver.interpolate;

import java.math.BigDecimal;

public class FunctionInterpolationContext {
	private BigDecimal x;

	public FunctionInterpolationContext(BigDecimal x) {
		this.setX(x);
	}

	public BigDecimal getX() {
		return x;
	}

	public void setX(BigDecimal x) {
		this.x = x;
	}
}
