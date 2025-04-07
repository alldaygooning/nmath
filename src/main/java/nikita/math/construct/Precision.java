package nikita.math.construct;

import java.math.BigDecimal;

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

	public String toString() {
		return String.format("Precision up to %s (%s decimal places).", accuracy.toString(), precision.toString());
	}

	public int getNPrecision() {
		if (precision.compareTo(new BigDecimal(17)) < 0) {
			return 17;
		}
		return precision.intValue();
	}
}
