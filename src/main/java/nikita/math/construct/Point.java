package nikita.math.construct;

import java.math.BigDecimal;

public class Point {
	private BigDecimal x;
	private BigDecimal y;

	public Point(BigDecimal x, BigDecimal y) {
		this.setX(x);
		this.setY(y);
	}

	public BigDecimal getX() {
		return x;
	}

	public void setX(BigDecimal x) {
		this.x = x;
	}

	public BigDecimal getY() {
		return y;
	}

	public void setY(BigDecimal y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return String.format("(%s; %s)", x.toPlainString(), y.toPlainString());
	}
}
