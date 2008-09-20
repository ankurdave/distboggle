package com.ankurdave.boggle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
public class BogglePopulation {
	private int childrenPerCouple;
	private ArrayList<Boggle> currentGeneration;
	private Dictionary dict;
	private int generation;
	private int popCap;
	private int sideLength;
	public BogglePopulation(int sideLength, int startingPopulation,
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
		currentGeneration = new ArrayList<Boggle>();
		Boggle temp;
		for (int i = 0; i < startingPopulation; i++) {
			temp = new Boggle(BoggleUtil.randomGrid(sideLength), dict);
			temp.generate();
			currentGeneration.add(temp);
		}
	}
	public void add(Boggle boggle) {
		assert boggle != null;
		currentGeneration.add(boggle);
	}
	public void add(char[][] grid) {
		assert grid.length == sideLength;
		currentGeneration.add(new Boggle(grid, dict));
	}
	public int averageScore() throws GenerationEmptyException {
		if (numBoggles() <= 0) { throw new GenerationEmptyException(
		        "not enough Boggles in current generation to find average"); }
		int counter = 0;
		int total = 0;
		for (Boggle b : currentGeneration) {
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
		ArrayList<Boggle> children = new ArrayList<Boggle>();
		Boggle parent1;
		Boggle parent2;
		Boggle child;
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
		Boggle highest = currentGeneration.get(currentGeneration.size() - 1);
		children.add(highest);
		// make sure there are no duplicates, then rank the boggles
		HashSet<String> uniqueGrids = new HashSet<String>();
		for (int i = 0; i < children.size(); i++) {
			Boggle b = children.get(i);
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
	public ArrayList<Boggle> getCurrentGeneration() {
		return currentGeneration;
	}
	public int getGeneration() {
		return generation;
	}
	public int getPopCap() {
		return popCap;
	}
	public Boggle highest() throws GenerationEmptyException {
		if (numBoggles() <= 0) { throw new GenerationEmptyException(
		        "not enough Boggles in current generation to find maximum"); }
		return Collections.max(currentGeneration);
	}
	public Boggle lowest() throws GenerationEmptyException {
		if (numBoggles() <= 0) { throw new GenerationEmptyException(
		        "not enough Boggles in current generation to find minimum"); }
		return Collections.min(currentGeneration);
	}
	public int numBoggles() {
		return currentGeneration.size();
	}
	public Boggle random() {
		return currentGeneration.get((int) (Math.random() * numBoggles()));
	}
	public Boggle removeHighest() throws GenerationEmptyException {
		if (numBoggles() <= 0) { throw new GenerationEmptyException(
		        "not enough Boggles in current generation to find maximum"); }
		Boggle highest = Collections.max(currentGeneration);
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
