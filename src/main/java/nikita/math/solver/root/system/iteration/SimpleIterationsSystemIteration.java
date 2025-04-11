package nikita.math.solver.root.system.iteration;

import java.math.BigDecimal;

public class SimpleIterationsSystemIteration extends Iteration {

	static final String TABLE_TEMPLATE = "%-15s | %-15s | %-15s | %-15s | %-25s | %-25s";
	static final String TABLE_HEADER = String.format(TABLE_TEMPLATE, "x_i", "y_i", "x_i+1", "y_i+1", "|x_i - x_i+1|",
			"|y_i - y_i+1|");

	BigDecimal xi;
	BigDecimal yi;
	BigDecimal xnew;
	BigDecimal ynew;
	BigDecimal xDiff;
	BigDecimal yDiff;

	public SimpleIterationsSystemIteration(int number, BigDecimal xi, BigDecimal yi, BigDecimal xnew, BigDecimal ynew, BigDecimal xDiff,
			BigDecimal yDiff) {
		super(number);

		this.xi = xi;
		this.yi = yi;
		this.xnew = xnew;
		this.ynew = ynew;
		this.xDiff = xDiff;
		this.yDiff = yDiff;
	}

	public BigDecimal getXi() {
		return xi;
	}

	public void setXi(BigDecimal xi) {
		this.xi = xi;
	}

	public BigDecimal getYi() {
		return yi;
	}

	public void setYi(BigDecimal yi) {
		this.yi = yi;
	}

	public BigDecimal getXnew() {
		return xnew;
	}

	public void setXnew(BigDecimal xnew) {
		this.xnew = xnew;
	}

	public BigDecimal getYnew() {
		return ynew;
	}

	public void setYnew(BigDecimal ynew) {
		this.ynew = ynew;
	}

	public BigDecimal getxDiff() {
		return xDiff;
	}

	public void setxDiff(BigDecimal xDiff) {
		this.xDiff = xDiff;
	}

	public BigDecimal getyDiff() {
		return yDiff;
	}

	public void setyDiff(BigDecimal yDiff) {
		this.yDiff = yDiff;
	}

	@Override
	public String toString() {
		return String.format(TABLE_TEMPLATE, getNumber(), xi, yi, xnew, ynew, xDiff, yDiff);
	}
}
