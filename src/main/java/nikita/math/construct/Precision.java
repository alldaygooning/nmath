package nikita.math.construct;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Precision {

	String string;
	BigDecimal precision;
	BigDecimal accuracy;

	// Передавать в формате 0.[0*]1
	public Precision(String string) {
		this.string = string;
		this.setAccuracy(this.string);
		this.setPrecision(this.string);
	}

	private void setAccuracy(String string) {
		int length = getLength(string);
		this.accuracy = BigDecimal.ONE.movePointLeft(length - 1);
	}

	private void setPrecision(String string) {
		int length = getLength(string);
		this.precision = new BigDecimal(length);
	}

	private int getLength(String string) {
		String filtered = string.replace(".", "").replace("-", "");
		return filtered.length();
	}

	public BigDecimal getPrecision() {
		return precision;
	}

	public void setPrecision(BigDecimal precision) {
		this.precision = precision;
	}

	public BigDecimal getAccuracy() {
		return accuracy;
	}

	public String getString() {
		return string;
	}

	public int getNPrecision() {
		if (precision.compareTo(new BigDecimal(17)) < 0) {
			return 17;
		}
		return precision.intValue();
	}

	public MathContext getMathContext() {
		int scale = this.precision.intValue();
		MathContext mc = new MathContext(scale, RoundingMode.HALF_UP);
		return mc;
	}

	public Precision getAdjustedPrecision(int adjustment) {
		Precision adjustedPrecision = new Precision(this.getString());
		adjustedPrecision.setPrecision(this.precision.add(new BigDecimal(adjustment)));
		return adjustedPrecision;
	}

	public String toString() {
		return String.format("Precision up to %s (%s decimal places).", accuracy.toString(), precision.toString());
	}
}
