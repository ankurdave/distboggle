public class BoggleClientTester {
	public static void main(String[] args) {
		new BoggleClient("localhost", 4444, "words.txt", 4).connect();
	}
}
