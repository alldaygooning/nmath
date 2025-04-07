package nikita.math.construct.extremum;

import java.math.BigDecimal;

public class Minimum extends Extremum {

	public Minimum(BigDecimal x, BigDecimal y) {
		super(x, y);
	}

	@Override
	public String toString() {
		return String.format("Minimum: (%s; %s)", this.getX().toPlainString(), this.getY().toPlainString());
	}
}
