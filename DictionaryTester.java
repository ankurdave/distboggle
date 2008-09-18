import java.util.Scanner;

public class DictionaryTester {
	public static void main(String[] args) {
		Dictionary d = new Dictionary();
		d.buildDictionary("words.txt");
		Scanner in = new Scanner(System.in);
		String input = "";
		while (!(input.equals("QUIT"))) {
			System.out.print("Enter a word: ");
			input = in.nextLine();
			System.out.println("\"" + input + "\""
			        + (d.isWord(input) ? " is a word." : " is not a word."));
		}
	}
}
