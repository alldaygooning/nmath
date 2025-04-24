package nikita.math.construct.point.extremum;

import java.math.BigDecimal;

public class Maximum extends Extremum {

	public Maximum(BigDecimal x, BigDecimal y) {
		super(x, y);
	}

	@Override
	public String toString() {
		return String.format("Maximum: (%s; %s)", this.getX().toPlainString(), this.getY().toPlainString());
	}
}
