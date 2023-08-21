package org.yasya;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import javax.swing.SwingWorker;

public class Syllabus extends SwingWorker<Void, Integer> {
	public double bestScore = 9999998;
	public Solution bestSolution = null;
	private long startTime = System.currentTimeMillis();

	public record Row(String group, String course, String teacher){}

	public Syllabus(){}
	
	public class Solution implements Chainable, Runnable {
		public Row[][][] rows;
		public double score;
		public ThreadLocalRandom localRandom;

		public Solution(Row[][][] rows, double score) {
			this.rows = new Row[Constants.SYLLABUS_DAYS.length][Constants.SYLLABUS_MAX_LESSONS_COUNT][Constants.SYLLABUS_ROOMS.length];
			for(int day = 0; day < Constants.SYLLABUS_DAYS.length; day++) {
				for(int time = 0; time < Constants.SYLLABUS_MAX_LESSONS_COUNT; time++) {
					for(int room = 0; room < Constants.SYLLABUS_ROOMS.length; room++) {
						this.rows[day][time][room] = rows[day][time][room];
					}
				}
			}
			this.score = score;
			this.localRandom = ThreadLocalRandom.current();
		}

		public Solution() {
			this.rows = new Row[Constants.SYLLABUS_DAYS.length][Constants.SYLLABUS_MAX_LESSONS_COUNT][Constants.SYLLABUS_ROOMS.length];
			//TODO fiil random syllabus

			this.calculateScore();
		}

		@Override
		public void afterBetterSolutionFound(Chainable s, double score, double t) {
			setBest((Solution)s, score, t);
		}

		public void calculateScore() {
			//TODO
			//this.score = score;
		}

		@Override
		public Chainable next() {
			Solution s = copy();
			//TODO
			s.calculateScore();
			return s;
		}

		@Override
		public double score() {
			return score;
		}

		public Solution copy() {
			//TODO
			//return s;
		}

		public void run() {
			try {
				Utils.fire(this, Constants.SYLLABUS_INITIAL_T, Constants.SYLLABUS_STEP_COUNT);
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
		SyllabusPNG.saveArea(bestSolution.rows, "area.png");
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
