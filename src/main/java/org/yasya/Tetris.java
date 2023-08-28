package org.yasya;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

public class Tetris extends SwingWorker<Void, Integer> {
	public Tile[] startTiles;
	public Color[] colors;
	public double bestScore = 999;
	public Solution bestSolution = null;
	public boolean stop = false;
	private long startTime = System.currentTimeMillis();
	public double[] history = new double[Constants.TETRIS_HISTORY_COUNT];
	public int historyIndex = 0;

	public Tetris() {
		startTiles = Tile.randomSmash();
		colors = Utils.getPalette(startTiles.length);
	}

	synchronized public void addHistory(boolean jumped, double newScore) {
		if(jumped){
			history[historyIndex] = newScore;
		}
		else{
			history[historyIndex] = 0;
		}
		historyIndex = (historyIndex + 1) % history.length;
		if(historyIndex == 0) {
			Utils.updateChart(history);
		}
	}

	public class Solution implements Chainable, Runnable {
		public Tile[] tiles;
		public double score;

		public Solution(Tile[] tiles, double score) {
			this.tiles = tiles;
			this.score = score;
		}

		public Solution() {
			this.tiles = startTiles;
			this.calculateScore();
		}

		@Override
		public void afterBetterSolutionFound(Chainable s, double score, double t) {
			setBest((Solution)s, score, t);
		}

		@Override
		public void afterJump(boolean jumped, double newScore) {
			addHistory(jumped, newScore);
		}

		public void calculateScore() {
			int[][] area = greedy(false);
			double score = 0;
			for(int y = 0; y < Constants.TETRIS_BOARD_HEIGHT; y++) {
				for(int x = 0; x < Constants.TETRIS_BOARD_WIDTH; x++) {
					if(area[x][y] == 0) score++;
				}
			}
			this.score = score;
		}

		public int[][] greedy(boolean makeColor) {
			int[][] area = new int[Constants.TETRIS_BOARD_WIDTH][Constants.TETRIS_BOARD_HEIGHT];
			for(int i = 0; i < tiles.length; i++) {
				Tile tile = tiles[i];
				int bestX = -1;
				int bestY = -1;
				int bestIntersect = 999;
				positionLoop:
				for(int y = 0; y <= Constants.TETRIS_BOARD_HEIGHT - tile.height; y++) {
					for(int x = 0; x <= Constants.TETRIS_BOARD_WIDTH - tile.width; x++) {
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
		public double score() {
			return score;
		}

		@Override
		public Chainable next() {
			Solution s = copy();
			int i1;
			int i2;
			do {
				i1 = Utils.random.nextInt(s.tiles.length);
				i2 = Utils.random.nextInt(s.tiles.length);
			} while(i1 == i2 || s.tiles[i1].code == s.tiles[i2].code);
			Tile t = s.tiles[i1];
			s.tiles[i1] = s.tiles[i2];
			s.tiles[i2] = t;
			s.calculateScore();

			return s;
		}

		public Solution copy() {
			Tile[] tilesCopy = Arrays.stream(tiles)
				.map(obj -> ((Tile)obj).copy())
				.toArray(Tile[]::new);
			Solution s = new Solution(tilesCopy, this.score);
			return s;
		}

		public void run() {
			try {
				Utils.fire(this);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		public synchronized boolean checkStop() {
			return stop;
		}

		@Override
		public synchronized void onProgress(int progress) {
			publish(progress);
		}

	}

	public synchronized void setBest(Solution newSolution, double newScore, double t) {
		if(newScore < bestScore) {
			bestScore = newScore;
			bestSolution = newSolution.copy();
			if(bestScore == 0) stop = true;
			System.out.printf("better solution found: %6.1f %8.5f \n", bestScore, t);
			int[][] area = (bestSolution).greedy(true);
			BufferedImage image = TetrisPNG.getAreaImage(340, 400, area, colors);
			UI.areaIcon.setImage(image);
			UI.scoreLabel.setText(String.format("better solution found: %6.1f", bestScore));
			UI.areaLabel.repaint();
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		UI.currentTemperature = Constants.TETRIS_INITIAL_T;
		UI.temperatureLabel.setText("t = " + Double.toString(Constants.TETRIS_INITIAL_T));
		publish(0);
		Solution initialSolution = new Solution();
		Thread[] sh = Stream.generate(() -> new Thread(initialSolution.copy()))
			.limit(Constants.TETRIS_PARALLEL)
			.toArray(Thread[]::new);

		Arrays.stream(sh).forEach(Thread::start);
		Arrays.stream(sh).forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}); 
		
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
		long duration = System.currentTimeMillis() - startTime;
		System.out.printf("best score: %f, duration: %d ms", bestScore, duration);
		int[][] area = (bestSolution).greedy(true);
		BufferedImage image = TetrisPNG.getAreaImage(800, 800, area, colors);
		File outputFile = new File("Tetris.png");
		try {
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
