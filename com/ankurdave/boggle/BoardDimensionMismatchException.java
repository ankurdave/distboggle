package com.ankurdave.boggle;

/**
 * Exception thrown when an operation (for example, comparison or merging) is requested on two {@link Board}s with different dimensions.
 */
@SuppressWarnings("serial")
public class BoardDimensionMismatchException extends Exception {
	public BoardDimensionMismatchException(String message) {
		super(message);
	}
	
	@Override
	public String toString() {
		return "BoardDimensionMismatchException: " + getMessage();
	}
}
