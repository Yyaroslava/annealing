package org.yasya;

public class Annealing {
	interface FireWitness {
		void afterStart(MarkovChain s);
		void afterNewSolution(MarkovChain s, int score);
		void beforeFinish(MarkovChain last, MarkovChain best);
		boolean stop();
	}

	interface MarkovChain {
		int score();
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
				if(witness != null) witness.afterNewSolution(chain, bestScore);
			}
			else {
				double p = Math.exp(-(double)(newScore - score) / t);
				if(App.random.nextDouble() < p) {
					score = newScore;
					chain = newChain;
					if(witness != null) witness.afterNewSolution(chain, bestScore);
				}
			}
			t -= initialT / STEP_COUNT;
			if(i % 100 == 0) {
				if(witness != null) {
					if(witness.stop()) break;
				} 
			}
			if(i % 100000 == 0) {
				//System.out.printf("thread %d runs %d \n", chain.hashCode(), i);
			}
		}
		if(witness != null) witness.beforeFinish(chain, bestChain);
		return bestScore;
	}
}
