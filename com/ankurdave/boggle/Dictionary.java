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
//			this.optimize();
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
		compressSuffixes();
		root.optimize();
		
		System.gc();
	}
	@Override public String toString() {
		return "Dictionary[]";
	}
	/**
	 * Converts a trie (redundant suffix information) into the equivalent DAWG, to save space.
	 */
	private void compressSuffixes() {
		root.compressSuffixes();
	}
	/**
	 * Counts the number of unique nodes (Letters) in the trie.
	 */
	public int numNodes() {
		ArrayList<Letter> allNodes = root.getAllNodes();
		
		// Return the number of unique nodes
		return allNodes.size();
	}

	public ArrayList<String> getAllWords() {
		ArrayList<String> words = new ArrayList<String>();
		root.accumulateWords(words, "");
		return words;
	}
	
	class Letter implements Comparable<Letter> {
		private char data;
		private boolean endsWord = false;
		private ArrayList<Letter> children;
		private boolean copy = false;
		public Letter(char data) {
			this.children = new ArrayList<Letter>();
			this.data = Character.toLowerCase(data);
		}
		public void compressSuffixes() {
			// Generate the hashes of each child's subtree to avoid redundant comparisons
			int[] childrenHashes = new int[children.size()];
			for (int i = 0; i < children.size(); i++) {
				childrenHashes[i] = children.get(i).subtreeHashCode();
			}
			
			// For each pair of children, merge their children if hash codes are the same and a deep equality test agrees
			// TODO: this is O(n^2). It could be O(n log n) if we sorted the array. Not too big a deal because n <= 26.
			for (int i = 0; i < children.size(); i++) {
				for (int j = i + 1; j < children.size(); j++) {
					if (childrenHashes[i] == childrenHashes[j] && children.get(i).subtreeEquals(children.get(j))) {
						children.get(i).mergeChildren(children.get(j));
						//System.out.println("Saved -" + children.get(i).getLongestWord());
					}
				}
			}
			
			// Recurse
			for (Letter child : children) {
				child.compressSuffixes();
			}
		}
		/**
		 * Copies the children of the given Letter over the children of this Letter. Useful for compressing the tree.
		 * Warning: it's a shallow copy, so modifying the children of the given Letter will affect this Letter, and vice versa. So don't call this unless no more changes will be made to the tree.
		 */
		private void mergeChildren(Letter letter) {
			this.children = letter.children;
			this.copy = true;
		}
		public boolean isCopy() {
			return copy;
		}
		private String getLongestWord() {
			if (this.children.size() == 0) {
				return Character.toString(this.data);
			}
			
			// Get the one with maximum length
			String longestWord = "";
			for (Letter child : children) {
				String word = child.getLongestWord();
				if (word.length() > longestWord.length()) {
					longestWord = word;
				}
			}

			return Character.toString(this.data) + longestWord;
		}

		public void accumulateWords(ArrayList<String> words, String currentWord) {
			if (this.endsWord) {
				words.add(currentWord + this.data);
			}

			for (Letter child : children) {
				child.accumulateWords(words, currentWord + this.data);
			}
		}
			
		/**
		 * Recursively checks if the subtrees of this and the given Letters (not including the Letters themselves) store the same characters, have the same connections, and have the same word ending markers.
		 * @param letter
		 * @return
		 */
		private boolean subtreeEquals(Letter that) {
			// The number of children has to be the same
			if (this.children.size() != that.children.size()) {
				return false;
			}
			
			// Iterate over the children in parallel and check for Letter and subtree equality
			for (int i = 0; i < this.children.size(); i++) {
				if (!this.children.get(i).equals(that.children.get(i)) || !this.children.get(i).subtreeEquals(that.children.get(i))) {
					return false;
				}
			}
			
			return true;
		}
		
		/**
		 * Generates a hash code for the subtree of this Letter.
		 */
		private int subtreeHashCode() {
			int hash = 0;
			for (Letter child : children) {
				hash ^= child.hashCode();
				hash ^= child.subtreeHashCode();
			}
			return hash;
		}
		
		/**
		 * Checks whether or not the given Letter and this Letter are directly equal. Does not recurse (see subtreeEquals(Letter) if you want that).
		 */
		@Override
		public boolean equals(Object obj) {
			Letter that = (Letter) obj;
			return this.data == that.data && this.endsWord == that.endsWord;
		}
		
		/**
		 * Generates a hash code for this Letter only. Does not recurse.
		 */
		@Override
		public int hashCode() {
			return (int)data ^ (endsWord ? 1 : 0);
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
			// base case
			if (word.length() == 1) { return true; }

			Letter match = null;
			for (Letter child : children) {
				if (word.charAt(0) == child.getData()) {
					match = child;
					break;
				}
			}
			
			if (match == null) {
				return false;
			}
			
			// traverse recursively
			return match.beginsWord(word.substring(1));
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
		/**
		 * Returns a flattened, deduplicated list of all the nodes in the the tree, including the current one.
		 */
		public ArrayList<Letter> getAllNodes() {
			if (isCopy()) {
				return new ArrayList<Letter>(0);
			}
			
			ArrayList<Letter> allNodes = new ArrayList<Letter>();

			allNodes.add(this);
			for (Letter child : children) {
				allNodes.addAll(child.getAllNodes());
			}

			return allNodes;
		}
	}

}