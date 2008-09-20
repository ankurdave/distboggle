package com.ankurdave.boggle;

public class PopulationClientTester {
	public static void main(String[] args) {
		new PopulationClient("192.168.1.223", 4444, "words.txt", 4, 20, 5, 20)
		        .connect();
	}
}
