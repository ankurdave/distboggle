package com.ankurdave.boggle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Stores a list of words in a trie structure.
 */
public class Dictionary {
	private TrieNode root;
	
	public Dictionary() {
		this.root = new TrieNode();
	}
	
	/**
	 * Adds the given word to the {@link Dictionary}.
	 */
	public void add(String word) {
		root.addWord(word, 0);
	}
	
	/**
	 * Reads in newline-separated words from the given file and adds them.
	 */
	public void buildDictionary(String path) {
		// read dictionary file
		try {
			String temp;
			Scanner file = new Scanner(new File(path));
			while (file.hasNextLine()) {
				temp = file.nextLine().toLowerCase();
				this.add(temp);
			}
			// this.optimize();
		} catch (FileNotFoundException e) {
			System.out.println("file " + path + " not found!");
			System.exit(-1);
		}
	}
	
	/**
	 * Gets the root node. Useful for traversing the dictionary in parallel with some other traversal.
	 */
	public TrieNode getRoot() {
		return root;
	}
	
	/**
	 * Checks if the given word exists in the dictionary.
	 */
	public boolean isWord(String word) {
		TrieNode currentNode = getRoot();
		
		// Loop through each character of the word and check if it's a child of the previous one
		for (int i = 0; i < word.length(); i++) {
			currentNode = currentNode.getChild(word.charAt(i));
			if (currentNode == null) {
				return false;
			}
		}
		
		return currentNode.endsWord();
	}
}