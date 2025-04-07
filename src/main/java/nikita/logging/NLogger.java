package nikita.logging;

public class NLogger {
	public static void info(String message) {
		System.out.println(message);
	}

	public static void error(String message) {
		System.out.println("Error: " + message);
	}
}
