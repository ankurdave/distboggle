package com.ankurdave.boggle;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
public class Dictionary {
	protected ArrayList<Letter> children;
	public Dictionary() {
		this.children = new ArrayList<Letter>();
	}
	public void add(String word) {
		if (word.length() <= 0) { return; }
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
	public boolean beginsWord(String word) { /*@ \label{Dictionary.java:beginsWord} @*/
		int index = Collections.binarySearch(this.children, new Letter(word
		        .charAt(0)));
		// return false if child matching the first char of word does not exist
		if (index < 0) { return false; }
		// otherwise, check base case
		if (word.length() == 1) { return true; }
		// otherwise, traverse recursively
		return children.get(index).beginsWord(word.substring(1));
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
		}
		catch (FileNotFoundException e) {
			System.out.println("file " + path + " not found!");
			System.exit(-1);
		}
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
	@Override public String toString() {
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

class Letter extends Dictionary implements Comparable<Letter> {
	private char data;
	private boolean endsWord = false;
	public Letter(char data) {
		super();
		this.data = Character.toLowerCase(data);
	}
	@Override public void add(String word) {
		if (word.length() == 0) {
			this.endsWord = true;
			return;
		}
		if (word.length() <= 0) { return; }
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
	public char getData() {
		return this.data;
	}
	public boolean getEndsWord() {
		return this.endsWord;
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
