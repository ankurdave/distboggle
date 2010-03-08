package com.ankurdave.boggle;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
public class Dictionary {
	private Letter root;
	public Dictionary() {
		this.root = new Letter(' ');
	}
	public void add(String word) {
		root.add(word);
	}
	public boolean beginsWord(String word) { /*@ \label{Dictionary.java:beginsWord} @*/
		return root.beginsWord(word);
	}
	public void buildDictionary(String path) {
		// read dictionary file
		try {
			String temp;
			Scanner file = new Scanner(new File(path));
			while (file.hasNextLine()) {
				temp = file.nextLine().toLowerCase();
				this.add(temp);
			}
			this.optimize();
		}
		catch (FileNotFoundException e) {
			System.out.println("file " + path + " not found!");
			System.exit(-1);
		}
	}
	public boolean isWord(String word) {
		return root.isWord(word);
	}
	/**
	 * Gets the root node. Useful for traversing the dictionary in parallel
	 * with some other traversal.
	 */
	public Letter getRoot() {
		return root;
	}
	/**
	 * Performs various optimizations to shrink a dictionary and optimize it
	 * for speed once it has been fully built.
	 */
	public void optimize() {
		root.optimize();
		System.gc();
	}
	@Override public String toString() {
		return "Dictionary[]";
	}
	
	class Letter implements Comparable<Letter> {
		private char data;
		private boolean endsWord = false;
		private ArrayList<Letter> children;
		public Letter(char data) {
			this.children = new ArrayList<Letter>();
			this.data = Character.toLowerCase(data);
		}
		public void add(String word) {
			if (word.length() == 0) {
				this.endsWord = true;
				return;
			}
			for (Letter a : this.children) {
				if (a == null) {
					continue;
				}
				if (a.getData() == word.charAt(0)) {
					a.add(word.substring(1));
					return;
				}
			}
			Letter child = new Letter(word.charAt(0));
			this.children.add(child);
			child.add(word.substring(1));
			Collections.sort(this.children);
		}
		public boolean beginsWord(String word) {
			int index = Collections.binarySearch(this.children, new Letter(word
			        .charAt(0)));
			// return false if child matching the first char of word does not exist
			if (index < 0) { return false; }
			// otherwise, check base case
			if (word.length() == 1) { return true; }
			// otherwise, traverse recursively
			return children.get(index).beginsWord(word.substring(1));
		}
		public boolean isWord(String word) {
			int index = Collections.binarySearch(this.children, new Letter(word
			        .charAt(0)));
			// return false if child matching the first char of word does not exist
			if (index < 0) { return false; }
			// otherwise, check base case
			if (word.length() == 1) { return children.get(index).getEndsWord(); }
			// otherwise, traverse recursively
			return children.get(index).isWord(word.substring(1));
		}
		public int compareTo(Letter that) {
			if (this.getData() > that.getData()) {
				return 1;
			} else if (this.getData() < that.getData()) {
				return -1;
			} else {
				return 0;
			}
		}
		public char getData() {
			return this.data;
		}
		public boolean getEndsWord() {
			return this.endsWord;
		}
		/**
		 * Optimizes this Letter and all its children for space and speed.
		 */
		public void optimize() {
			children.trimToSize();
			for (Letter l : children) {
				l.optimize();
			}
		}
		/**
		 * Gets the child with the given letter, or null if it doesn't exist.
		 * Useful for traversing in parallel with some other traversal.
		 */
		public Letter getChild(char childLetter) {
			int index = Collections.binarySearch(this.children, new Letter(childLetter));
			if (index < 0) {
				return null;
			} else {
				return children.get(index);
			}
		}
		@Override public String toString() {
			String s = "Letter[data=" + this.data + "; endsWord=" + this.endsWord
			        + "]\nchildren=";
			for (Letter a : this.children) {
				if (a == null) {
					continue;
				}
				s += "\n" + a;
			}
			return s;
		}
	}

}