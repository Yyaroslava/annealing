package org.yasya;

import java.awt.Color;
import java.util.Arrays;

import org.yasya.Annealing.MarkovChain;

public class SolutionHybrid implements Annealing.MarkovChain {
	public Tile[] tiles;
	public static Color[] colors;

	public SolutionHybrid(Tile[] tiles) {
		this.tiles = tiles;
	}

	public static void initColors(SolutionHybrid solution) {
		colors = new Color[solution.tiles.length];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = new Color(100 + App.random.nextInt(50), 100 + App.random.nextInt(50), 100 + App.random.nextInt(50));
		}
	}

	public int intersect(int[][] area, Tile tile, int deltaX, int deltaY) {
		int result = 0;
		for(int y = 0; y < tile.height; y++) {
			for(int x = 0; x < tile.width; x++) { 
				if(tile.area[x][y] > 0 && area[x + deltaX][y + deltaY] > 0) result++;
			}
		}
		return result;
	}

	@Override
	public int score() {
		int[][] area = greedy(false);
		int score = 0;
		for(int y = 0; y < Constants.BOARD_HEIGHT; y++) {
			for(int x = 0; x < Constants.BOARD_WIDTH; x++) {
				if(area[x][y] == 0) score++;
			}
		}
		return score;
	}

	public int[][] greedy(boolean makeColor) {
		int[][] area = new int[Constants.BOARD_WIDTH][Constants.BOARD_HEIGHT];
		for(int i = 0; i < tiles.length; i++) {
			Tile tile = tiles[i];
			int bestX = -1;
			int bestY = -1;
			int bestIntersect = 999;
			positionLoop:
			for(int y = 0; y <= Constants.BOARD_HEIGHT - tile.height; y++) {
				for(int x = 0; x <= Constants.BOARD_WIDTH - tile.width; x++) {
					int currentIntersect = intersect(area, tile, x, y);
					if(currentIntersect == 0) {
						bestX = x;
						bestY = y;
						bestIntersect = 0;
						break positionLoop;
					}
					if(currentIntersect < bestIntersect) {
						bestX = x;
						bestY = y;
						bestIntersect = currentIntersect;
					}
				}
			}
			if(makeColor) {
				tile.stampColor(area, bestX, bestY, i + 1);
			}
			else {
				tile.stamp(area, bestX, bestY);
			}
		}
		return area;
	}

	@Override
	public MarkovChain next() {
		SolutionHybrid s = copy();
		int i1;
		int i2;
		do {
			i1 = App.random.nextInt(s.tiles.length);
			i2 = App.random.nextInt(s.tiles.length);
		} while(i1 == i2);
		Tile t = s.tiles[i1];
		s.tiles[i1] = s.tiles[i2];
		s.tiles[i2] = t;

		return s;
	}

	public static SolutionHybrid startSolution() {
		Tile[] tiles = Tile.randomSmash();
		SolutionHybrid solution = new SolutionHybrid(tiles);
		return solution;
	}

	public SolutionHybrid copy() {
		Tile[] tilesCopy = Arrays.stream(tiles)
			.map(obj -> ((Tile)obj).copy())
			.toArray(Tile[]::new);
		SolutionHybrid s = new SolutionHybrid(tilesCopy);
		return s;
	}

	public static void algorythmHybrid() {
		SolutionHybrid s = SolutionHybrid.startSolution();
		var witness = new Annealing.FireWitness() {

			@Override
			public void afterStart(Annealing.MarkovChain s) {
				initColors((SolutionHybrid)s);
			}

			@Override
			public void afterNewSolution(Annealing.MarkovChain s) {}

			@Override
			public void beforeFinish(Annealing.MarkovChain last, Annealing.MarkovChain best) {
				int[][] area = ((SolutionHybrid)best).greedy(true);
				PNGMaker.make(area, 20, "bestAnnealingGreedy.png", colors);
			}	
		};
		int score = Annealing.fire(s, witness, 0.5, 500000);
		System.out.printf("score: %d \n", score);
	}
	
}
