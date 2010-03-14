package com.ankurdave.boggle;

/**
 * Exception thrown when there are insufficient {@link Board}s in the current generation to evolve to the next generation.
 */
@SuppressWarnings("serial")
public class GenerationEmptyException extends Exception {
	public GenerationEmptyException(String message) {
		super(message);
	}
	
	@Override
	public String toString() {
		return "GenerationEmptyException: " + getMessage();
	}
}
