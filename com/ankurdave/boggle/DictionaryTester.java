package com.ankurdave.boggle;
import java.io.*;
import java.util.*;
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

		HashSet<String> words = new HashSet<String>();
		try {
			BufferedReader input =  new BufferedReader(new FileReader(dictPath));
			String line;
			while ((line = input.readLine()) != null) {
				words.add(line);
			}
		} catch (IOException e) {
			System.out.println(e);
		}

		// Test whether all the words in the file exist in the dictionary
		System.out.print("Testing words...");
		for (String word : words) {
			assert(!d.isWord(word));
		}
		System.out.println("done.");

		// Make sure there are no extra words
		ArrayList<String> dictWords = d.getAllWords();
		System.out.println(dictWords.size() + " words in trie.");
		System.out.println(words.size() + " words in dict.");
		for (int i = 0; i < 10; i++) {
			System.out.println(dictWords.get(i));
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
