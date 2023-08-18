package org.yasya;

import java.util.Arrays;
import java.util.stream.Stream;
import javax.swing.SwingWorker;

public class Salesman extends SwingWorker<Void, Integer> {
	public double[][] towns;
	public double bestScore = 9999999;
	public Solution bestSolution = null;
	private long startTime = System.currentTimeMillis();

	public Salesman() {
		towns = new double[Constants.SALESMAN_TOWNS_COUNT][2];
		for(int i = 0; i < Constants.SALESMAN_TOWNS_COUNT; i++) {
			towns[i][0] = Utils.random.nextDouble() * 300;
			towns[i][1] = Utils.random.nextDouble() * 300;
		}
	}
	
	public class Solution implements Chainable, Runnable {
		public int[] path;
		public double score;

		public Solution(int[] path, double score) {
			this.path = path;
			this.score = score;
		}

		public Solution() {
			this.path = Utils.randomPermutation(Constants.SALESMAN_TOWNS_COUNT);
			this.calculateScore();
		}

		@Override
		public void afterBetterSolutionFound(Chainable s, double score, double t) {
			setBest((Solution)s, score, t);
		}

		public void calculateScore() {
			double score = Utils.distance(towns[0][0], towns[0][1], towns[Constants.SALESMAN_TOWNS_COUNT - 1][0], towns[Constants.SALESMAN_TOWNS_COUNT - 1][1]);
			for(int i = 0; i < Constants.SALESMAN_TOWNS_COUNT - 1; i++) {
				score += Utils.distance(towns[i][0], towns[i][1], towns[i + 1][0], towns[i + 1][1]);
			}
			this.score = score;
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
				i1 = Utils.random.nextInt(Constants.SALESMAN_TOWNS_COUNT);
				i2 = Utils.random.nextInt(Constants.SALESMAN_TOWNS_COUNT);
			} while(i1 == i2);

			int temp = s.path[(i1 + 1) % Constants.SALESMAN_TOWNS_COUNT];
			s.path[(i1 + 1) % Constants.SALESMAN_TOWNS_COUNT] = s.path[(i2 + 1) % Constants.SALESMAN_TOWNS_COUNT];
			s.path[(i2 + 1) % Constants.SALESMAN_TOWNS_COUNT] = s.path[i2];
			s.path[i2] = temp;
			
			s.calculateScore();

			return s;
		}

		public Solution copy() {
			int[] pathCopy = Arrays.copyOf(path, path.length);
			Solution s = new Solution(pathCopy, this.score);
			return s;
		}

		public void run() {
			try {
				System.out.println("Salesman fire");
				Utils.fire(this, Constants.SALESMAN_INITIAL_T, Constants.SALESMAN_STEP_COUNT);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public synchronized void onProgress(int progress) {
			publish(progress);
		}

	}
	
	public void saveBestSolution() {
		SalesmanPNG.saveArea(towns, bestSolution.path, "area.png");
	}

	public synchronized void setBest(Solution newSolution, double newScore, double t) {
		if(newScore < bestScore) {
			bestScore = newScore;
			bestSolution = newSolution.copy();
			System.out.printf("better solution found: %8.1f %8.5f \n", bestScore, t);
			saveBestSolution();
			UI.areaIcon.getImage().flush();
			UI.areaLabel.repaint();
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		publish(0);
		Solution initialSolution = new Solution();
		Thread[] sh = Stream.generate(() -> new Thread(initialSolution.copy()))
			.limit(Constants.SALESMAN_PARALLEL)
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
		UI.areaIcon.getImage().flush();
		UI.areaLabel.repaint();
		long duration = System.currentTimeMillis() - startTime;
		System.out.printf("best score: %f, duration: %d ms", bestScore, duration);
	}

}

