package com.ankurdave.boggle;
public class GeneticClientTester {
	public static void main(String[] args) {
		new GeneticClient("192.168.1.123", 4444, "words.txt", 4, 20, 5, 20)
		        .run();
	}
}
