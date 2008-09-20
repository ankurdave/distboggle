package com.ankurdave.boggle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
public class Population {
	private int childrenPerCouple;
	private ArrayList<Board> currentGeneration;
	private Dictionary dict;
	private int generation;
	private int popCap;
	private int sideLength;
	public Population(int sideLength, int startingPopulation,
	        int childrenPerCouple, int popCap, Dictionary dict) {
		assert sideLength > 0;
		assert startingPopulation >= 0;
		assert childrenPerCouple >= 0;
		assert popCap >= startingPopulation;
		assert dict != null;
		// copy params to object fields
		this.sideLength = sideLength;
		this.childrenPerCouple = childrenPerCouple;
		this.popCap = popCap;
		this.dict = dict;
		// make the first generation
		generation = 1;
		currentGeneration = new ArrayList<Board>();
		Board temp;
		for (int i = 0; i < startingPopulation; i++) {
			temp = new Board(Util.randomGrid(sideLength), dict);
			temp.generate();
			currentGeneration.add(temp);
		}
	}
	public void add(Board boggle) {
		assert boggle != null;
		currentGeneration.add(boggle);
	}
	public void add(char[][] grid) {
		assert grid.length == sideLength;
		currentGeneration.add(new Board(grid, dict));
	}
	public int averageScore() throws GenerationEmptyException {
		if (numBoggles() <= 0) { throw new GenerationEmptyException(
		        "not enough Boggles in current generation to find average"); }
		int counter = 0;
		int total = 0;
		for (Board b : currentGeneration) {
			counter++;
			total += b.getScore();
		}
		return total / counter;
	}
	public void evolve() throws GenerationEmptyException {
		if (numBoggles() <= 1) { throw new GenerationEmptyException(
		        "not enough Boggles in current generation to evolve"); }
		// sort the current generation by score
		Collections.sort(currentGeneration);
		// make children
		ArrayList<Board> children = new ArrayList<Board>();
		Board parent1;
		Board parent2;
		Board child;
		for (int i = 0; i < this.numBoggles() - 1; i += 2) {
			// get the next two parents
			parent1 = currentGeneration.get(i);
			parent2 = currentGeneration.get(i + 1);
			// mate them childrenPerCouple times
			for (int j = 0; j < childrenPerCouple; j++) {
				child = parent1.merge(parent2);
				children.add(child);
			}
		}
		// do elitist selection
		// highest() seems to clone the object or something and so age is not
		// preserved
		Board highest = currentGeneration.get(currentGeneration.size() - 1);
		children.add(highest);
		// make sure there are no duplicates, then rank the boggles
		HashSet<String> uniqueGrids = new HashSet<String>();
		for (int i = 0; i < children.size(); i++) {
			Board b = children.get(i);
			if (uniqueGrids.contains(b.gridToString())) {
				children.remove(b);
				continue;
			} else {
				uniqueGrids.add(b.gridToString());
			}
			b.generate();
		}
		Collections.sort(children);
		// make sure number of children <= popCap by removing the worst few
		Collections.sort(children);
		while (children.size() > popCap) {
			children.remove(0);
		}
		// apply changes
		currentGeneration.clear();
		currentGeneration.addAll(children);
		// record generation change
		generation++;
	}
	public ArrayList<Board> getCurrentGeneration() {
		return currentGeneration;
	}
	public int getGeneration() {
		return generation;
	}
	public int getPopCap() {
		return popCap;
	}
	public Board highest() throws GenerationEmptyException {
		if (numBoggles() <= 0) { throw new GenerationEmptyException(
		        "not enough Boggles in current generation to find maximum"); }
		return Collections.max(currentGeneration);
	}
	public Board lowest() throws GenerationEmptyException {
		if (numBoggles() <= 0) { throw new GenerationEmptyException(
		        "not enough Boggles in current generation to find minimum"); }
		return Collections.min(currentGeneration);
	}
	public int numBoggles() {
		return currentGeneration.size();
	}
	public Board random() {
		return currentGeneration.get((int) (Math.random() * numBoggles()));
	}
	public Board removeHighest() throws GenerationEmptyException {
		if (numBoggles() <= 0) { throw new GenerationEmptyException(
		        "not enough Boggles in current generation to find maximum"); }
		Board highest = Collections.max(currentGeneration);
		currentGeneration.remove(highest);
		return highest;
	}
	public void setPopCap(int popCap) {
		this.popCap = popCap;
	}
	@Override public String toString() {
		String s = null;
		try {
			s = generation + " " + highest().getScore() + " " + averageScore()
			        + " " + lowest().getScore();
		}
		catch (GenerationEmptyException e) {
			System.err.println(e);
		}
		return s;
	}
}
