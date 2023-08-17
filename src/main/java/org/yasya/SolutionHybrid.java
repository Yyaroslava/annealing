package org.yasya;

import org.yasya.Annealing.MarkovChain;
import java.awt.Color;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.stream.Stream;
import javax.swing.SwingWorker;

public class SolutionHybrid extends SwingWorker<Void, Integer> implements Annealing.MarkovChain {
	public static Color[] colors;
	public Tile[] tiles;
	public int id;
	public int score;
	public int bestScore = 999;
	public SolutionHybrid bestSolution = null;
	public boolean stop = false;
	private long startTime = System.currentTimeMillis();
	public TreeMap<Integer, TreeMap<Integer, int[]>> statistic = new TreeMap<>();

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
			int[] c = Utils.smash(240);
			colors[i] = new Color(c[0], c[1], c[2]);
		}
	}

	public int intersect(int[][] area, Tile tile, int deltaX, int deltaY, int bestResult) {
		int result = 0;
		for(int y = 0; y < tile.height; y++) {
			for(int x = 0; x < tile.width; x++) { 
				if(tile.area[x][y] > 0 && area[x + deltaX][y + deltaY] > 0) {
					result++;
					if(result >= bestResult) return result;
				}
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
					int currentIntersect = intersect(area, tile, x, y, bestIntersect);
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
		} while(i1 == i2 || s.tiles[i1].code == s.tiles[i2].code);
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

		SolutionHybrid initialSolution = new SolutionHybrid(initialTiles);
		Thread[] sh = Stream.generate(() -> new Thread(initialSolution.copy()))
			.limit(Constants.PARALLEL)
			.toArray(Thread[]::new);
				
		Arrays.stream(sh).forEach(Thread::start);
		
		
		Arrays.stream(sh).forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}); 
		
		System.out.println("finished");
	}

	public void start() {
		try {
			Annealing.fire(this, Constants.HYBRID_INITIAL_T, Constants.HYBRID_STEP_COUNT);
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

	@Override
	public void afterStart(Annealing.MarkovChain s) {
		colors = Utils.getPalette(((SolutionHybrid)s).tiles.length);
	}

	@Override
	public void afterNewSolution(Annealing.MarkovChain s, int score, double t) {
		if(score < bestScore) {
			setBest((SolutionHybrid)s, score);
			System.out.printf("better solution found: %4d %8.5f \n", bestScore, t);
			saveBestSolution();
			UI.areaIcon.getImage().flush();
			UI.areaLabel.repaint();
		}
	}
	
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
	public void onProgress(int progress) {
		publish(progress);
	}

	@Override
	public void addStatistic(int oldScore, int newScore, boolean moved) {
		if(!statistic.containsKey(oldScore)) {
			statistic.put(oldScore, new TreeMap<>());
		}
		var oldScoreStatistic = statistic.get(oldScore);
		if(!oldScoreStatistic.containsKey(newScore)) {
			oldScoreStatistic.put(newScore, new int[3]);
		}
		var moveStatistic = oldScoreStatistic.get(newScore);
		if(moved) {
			moveStatistic[0]++;
			moveStatistic[2]++;
		}
		else {
			moveStatistic[1]++;
			moveStatistic[2]++;
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		publish(0);
		SolutionHybrid.algorythmHybrid();
		publish(100);

		return null;
	}

	@Override
	protected void process(java.util.List<Integer> chunks) {
		int latestProgress = chunks.get(chunks.size() - 1);
		UI.progressBar.setValue(latestProgress);
	}

	@Override
	protected void done() {
		UI.areaIcon.getImage().flush();
		UI.areaLabel.repaint();
		for(Integer oldScore : statistic.keySet()) {
			var oldScoreStatistic = statistic.get(oldScore);
			System.out.printf("%3d <- ", oldScore);
			for(Integer from : statistic.keySet()) {
				var fromStatistic = statistic.get(from);
				if(fromStatistic.containsKey(oldScore)) {
					var fromToStatistic = fromStatistic.get(oldScore);
					System.out.printf("%d:%d, ", from, fromToStatistic[0], fromToStatistic[1]);
				}
			}
			System.out.println();

			for(Integer newScore : oldScoreStatistic.keySet()) {
				var moveStatistic = oldScoreStatistic.get(newScore);
				System.out.printf("%3d -> %3d %8d %8d %8d \n", oldScore, newScore, moveStatistic[0], moveStatistic[1], moveStatistic[2]);
			}
		}
		long duration = System.currentTimeMillis() - startTime;
		System.out.printf("duration: %d ms", duration);
	}

}
