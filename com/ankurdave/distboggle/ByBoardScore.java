package com.ankurdave.distboggle;
import java.util.Comparator;

public class ByBoardScore implements Comparator<String> {
	public int compare(String s1, String s2) {
		int score1 = Integer.parseInt(s1.split(" ")[2]);
		int score2 = Integer.parseInt(s2.split(" ")[2]);
		return score2 - score1;
	}
}
