package org.yasya;

import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Stream;
import org.yasya.Annealing.MarkovChain;

public class SolutionHybrid extends Thread implements Annealing.MarkovChain {
	public Tile[] tiles;
	public static Color[] colors;
	public static Annealing.FireWitness witness = null;
	public int id;
	public int score;
	public int bestScore;

	public SolutionHybrid(Tile[] tiles, int score, int bestScore) {
		this.tiles = tiles;
		this.score = score;
		this.bestScore = bestScore;
	}

	public SolutionHybrid(Tile[] tiles) {
		this.tiles = tiles;
		this.calculateScore();
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

		s.calculateScore();

		return s;
	}

	public void calculateScore() {
		int[][] area = greedy(false);
		int score = 0;
		for(int y = 0; y < Constants.BOARD_HEIGHT; y++) {
			for(int x = 0; x < Constants.BOARD_WIDTH; x++) {
				if(area[x][y] == 0) score++;
			}
		}
		this.score = score;
		if(bestScore > score) {
			bestScore = score;
		}
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
		SolutionHybrid s = new SolutionHybrid(tilesCopy, this.score, this.bestScore);
		return s;
	}

	public static void algorythmHybrid() {
		Tile[] initialTiles = Tile.randomSmash();

		var witness = new Annealing.FireWitness() {
			public SolutionHybrid bestSolution = null;
			public int bestScore = 999;
			public boolean stop = false;

			@Override
			public void afterStart(Annealing.MarkovChain s) {
				initColors((SolutionHybrid)s);
			}

			@Override
			public void afterNewSolution(Annealing.MarkovChain s, int score, double t) {
				if(score < bestScore) {
					setBest((SolutionHybrid)s, score);
					System.out.printf("better solution found: %4d %8.5f \n", bestScore, t);
				}
			}

			@Override
			public void beforeFinish(Annealing.MarkovChain last, Annealing.MarkovChain best) {}	

			public void saveBestSolution() {
				int[][] area = (bestSolution).greedy(true);
				PNGMaker.make(area, 20, "bestAnnealingGreedy.png", colors);
			}

			public synchronized void setBest(SolutionHybrid newSolution, int newScore) {
				bestScore = newScore;
				bestSolution = newSolution.copy();
				if(bestScore == 0) stop = true;
			}

			public synchronized boolean checkStop() {
				return stop;
			}

			@Override
			public boolean checkJump(Annealing.MarkovChain chain, Annealing.MarkovChain bestChain, int bestScore) {
				if(bestScore > this.bestScore) {
					chain.jump(bestScore, (SolutionHybrid)bestChain);
				}
				return false;
			}
		};

		SolutionHybrid.witness = witness;
		
		SolutionHybrid initialSolution = new SolutionHybrid(initialTiles);
		SolutionHybrid[] sh = Stream.generate(() -> initialSolution.copy())
			.limit(Constants.PARALLEL)
			.toArray(SolutionHybrid[]::new); 
				
		Arrays.stream(sh).forEach(SolutionHybrid::start);
		
		try {
			Arrays.stream(sh).forEach(t -> {
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}); 
		} catch (Exception e) {
			e.printStackTrace();
		}

		witness.saveBestSolution();
		System.out.println("finished");
	}

	public void run() {
		try {
			Annealing.fire(this, witness, 0.5, 10000);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void jump(int bestScore, MarkovChain bestSolution) {
		if(this.bestScore > bestScore) {
			tiles = Arrays.stream(((SolutionHybrid)bestSolution).tiles)
				.map(obj -> ((Tile)obj).copy())
				.toArray(Tile[]::new);
			this.bestScore = bestScore;
		}
	}
	
}
