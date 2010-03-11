package com.ankurdave.boggle;

/**
 * Represents a node in a trie. The edges between the nodes represent the letters.
 */
public class TrieNode {
	private boolean endsWord = false;
	private TrieNode[] children;
	/**
	 * Stores any data that should be associated with individual nodes, allowing trie annotation.
	 */
	private int data = 0;
	
	public TrieNode() {
	}
	
	/**
	 * Adds a word to the trie below the current node.
	 * 
	 * @param word the word to add
	 * @param index the character index to start adding from
	 */
	public void addWord(String word, int index) {
		// If the given word is empty, set the endsWord flag
		if (word.length() - index <= 0) {
			this.endsWord = true;
			return;
		}
		
		// Get or create the child at the appropriate index
		int childIndex = charToIndex(word.charAt(index));
		TrieNode child = getChild(childIndex);
		if (child == null) {
			child = new TrieNode();
			setChild(childIndex, child);
		}
		
		// Recursively add the word to that child
		child.addWord(word, index + 1);
	}
	
	/**
	 * Returns whether or not a word ends on this TrieNode.
	 */
	public boolean endsWord() {
		return this.endsWord;
	}
	
	/**
	 * Gets the child with the given letter, or null if it doesn't exist.
	 */
	public TrieNode getChild(char letter) {
		int letterIndex = charToIndex(letter);
		return getChild(letterIndex);
	}
	
	/**
	 * Gets the child at the given index, or null if it doesn't exist.
	 */
	public TrieNode getChild(int index) {
		// Support lazy allocation of children array
		if (children == null) {
			return null;
		}

		return children[index];
	}
	
	/**
	 * Sets the child associated with the given letter to the given TrieNode object.
	 */
	public void setChild(char letter, TrieNode child) {
		int letterIndex = charToIndex(letter);
		setChild(letterIndex, child);
	}
	
	/**
	 * Sets the child at the given index to the given TrieNode object.
	 */
	public void setChild(int index, TrieNode child) {
		// Lazily allocate the children array
		if (children == null) {
			children = new TrieNode[26];
		}

		children[index] = child;
	}
	
	/**
	 * Returns the numeric index associated with the given character.
	 */
	private int charToIndex(char letter) {
		return ((int) Character.toLowerCase(letter)) - 97;
	}
	
	public int getData() {
		return data;
	}
	
	public void setData(int data) {
		this.data = data;
	}
}