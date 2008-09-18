package com.ankurdave.distboggle;
import java.util.Comparator;

public class ByStringLength implements Comparator<String> {
	/**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
	public int compare(String s1, String s2) {
		if (s1.length() > s2.length()) {
			return 1;
		} else if (s1.length() < s2.length()) {
			return -1;
		} else {
			return s1.compareTo(s2);
		}
	}
}
