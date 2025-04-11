package nikita.math.exception.construct;

public class NMathException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	static final String EMAIL_CONTACT_INFORMATION = "munchman@gmail.com";
	static final String PHONE_NUMBER_CONTACT_INFORMATION = "(202) 622-2000";

	public NMathException(String message) {
		super(String.format("%s\nNeed help? Contact us:\nEmail: %s\tPhone:%s", message, EMAIL_CONTACT_INFORMATION,
				PHONE_NUMBER_CONTACT_INFORMATION));
	}
}
