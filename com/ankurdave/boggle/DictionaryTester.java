package com.ankurdave.boggle;
import java.io.*;
public class DictionaryTester {
	public static void main(String[] args) {
		String dictPath = args[0];

		Dictionary d = new Dictionary();		
		System.out.print("Generating...");
		d.buildDictionary(dictPath);
		System.out.println("done.");
		System.out.println(d.numNodes() + " nodes in dictionary.");
		
		System.out.print("Optimizing...");
		d.optimize();
		System.out.println("done.");
		System.out.println(d.numNodes() + " nodes in dictionary.");

		try {
			BufferedReader input =  new BufferedReader(new FileReader(dictPath));
			String line;
			System.out.print("Testing words...");
			while ((line = input.readLine()) != null) {
				assert(!d.isWord(line));
			}
			System.out.println("done.");
		} catch (IOException e) {
			System.out.println(e);
		}

		/*Scanner in = new Scanner(System.in);
		String input = "";
		while (!(input.equals("QUIT"))) {
			System.out.print("Enter a word: ");
			input = in.nextLine();
			System.out.println("\"" + input + "\""
			        + (d.isWord(input) ? " is a word." : " is not a word."));
		}*/
	}
}
