public class BoggleUtil {
	/**
     * Creates a character grid filled with random uppercase letters.
     * @param sideLength
     *            length of one side of the random grid
     * @return the random grid
     */
	public static char[][] randomGrid(int sideLength) {
		char[][] temp = new char[sideLength][sideLength];
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				// rand 65-90
				temp[i][j] = (char) (Math.random() * (90 - 65 + 1) + 65);
			}
		}
		return temp;
	}
}
