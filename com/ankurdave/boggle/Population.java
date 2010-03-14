package com.ankurdave.boggle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Represents a population of {@link Board}s in the context of a genetic algorithm.
 */
public class Population {
	private ArrayList<GeneticBoard> currentGeneration;
	private Dictionary dict;
	private int generation = 0;
	private int popCap = 20;
	private int sideLength = 4;
	private int startingPopulation = 20;
	private int childrenPerCouple = 5;
	private ArrayList<GeneticBoard> incomingBoards = new ArrayList<GeneticBoard>();
	
	public Population() {}
	
	public void evolve() throws GenerationEmptyException { /* @ \label{Population.java:evolve} @ */
		if (generation == 0) {
			makeFirstGeneration();
		}
		
		if (currentGeneration.size() <= 1) {
			throw new GenerationEmptyException("Current generation size " + currentGeneration.size() + " too small to evolve");
		}
		
		// Record the new generation
		generation++;
		
		// Add the incoming boards to the current generation
		// Don't allow Population#add(GeneticBoard) to add more boards at the same time
		synchronized (incomingBoards) {
			currentGeneration.addAll(incomingBoards);
			incomingBoards.clear();
		}
		
		// Pair up the boards by score and make children
		Collections.sort(currentGeneration);
		ArrayList<GeneticBoard> children = new ArrayList<GeneticBoard>();
		GeneticBoard parent1;
		GeneticBoard parent2;
		GeneticBoard child;
		for (int i = 0; i < currentGeneration.size() - 1; i += 2) {
			// Get the next two parents
			parent1 = currentGeneration.get(i);
			parent2 = currentGeneration.get(i + 1);
			
			// Mate them childrenPerCouple times
			for (int j = 0; j < childrenPerCouple; j++) {
				child = parent1.merge(parent2);
				children.add(child);
			}
		}
		
		// Do elitist selection -- preserve the highest-scoring Board into the next generation
		GeneticBoard highest = currentGeneration.get(currentGeneration.size() - 1);
		children.add(highest);
		
		// Copy the unique children into childrenUnique /*@ \label{Population.java:unique} @*/
		HashSet<String> uniqueGridStrings = new HashSet<String>();
		ArrayList<GeneticBoard> childrenUnique = new ArrayList<GeneticBoard>();
		for (GeneticBoard b : children) {
			if (!uniqueGridStrings.contains(b.gridToString())) {
				childrenUnique.add(b);
				uniqueGridStrings.add(b.gridToString());
				
				// Score each unique Board
				b.generate();
			}
		}
		
		// If the list is too big, truncate it, keeping only the top popCap Boards
		Collections.sort(childrenUnique);
		if (childrenUnique.size() > popCap) {
			childrenUnique.subList(0, childrenUnique.size() - popCap).clear();
		}
		
		currentGeneration.clear();
		currentGeneration.addAll(childrenUnique);
	}
	
	public void add(GeneticBoard board) {
		// Make sure the Board uses the standard dictionary, so that when GeneticBoard#merge(GeneticBoard) gets called, it sets the right dictionary
		board.setDictionary(dict);
		
		// Add it to the queue
		// Don't allow Population#evolve() to remove the boards at the same time
		synchronized (incomingBoards) {
			incomingBoards.add(board);
		}
	}
	
	public ArrayList<GeneticBoard> getCurrentGeneration() {
		return currentGeneration;
	}
	
	public void setDictionary(Dictionary dict) {
		this.dict = dict;
	}
	
	public void setSideLength(int sideLength) {
		this.sideLength = sideLength;
	}
	
	public void setStartingPopulation(int startingPopulation) {
		this.startingPopulation = startingPopulation;
	}
	
	public void setChildrenPerCouple(int childrenPerCouple) {
		this.childrenPerCouple = childrenPerCouple;
	}
	
	public void setPopCap(int popCap) {
		this.popCap = popCap;
	}
	
	private void makeFirstGeneration() {
		generation = 0;
		currentGeneration = new ArrayList<GeneticBoard>(startingPopulation);
		GeneticBoard temp;
		for (int i = 0; i < startingPopulation; i++) {
			temp = new GeneticBoard(Util.randomGrid(sideLength));
			temp.setDictionary(dict);
			temp.generate();
			currentGeneration.add(temp);
		}
	}
	
	private int getAverageScore() {
		int counter = 0;
		int total = 0;
		for (GeneticBoard b : currentGeneration) {
			counter++;
			total += b.getScore();
		}
		return total / counter;
	}
	
	private GeneticBoard getHighestBoard() {
		Collections.sort(currentGeneration);
		return currentGeneration.get(currentGeneration.size() - 1);
	}
	
	private GeneticBoard getLowestBoard() {
		Collections.sort(currentGeneration);
		return currentGeneration.get(0);
	}
	
	@Override
	public String toString() {
		return generation + " " + getHighestBoard().getScore() + " " + getAverageScore() + " " + getLowestBoard().getScore();
	}
}
