package org.yasya;

public class Annealing {

	interface MarkovChain {
		int score();
		void jump(int bestScore, MarkovChain bestSolution);
		MarkovChain next();
		default void afterStart(MarkovChain s) {};
		default void afterNewSolution(MarkovChain s, int score, double t) {};
		default void beforeFinish(MarkovChain last, MarkovChain best) {};
		default boolean checkStop() { return false; };
		default void onProgress(int progress) {};
		default void addStatistic(int oldScore, int newScore, boolean moved) {};
	}
	
	public static int fire(MarkovChain chain, double initialT, int STEP_COUNT) {
		chain.afterStart(chain);
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
				chain.afterNewSolution(chain, bestScore, t);
			}
			else {
				double p = Math.exp(-(double)(newScore - score) / t);
				if(App.random.nextDouble() < p) {
					score = newScore;
					chain = newChain;
					moved = true;
					chain.afterNewSolution(chain, bestScore, t);
				}
			}
		
			chain.addStatistic(oldScore, newScore, moved);
			
			t = initialT * ((double)(STEP_COUNT - i)) / ((double)STEP_COUNT);
			
			if(i % 100 == 0) {
				if(chain.checkStop()) break;
			}
			if(i % (STEP_COUNT / 100) == 0) {
				chain.onProgress(i * 100 / STEP_COUNT);
			}
		}
		chain.beforeFinish(chain, bestChain);
		return bestScore;
	}
}
