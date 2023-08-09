package org.yasya;

public class Annealing {

	interface FireWitness {
		void afterStart(MarkovChain s);
		void afterNewSolution(MarkovChain s, int score, double t);
		void beforeFinish(MarkovChain last, MarkovChain best);
		boolean checkStop();
		boolean checkJump(MarkovChain chain, MarkovChain bestChain, int bestScore);
		void onProgress(int progress);
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
			if(newScore <= score) {
				score = newScore;
				chain = newChain;
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
					if(witness != null) witness.afterNewSolution(chain, bestScore, t);
				}
			}
			t -= initialT / STEP_COUNT;
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
