/**
 * Exception thrown when there are insufficient Boggles in the current
 * generation to evolve to the next generation.
 * @author ankur
 */
public class GenerationEmptyException extends Exception {
	public static final long serialVersionUID = 0;

	private String message;

	public GenerationEmptyException(String message) {
		this.message = message;
	}

	public String toString() {
		return "GenerationEmptyException: " + message;
	}
}
