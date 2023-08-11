package org.yasya;

public class Annealing {

	interface FireWitness {
		default void afterStart(MarkovChain s) {};
		default void afterNewSolution(MarkovChain s, int score, double t) {};
		default void beforeFinish(MarkovChain last, MarkovChain best) {};
		default boolean checkStop() { return false; };
		default boolean checkJump(MarkovChain chain, MarkovChain bestChain, int bestScore) { return false; };
		default void onProgress(int progress) {};
		default void addStatistic(int oldScore, int newScore, boolean moved) {};
	}

	interface MarkovChain {
		int score();
		void jump(int bestScore, MarkovChain bestSolution);
		MarkovChain next();
	}
	
	public static int fire(MarkovChain chain, FireWitness witness, double initialT, int STEP_COUNT) {
		if(witness != null) witness.afterStart(chain);
		int score = chain.score();
		double t = initialT;
		int bestScore = 9999;
		MarkovChain bestChain = null;
		for(int i = 0; i < STEP_COUNT; i++) {
			MarkovChain newChain = chain.next();
			int newScore = newChain.score();
			boolean moved = false;
			int oldScore = score;
			if(newScore <= score) {
				score = newScore;
				chain = newChain;
				moved = true;
				if(bestScore > score) {
					bestScore = score;
					bestChain = chain;
				}
				if(witness != null) witness.afterNewSolution(chain, bestScore, t);
			}
			else {
				double p = Math.exp(-(double)(newScore - score) / t);
				if(App.random.nextDouble() < p) {
					score = newScore;
					chain = newChain;
					moved = true;
					if(witness != null) witness.afterNewSolution(chain, bestScore, t);
				}
			}
			if(witness != null) {
				witness.addStatistic(oldScore, newScore, moved);
			}
			switch (score) {
				case 1:
					t = 0.2;
					break;
				case 2:
					t = 0.2;
					break;
				case 3:
					t = 0.2;
					break;
				case 4:
					t = 0.2;
					break;
				case 5:
					t = 0.2;
					break;
				case 6:
					t = 0.2;
					break;
				case 7:
					t = 0.2;
					break;
				default:
					t = initialT * ((double)(STEP_COUNT - i)) / ((double)STEP_COUNT);
			} 
			
			if(i % 100 == 0) {
				if(witness != null) {
					if(witness.checkStop()) break;
					if(witness.checkJump(chain, bestChain, bestScore)) {}
				} 
			}
			if(witness != null) {
				if(i % (STEP_COUNT / 100) == 0) {
					witness.onProgress(i * 100 / STEP_COUNT);
				}
			}
		}
		if(witness != null) witness.beforeFinish(chain, bestChain);
		return bestScore;
	}
}
