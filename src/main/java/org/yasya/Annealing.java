package org.yasya;

public class Annealing {
	interface FireWitness {
		void afterStart(MarkovChain s);
		void afterNewSolution(MarkovChain s);
		void beforeFinish(MarkovChain last, MarkovChain best);
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
				if(witness != null) witness.afterNewSolution(chain);
			}
			else {
				double p = Math.exp(-(double)(newScore - score) / t);
				if(App.random.nextDouble() < p) {
					score = newScore;
					chain = newChain;
					if(witness != null) witness.afterNewSolution(chain);
				}
			}
			t -= initialT / STEP_COUNT;
		}
		if(witness != null) witness.beforeFinish(chain, bestChain);
		return bestScore;
	}
}
