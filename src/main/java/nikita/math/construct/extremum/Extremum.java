package nikita.math.construct.extremum;

import java.math.BigDecimal;

import nikita.math.construct.Point;

public class Extremum {

	private Point point;

	public Extremum(BigDecimal x, BigDecimal y) {
		this.point = new Point(x, y);
	}

	public BigDecimal getX() {
		return this.point.getX();
	}

	public void setX(BigDecimal x) {
		this.point.setX(x);
	}

	public BigDecimal getY() {
		return this.point.getY();
	}

	public void setY(BigDecimal y) {
		this.point.setY(y);
	}
}
