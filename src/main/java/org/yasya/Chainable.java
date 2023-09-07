package org.yasya;

interface Chainable {
	double score();
	Chainable next();
	default void afterStart(Chainable s) {};
	default void afterBetterSolutionFound(Chainable s, double score, double t) {};
	default void afterJump(boolean jumped, double newScore) {};
	default void addStatistic(double oldScore, double newScore, boolean moved) {};
	default void handleEvents(Object[] params) {};
	default void beforeFinish(Chainable last, Chainable best) {};
	default void onProgress(int progress) {};
}
