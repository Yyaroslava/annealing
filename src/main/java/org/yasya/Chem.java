package org.yasya;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import org.jfree.chart.JFreeChart;

public class Chem extends SwingWorker<Void, Integer> {
	public Atom[] atoms;
	public int[][] links;
	public double[][] startCoordinates;
	public Color[] colors;
	public double bestScore = 999999999;
	public Solution bestSolution = null;
	public boolean stop = false;
	private long startTime = System.currentTimeMillis();
	public double[] history = new double[Config.HISTORY_COUNT];
	public int historyIndex = 0;
	public static String description = 
		"""
		The task is to find the dimensional arrangement of atoms within a molecule that minimizes the binding energy.

		The solution search is performed using a simulated annealing algorythm.
		""";

	public Chem() {
		atoms = new Atom[]{
			Atom.C, Atom.C, Atom.C, Atom.C, Atom.C, Atom.C, Atom.C,
			Atom.H, Atom.H, Atom.H, Atom.H, Atom.H,
			Atom.N, Atom.N, Atom.N,
			Atom.O, Atom.O, Atom.O, Atom.O, Atom.O, Atom.O
		};
		links = new int[][]{
		//	 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20
			{0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 2, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 2, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0},
			{0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0},
			{0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0}
		};
		startCoordinates = new double[atoms.length][3];
		for(int i = 0; i < atoms.length; i++){
			startCoordinates[i][0] = Utils.random.nextDouble() * 20;
			startCoordinates[i][1] = Utils.random.nextDouble() * 20;
			startCoordinates[i][2] = Utils.random.nextDouble() * 20;
		}
	}

	public class Config {
		public static final int PARALLEL = 14;
		public static final double INITIAL_T = 0.27;
		public static final int HISTORY_COUNT = 100000;
		public static final int[][] PALETTE = new int[][] {
			{0, 128, 0},
			{0, 0, 255},
			{75, 0, 130},
			{238, 130, 238}
		};
	}
	
	public enum Atom {
		H,
		C,
		N,
		O
	}

	synchronized public void addHistory(boolean jumped, double newScore) {
		double score = jumped ? newScore : 0;
		history[historyIndex] = score;
		historyIndex = (historyIndex + 1) % history.length;
		if(historyIndex == 0) {
			JFreeChart chart = Utils.updateChart(history);
			UI.setChart(chart);
		}
	}

	public class Solution implements Chainable, Runnable {
		public double[][] coordinates;
		public double score;

		public Solution(double[][] coordinates, double score) {
			this.coordinates = coordinates;
			this.score = score;
		}

		public Solution() {
			this.coordinates = startCoordinates;
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
			double score = 0;
			for(int i = 0; i < atoms.length - 1; i++) {
				for(int j = i + 1; j < atoms.length; j++) {
					score += energy(i, j);
				}
			}
			this.score = score;
		}

