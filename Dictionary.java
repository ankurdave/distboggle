package com.ankurdave.distboggle;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Holds a dictionary of valid words.
 * @author ankur
 */
public class Dictionary {
	/**
     * List of children of the top node.
     */
	protected ArrayList<Letter> children;
	
	/**
     * Default constructor for Dictionary.
     */
	public Dictionary() {
		this.children = new ArrayList<Letter>();
	}
	
	/**
     * Adds a <CODE>String</CODE> to the dictionary.
     * @param word
     *            <CODE>String</CODE> to add
     */
	public void add(String word) {
		if (word.length() <= 0) {
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
	
	/**
     * Looks up a <CODE>String</CODE> in the dictionary, checking if it begins
     * a word.
     * @return whether or not <CODE>word</CODE> begins a word in the
     *         dictionary
     * @param word
     *            <CODE>String</CODE> to check
     */
	public boolean beginsWord(String word) {
		int index = Collections.binarySearch(this.children, new Letter(word
		        .charAt(0)));
		// return false if child matching the first char of word does not exist
		if (index < 0) {
			return false;
		}
		// otherwise, check base case
		if (word.length() == 1) {
			return true;
		}
		// otherwise, traverse recursively
		return children.get(index).beginsWord(word.substring(1));
	}
	
	/**
     * Fills the dictionary with words from a file.
     * @param path
     *            location of the newline-separated dictionary file
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
		}
		catch (FileNotFoundException e) {
			System.out.println("file " + path + " not found!");
			System.exit(-1);
		}
	}
	
	/**
     * Looks up a <CODE>String</CODE> in the dictionary.
     * @return whether or not <CODE>word</CODE> is in the dictionary
     * @param word
     *            <CODE>String</CODE> to check
     */
	public boolean isWord(String word) {
		int index = Collections.binarySearch(this.children, new Letter(word
		        .charAt(0)));
		// return false if child matching the first char of word does not exist
		if (index < 0) {
			return false;
		}
		// otherwise, check base case
		if (word.length() == 1) {
			return children.get(index).getEndsWord();
		}
		// otherwise, traverse recursively
		return children.get(index).isWord(word.substring(1));
	}
	
	/**
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		String s = "Dictionary[]\nchildren=";
		for (Letter a : this.children) {
			if (a == null) {
				continue;
			}
			s += "\n" + a;
		}
		return s;
	}
}

/**
 * Represents each letter in the <CODE>Dictionary</CODE>.
 * @author Ankur Dave
 */
class Letter extends Dictionary implements Comparable<Letter> {
	/**
     * Character that this Letter object represents.
     */
	private char data;
	
	/**
     * Whether this Letter ends a word in the dictionary.
     */
	private boolean endsWord = false;
	
	/**
     * @param data
     *            character that this object represents
     */
	public Letter(char data) {
		super();
		this.data = Character.toLowerCase(data);
	}
	
	/**
     * @see Dictionary#add(java.lang.String)
     */
	@Override
	public void add(String word) {
		if (word.length() == 0) {
			this.endsWord = true;
			return;
		}
		if (word.length() <= 0) {
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
	
	public int compareTo(Letter that) {
		if (this.getData() > that.getData()) {
			return 1;
		} else if (this.getData() < that.getData()) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
     * @return character that this object represents
     */
	public char getData() {
		return this.data;
	}
	
	/**
     * @return whether this Letter ends a word in the dictionary.
     */
	public boolean getEndsWord() {
		return this.endsWord;
	}
	
	/**
     * @see Dictionary#toString()
     */
	@Override
	public String toString() {
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
