package com.ankurdave.boggle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * Runs various tests on Dictionary.
 */
public class DictionaryTester {
	public static void main(String[] args) {
		// Create the dictionary
		String dictPath = args[0];
		Dictionary d = new Dictionary();
		System.out.print("Generating...");
		d.buildDictionary(dictPath);
		System.out.println("done.");
		
		// Read the words from the file into a different data structure
		HashSet<String> words = new HashSet<String>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(dictPath));
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
			if (!d.isWord(word)) {
				System.err.println("Word test failed for " + word);
			}
		}
		System.out.println("done.");
	}
}