		public double energy(int i, int j) {
			double e = 0;
			double distance = Utils.distance3(coordinates[i][0], coordinates[i][1], coordinates[i][2], coordinates[j][0], coordinates[j][1], coordinates[j][2]);
			if(atoms[i] == Atom.C && atoms[j] == Atom.C) {
				if(links[i][j] == 0) {
					e = 3 / distance;
				}
				else if(links[i][j] == 1) {
					e = 10.0 * (1 - distance) * (1 - distance);
				}
				else if(links[i][j] == 2) {
					e = 20.0 * (0.7 - distance) * (0.7 - distance);
				}
				else if(links[i][j] == 3) {
					e = 30.0 * (0.6 - distance) * (0.6 - distance);
				}
			}
			else if(atoms[i] == Atom.C && atoms[j] == Atom.H || atoms[i] == Atom.H && atoms[j] == Atom.C) {
				if(links[i][j] == 0) {
					e = 3 / distance;
				}
				else if(links[i][j] == 1) {
					e = 10.0 * (1 - distance) * (1 - distance);
				}
			}
			else if(atoms[i] == Atom.C && atoms[j] == Atom.N || atoms[i] == Atom.N && atoms[j] == Atom.C) {
				if(links[i][j] == 0) {
					e = 3 / distance;
				}
				else if(links[i][j] == 1) {
					e = 10.0 * (1 - distance) * (1 - distance);
				}
				else if(links[i][j] == 2) {
					e = 20.0 * (0.7 - distance) * (0.7 - distance);
				}
				else if(links[i][j] == 3) {
					e = 30.0 * (0.6 - distance) * (0.6 - distance);
				}
			}
			else if(atoms[i] == Atom.C && atoms[j] == Atom.O || atoms[i] == Atom.O && atoms[j] == Atom.C) {
				if(links[i][j] == 0) {
					e = 3 / distance;
				}
				else if(links[i][j] == 1) {
					e = 10.0 * (1 - distance) * (1 - distance);
				}
				else if(links[i][j] == 2) {
					e = 20.0 * (0.7 - distance) * (0.7 - distance);
				}
			}

			else if(atoms[i] == Atom.N && atoms[j] == Atom.N) {
				if(links[i][j] == 0) {
					e = 3 / distance;
				}
				else if(links[i][j] == 1) {
					e = 10.0 * (1 - distance) * (1 - distance);
				}
			}
			else if(atoms[i] == Atom.N && atoms[j] == Atom.O || atoms[i] == Atom.O && atoms[j] == Atom.N) {
				if(links[i][j] == 0) {
					e = 3 / distance;
				}
				else if(links[i][j] == 1) {
					e = 10.0 * (1 - distance) * (1 - distance);
				}
				else if(links[i][j] == 2) {
					e = 20.0 * (0.7 - distance) * (0.7 - distance);
				}
			}
			else if(atoms[i] == Atom.N && atoms[j] == Atom.H || atoms[i] == Atom.H && atoms[j] == Atom.N) {
				if(links[i][j] == 0) {
					e = 3 / distance ;
				}
				else if(links[i][j] == 1) {
					e = 10.0 * (1 - distance) * (1 - distance);
				}
			}

			else if(atoms[i] == Atom.O && atoms[j] == Atom.O) {
				if(links[i][j] == 0) {
					e = 3 / distance;
				}
				else if(links[i][j] == 1) {
					e = 10.0 * (1 - distance) * (1 - distance);
				}
				else if(links[i][j] == 2) {
					e = 20.0 * (0.7 - distance) * (0.7 - distance);
				}
			}
			else if(atoms[i] == Atom.O && atoms[j] == Atom.H || atoms[i] == Atom.H && atoms[j] == Atom.O) {
				if(links[i][j] == 0) {
					e = 3 / distance;
				}
				else if(links[i][j] == 1) {
					e = 10.0 * (1 - distance) * (1 - distance);
				}
			}

			else if(atoms[i] == Atom.H && atoms[j] == Atom.H) {
				if(links[i][j] == 0) {
					e = 3 / distance;
				}
				else if(links[i][j] == 1) {
					e = 10.0 * (1 - distance) * (1 - distance);
				}
			}

			return e;
		}

		@Override
		public double score() {
			return score;
		}

		@Override
		public Chainable next() {
			Solution s = copy();
			int i = Utils.random.nextInt(atoms.length);
			s.coordinates[i][0] = Utils.random.nextDouble() * 20;
			s.coordinates[i][1] = Utils.random.nextDouble() * 20;
			s.coordinates[i][2] = Utils.random.nextDouble() * 20;
			s.calculateScore();

			return s;
		}

		public Solution copy() {
			double[][] coordinatesCopy = new double[atoms.length][3];
			for(int i = 0; i < atoms.length; i++) {
				coordinatesCopy[i][0] = coordinates[i][0];
				coordinatesCopy[i][1] = coordinates[i][1];
				coordinatesCopy[i][2] = coordinates[i][2];
			}
			Solution s = new Solution(coordinatesCopy, this.score);
			
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

		public synchronized void handleEvents(Object[] params) {
			params[0] = stop || UI.checkStop();
			params[1] = UI.currentTemperature;
			if(UI.save) {
				UI.save = false;
				save();
			}
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
			BufferedImage image = ChemPNG.getAreaImage(400, 400, atoms, links, bestSolution.coordinates);
			UI.setAreaImage(image);
			UI.setScoreLabel(bestScore);
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		UI.currentTemperature = Config.INITIAL_T;
		UI.temperatureLabel.setText("t = " + Double.toString(Config.INITIAL_T));
		UI.setDescriptionLabel(description);
		publish(0);
		Solution initialSolution = new Solution();
		Thread[] sh = Stream.generate(() -> new Thread(initialSolution.copy()))
			.limit(Config.PARALLEL)
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

	public void save() {
		BufferedImage image = ChemPNG.getAreaImage(800, 800, atoms, links, bestSolution.coordinates);
		File outputFile = new File("Chem.png");
		try {
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void done() {
		long duration = System.currentTimeMillis() - startTime;
		System.out.printf("best score: %f, duration: %d ms", bestScore, duration);
		save();
	}

}

